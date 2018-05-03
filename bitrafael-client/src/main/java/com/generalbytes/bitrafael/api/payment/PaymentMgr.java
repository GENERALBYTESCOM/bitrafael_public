/*************************************************************************************
 * Copyright (C) 2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.bitrafael.api.payment;

import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.AmountsPair;
import com.generalbytes.bitrafael.api.dto.OutputInfo;
import com.generalbytes.bitrafael.api.dto.TxInfo;
import com.generalbytes.bitrafael.api.wallet.WalletTools;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherTransactionListener;
import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherWalletListener;
import com.generalbytes.bitrafael.api.watch.BlockchainWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class PaymentMgr implements IPaymentMgr{

    private static final PaymentMgr instance = new PaymentMgr();
    private Map<String,IClient> clients;

    private final Object INDEXES_LOCK = new Object();

    private final Object PAYMENTS_LOCK = new Object();
    private List<Payment> watchedPayments = new ArrayList<Payment>();
    private BlockchainWatcher watcher;
    private static final long STOP_WATCH_EXPIRATION_SINCE_PAYMENT_REQUEST_CREATED = TimeUnit.HOURS.toSeconds(12);

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private PaymentMgr() {
        clients = new HashMap<String,IClient>();
        clients.put(IClient.BTC, new Client(System.getProperty("coin.cz", "https://coin.cz"), IClient.BTC));
        clients.put(IClient.LTC, new Client(System.getProperty("coin.cz", "https://coin.cz"), IClient.LTC));

        watcher = new BlockchainWatcher();
        watcher.start();
    }

    private void stop() {
        if (service != null) {
            service.shutdownNow();
        }
    }

    private synchronized void start() {
        stop();
        service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("PaymentMgr");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public static PaymentMgr getInstance() {
        return instance;
    }

    public PaymentRequest createPaymentRequest(PaymentRequestSpec spec, IPaymentListener listener) {

        String receivingAddress = getReceivingAddress(spec.getAccountXPUBForReceivingPayment(), IWalletTools.CHAIN_EXTERNAL, spec.getCryptoCurrency());

        final ArrayList<AmountsPair> amountsPairs = new ArrayList<AmountsPair>();
        amountsPairs.add(new AmountsPair(spec.getFiatAmount(),spec.getFiatCurrency(),null, spec.getCryptoCurrency()));
        amountsPairs.add(new AmountsPair(spec.getFiatToleranceAmount(),spec.getFiatCurrency(),null, spec.getCryptoCurrency()));

        final List<AmountsPair> results = clients.get(spec.getCryptoCurrency()).convertAmounts(amountsPairs);

        if (results != null && results.size() == 2) {
            BigDecimal cryptoAmount = results.get(0).getToAmount();
            BigDecimal cryptoToleranceAmount = results.get(1).getToAmount();
            final PaymentRequestInternal paymentRequest = new PaymentRequestInternal(spec, cryptoAmount, cryptoToleranceAmount, spec.getCryptoCurrency(), receivingAddress,listener);
            //register and start watch following payment request
            final Payment payment = new Payment(System.currentTimeMillis(),paymentRequest, Payment.State.NEW);
            registerPayment(payment);
            return paymentRequest;
        }
        return null;
    }

    private void registerPayment(final Payment payment) {
        synchronized (PAYMENTS_LOCK) {
            watchedPayments.add(payment);
            service.schedule(new Runnable() {
                @Override
                public void run() {
                    if (payment.getState() == Payment.State.NEW) {
                        payment.setFailure(Payment.Failure.NOTHING_ARRIVED_WITHIN_TIMEOUT);
                        payment.setState(Payment.State.FAILED);
                        final IPaymentListener listener = ((PaymentRequestInternal) payment.getRequest()).getListener();
                        if (listener != null) {
                            listener.paymentFailed(payment);
                        }
                        // method "unregisterPayment" isn't used -> if later payment arrives, we want to know it
                    }
                }
            }, payment.getRequest().getSpec().getValidityDurationInSeconds(), TimeUnit.SECONDS);

            service.schedule(new Runnable() {
                @Override
                public void run() {
                    if (payment.getState() == Payment.State.NEW || (payment.getState() == Payment.State.FAILED && payment.getFailure() == Payment.Failure.NOTHING_ARRIVED_WITHIN_TIMEOUT)) {
                        payment.setFailure(Payment.Failure.NOTHING_RECEIVED_WITHIN_TIMEOUT);
                        payment.setState(Payment.State.FAILED);
                        final IPaymentListener listener = ((PaymentRequestInternal) payment.getRequest()).getListener();
                        if (listener != null) {
                            listener.paymentFailed(payment);
                        }
                    } else if (payment.getState() == Payment.State.ARRIVING) {
                        payment.setFailure(Payment.Failure.SOMETHING_ARRIVED_AFTER_TIMEOUT);
                        payment.setState(Payment.State.FAILED);
                        final IPaymentListener listener = ((PaymentRequestInternal) payment.getRequest()).getListener();
                        if (listener != null) {
                            listener.paymentFailed(payment);
                        }
                    }
                    unregisterPayment(payment);
                }
            }, STOP_WATCH_EXPIRATION_SINCE_PAYMENT_REQUEST_CREATED, TimeUnit.SECONDS);

            watcher.addWallet(payment.getRequest().getCryptoAddress(), payment.getRequest().getCryptoCurrency(), new AbstractBlockchainWatcherWalletListener() {
                @Override
                public void walletContainsChanged(String __unused1, String __unused2, Object __unused3, final TxInfo tx) {

                    if (payment.getState() == Payment.State.NEW || (payment.getState()== Payment.State.FAILED && payment.getFailure() == Payment.Failure.NOTHING_ARRIVED_WITHIN_TIMEOUT)) {

                        //check if the amountReceived is correct
                        BigDecimal amountReceived = getAmountForAddress(tx, payment.getRequest().getCryptoAddress());
                        boolean amountOk = false;
                        if (amountReceived.compareTo(payment.getRequest().getCryptoAmount()) == 0) {
                            //exact match
                            amountOk = true;
                        } else if (
                                amountReceived.compareTo(payment.getRequest().getCryptoAmount()) > 0 &&
                                        amountReceived.compareTo(payment.getRequest().getCryptoAmount().add(payment.getRequest().getCryptoToleranceAmount())) <= 0
                                ) {
                            //amountReceived is higher than expected but within a tolerance
                            amountOk = true;
                        } else if (
                                amountReceived.compareTo(payment.getRequest().getCryptoAmount()) < 0 &&
                                        amountReceived.compareTo(payment.getRequest().getCryptoAmount().subtract(payment.getRequest().getCryptoToleranceAmount())) >= 0
                                ) {
                            //amountReceived low higher than expected but within a tolerance
                            amountOk = true;
                        }

                        //check if the transaction didn't arrive late
                        long now = System.currentTimeMillis();
                        long timeDiffInSeconds = (now - payment.getCreated()) / 1000;
                        if (timeDiffInSeconds > payment.getRequest().getSpec().getValidityDurationInSeconds()) {
                            payment.setFailure(Payment.Failure.SOMETHING_ARRIVED_AFTER_TIMEOUT);
                            payment.setState(Payment.State.FAILED);

                        } else if (!amountOk) {
                            payment.setFailure(Payment.Failure.INVALID_AMOUNT_RECEIVED);
                            payment.setState(Payment.State.FAILED);
                        }

                        if (payment.getState() != Payment.State.FAILED) {
                            if (tx.getConfirmations() < payment.getRequest().getSpec().getSafeNumberOfBlockConfirmations()) {
                                //transaction is still unconfirmed
                                payment.setState(Payment.State.ARRIVING);
                            } else {
                                payment.setState(Payment.State.RECEIVED);
                            }
                        }

                        payment.setCryptoAmountReceived(amountReceived);
                        payment.setCryptoAmountMiningFee(Client.calculateMiningFee(tx));
                        payment.setTxId(tx.getTxHash());

                        if (payment.getRequest() instanceof PaymentRequestInternal) {
                            final PaymentRequestInternal req = (PaymentRequestInternal) payment.getRequest();
                            final IPaymentListener listener = req.getListener();
                            if (listener != null) {
                                switch (payment.getState()) {
                                    case FAILED:
                                        listener.paymentFailed(payment);
                                        // stop watching for it
                                        unregisterPayment(payment);
                                        watcher.removeTransaction(payment.getTxId(), payment.getRequest().getCryptoCurrency());
                                        break;

                                    case ARRIVING:
                                        listener.paymentArriving(payment);
                                        //start listening for transaction confirmations
                                        watcher.addTransaction(tx.getTxHash(), payment.getRequest().getCryptoCurrency(), new AbstractBlockchainWatcherTransactionListener() {
                                            private int lastNumberOfConfirmations = 0;

                                            @Override
                                            public void numberOfConfirmationsChanged(String transactionHash, String cryptoCurrency, Object tag, int numberOfConfirmations) {
                                                Payment p = (Payment) tag;
                                                if (numberOfConfirmations > lastNumberOfConfirmations) {
                                                    if (payment.getState() == Payment.State.ARRIVING) {
                                                        if (numberOfConfirmations >= payment.getRequest().getSpec().getSafeNumberOfBlockConfirmations()) {
                                                            payment.setState(Payment.State.RECEIVED);
                                                            listener.paymentReceived(p);
                                                            // stop watching for it
                                                            unregisterPayment(p);
                                                            watcher.removeTransaction(payment.getTxId(), payment.getRequest().getCryptoCurrency());
                                                        }
                                                    }
                                                }
                                                lastNumberOfConfirmations = numberOfConfirmations;
                                            }
                                        }, payment);
                                        break;

                                    case RECEIVED:
                                        listener.paymentReceived(payment);
                                        // stop watching for it
                                        unregisterPayment(payment);
                                        watcher.removeTransaction(payment.getTxId(), payment.getRequest().getCryptoCurrency());
                                        break;
                                }
                            }
                        }
                    } else if (payment.getState() == Payment.State.ARRIVING) {
                        //there was a move on wallet after we already seen the transaction
                        BigDecimal amountReceived = getAmountForAddress(tx, payment.getRequest().getCryptoAddress());
                        if (BigDecimal.ZERO.compareTo(amountReceived) < 0) {
                            //customer probably sent coins to address again
                            payment.setFailure(Payment.Failure.INVALID_AMOUNT_RECEIVED);
                            payment.setState(Payment.State.FAILED);
                            payment.setCryptoAmountReceived(amountReceived);
                            payment.setCryptoAmountMiningFee(Client.calculateMiningFee(tx));
                            payment.setTxId(tx.getTxHash());
                            if (payment.getRequest() instanceof PaymentRequestInternal) {
                                final PaymentRequestInternal req = (PaymentRequestInternal) payment.getRequest();
                                final IPaymentListener listener = req.getListener();
                                if (listener != null) {
                                    listener.paymentFailed(payment);
                                }
                            }
                            // stop watching for it
                            unregisterPayment(payment);
                            watcher.removeTransaction(payment.getTxId(), payment.getRequest().getCryptoCurrency());
                        }
                        // else: somebody probably moved coins from the address

                    } else {
                        // stop watching for it
                        unregisterPayment(payment);
                        watcher.removeTransaction(payment.getTxId(), payment.getRequest().getCryptoCurrency());
                    }
                }
            }, payment);
        }
    }

    private static BigDecimal getAmountForAddress(TxInfo tx, String walletAddress) {
        if (tx == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ZERO;
        final List<OutputInfo> outputInfos = tx.getOutputInfos();
        for (int i = 0; i < outputInfos.size(); i++) {
            OutputInfo outputInfo = outputInfos.get(i);
            if (walletAddress.equals(outputInfo.getAddress())) {
                result = result.add(Client.satoshisToBigDecimal(outputInfo.getValue()));
            }
        }
        return result;
    }

    private void unregisterPayment(Payment payment) {
        synchronized (PAYMENTS_LOCK) {
            watchedPayments.remove(payment);
            watcher.removeWallet(payment.getRequest().getCryptoAddress(),payment.getRequest().getCryptoCurrency());
        }
    }

    private String getReceivingAddress(String xpubAccount, int chainIndex, String cryptoCurrency) {
        synchronized (INDEXES_LOCK) {
            File xpubIndexesFile = new File("./payment_indexes_" + cryptoCurrency.toLowerCase() + ".properties");
            Properties p = new Properties();
            String address = null;
            try {
                if (xpubIndexesFile.exists()) {
                    FileInputStream fis = new FileInputStream(xpubIndexesFile);
                    p.load(fis);
                    fis.close();
                }

                final String chainKey = xpubAccount + "_" + chainIndex;
                int index = Integer.parseInt(p.getProperty(chainKey,"-1"));
                WalletTools wt = new WalletTools();
                index++;
                p.setProperty(chainKey,index +"");

                address = wt.getWalletAddressFromAccountXPUB(xpubAccount, cryptoCurrency, chainIndex, index);

                FileOutputStream fos = new FileOutputStream(xpubIndexesFile);
                p.store(fos,"Bitrafael");
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }
    }
}
