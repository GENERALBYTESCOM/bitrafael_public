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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class PaymentMgr implements IPaymentMgr{
    private static final String BTC = "BTC";

    private static final PaymentMgr instance = new PaymentMgr();
    private File xpubIndexesFile = new File("./payment_indexes.properties");
    private IClient IClient = new Client("https://coin.cz");
    private Object INDEXES_LOCK = new Object();

    private Object PAYMENTS_LOCK = new Object();
    private List<Payment> watchedPayments = new ArrayList<Payment>();
    private BlockchainWatcher watcher = new BlockchainWatcher();
    private static final int EXPIRATION_FOR_REQUIRED_CONFIRMATIONS_IN_SECONDS = 12 * 60 * 60; //12hours

    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private PaymentMgr() {
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
                thread.setName("BlockchainWatcher");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public static PaymentMgr getInstance() {
        return instance;
    }

    public void setXpubIndexesFile(File xpubIndexesFile) {
        this.xpubIndexesFile = xpubIndexesFile;
    }

    public PaymentRequest createPaymentRequest(PaymentRequestSpec spec, IPaymentListener listener) {
        String receivingAddress = getReceivingAddress(spec.getAccountXPUBForReceivingPayment(), IWalletTools.CHAIN_EXTERNAL);

        final ArrayList<AmountsPair> amountsPairs = new ArrayList<>();
        final String cryptoCurrency = BTC;
        amountsPairs.add(new AmountsPair(spec.getFiatAmount(),spec.getFiatCurrency(),null, cryptoCurrency));
        amountsPairs.add(new AmountsPair(spec.getFiatToleranceAmount(),spec.getFiatCurrency(),null, cryptoCurrency));

        final List<AmountsPair> results = IClient.convertAmounts(amountsPairs);

        if (results != null && results.size() == 2) {
            BigDecimal cryptoAmount = results.get(0).getToAmount();
            BigDecimal cryptoToleranceAmount = results.get(1).getToAmount();
            final PaymentRequestInternal paymentRequest = new PaymentRequestInternal(spec, cryptoAmount, cryptoToleranceAmount, cryptoCurrency, receivingAddress,listener);
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
                        unregisterPayment(payment);
                    }
                }
            },payment.getRequest().getSpec().getValidityDurationInSeconds(), TimeUnit.SECONDS);



            watcher.addWallet(payment.getRequest().getCryptoAddress(), new AbstractBlockchainWatcherWalletListener() {
                @Override
                public void walletContainsChanged(String walletAddress, Object tag, final TxInfo tx) {
                    boolean processed = false;
                    Payment p = (Payment) tag;

                    if (p.getState() == Payment.State.NEW) {
                        BigDecimal amountReceived = BigDecimal.ZERO;
                        //check if the amountReceived is correct
                        if (!processed) {
                            amountReceived = getAmountForAddress(tx, walletAddress);
                            boolean amountOk = false;
                            if (amountReceived.compareTo(p.getRequest().getCryptoAmount()) == 0) {
                                //exact match
                                amountOk = true;
                            } else if (
                                    amountReceived.compareTo(p.getRequest().getCryptoAmount()) > 0 &&
                                            amountReceived.compareTo(p.getRequest().getCryptoAmount().add(p.getRequest().getCryptoAmount())) <= 0
                                    ) {
                                //amountReceived is higher than expected but within a tolerance
                                amountOk = true;
                            } else if (
                                    amountReceived.compareTo(p.getRequest().getCryptoAmount()) < 0 &&
                                            amountReceived.compareTo(p.getRequest().getCryptoAmount().subtract(p.getRequest().getCryptoAmount())) >= 0
                                    ) {
                                //amountReceived low higher than expected but within a tolerance
                                amountOk = true;
                            }

                            if (!amountOk) {
                                p.setFailure(Payment.Failure.INVALID_AMOUNT_RECEIVED);
                                p.setState(Payment.State.FAILED);
                                processed = true;
                            }
                        }

                        //check if the transaction didn't arrive late
                        if (!processed) {
                            long now = System.currentTimeMillis();
                            long timeDiffInSeconds = (now - p.getCreated()) / 1000;
                            if (timeDiffInSeconds > payment.getCreated()) {
                                p.setFailure(Payment.Failure.SOMETHING_ARRIVED_AFTER_TIMEOUT);
                                p.setState(Payment.State.FAILED);
                                processed = true;
                            }
                        }


                        if (!processed) {
                            if (tx.getConfirmations() < p.getRequest().getSpec().getSafeNumberOfBlockConfirmations()) {
                                //transaction is still in memory pool
                                p.setState(Payment.State.ARRIVING);
                                processed = true;
                            } else if (tx.getConfirmations() >= p.getRequest().getSpec().getSafeNumberOfBlockConfirmations()) {
                                p.setState(Payment.State.RECEIVED);
                                processed = true;
                            }
                        }

                        if (processed) {
                            p.setCryptoAmountReceived(amountReceived);
                            p.setCryptoAmountMiningFee(Client.calculateMiningFee(tx));
                            p.setTxId(tx.getTxHash());

                            if (payment.getRequest() instanceof PaymentRequestInternal) {
                                final PaymentRequestInternal req = (PaymentRequestInternal) payment.getRequest();
                                final IPaymentListener listener = req.getListener();
                                if (listener != null) {
                                    switch (p.getState()) {
                                        case FAILED:
                                            listener.paymentFailed(p);
                                            unregisterPayment(p);
                                            break;
                                        case ARRIVING:
                                            listener.paymentArriving(p);
                                            //start listening for transaction confirmations
                                            watcher.addTransaction(tx.getTxHash(), new AbstractBlockchainWatcherTransactionListener() {
                                                private int lastNumberOfConfirmations = 0;

                                                @Override
                                                public void numberOfConfirmationsChanged(String transactionHash, Object tag, int numberOfConfirmations) {
                                                    Payment p = (Payment) tag;
                                                    if (numberOfConfirmations > lastNumberOfConfirmations) {
                                                        if (p.getState() == Payment.State.ARRIVING) {
                                                            if (numberOfConfirmations >= p.getRequest().getSpec().getSafeNumberOfBlockConfirmations()) {
                                                                watcher.removeTransaction(transactionHash);
                                                                p.setState(Payment.State.RECEIVED);
                                                                listener.paymentReceived(p);
                                                                //payment was received stop watching for it
                                                                unregisterPayment(p);
                                                            }
                                                        }
                                                    }
                                                    lastNumberOfConfirmations = numberOfConfirmations;

                                                    if (payment.getState() == Payment.State.FAILED) {
                                                        //stop listening if transaction already failed
                                                        watcher.removeTransaction(transactionHash);
                                                    }
                                                }
                                            }, p);
                                            service.schedule(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (payment.getState() == Payment.State.ARRIVING) {
                                                        payment.setFailure(Payment.Failure.NOTHING_RECEIVED_WITHIN_TIMEOUT);
                                                        payment.setState(Payment.State.FAILED);
                                                        final IPaymentListener listener = ((PaymentRequestInternal) payment.getRequest()).getListener();
                                                        if (listener != null) {
                                                            listener.paymentFailed(payment);
                                                        }
                                                        unregisterPayment(payment);
                                                        watcher.removeTransaction(tx.getTxHash());
                                                    }
                                                }
                                            }, EXPIRATION_FOR_REQUIRED_CONFIRMATIONS_IN_SECONDS, TimeUnit.SECONDS);
                                            break;
                                        case RECEIVED:
                                            listener.paymentReceived(p);
                                            //payment was received stop watching for it
                                            unregisterPayment(p);
                                            break;
                                    }
                                }
                            }
                        }
                    } else if (p.getState() == Payment.State.ARRIVING) {
                        //there was a move on wallet after we already seen the transaction
                        BigDecimal amountReceived = getAmountForAddress(tx, walletAddress);
                        if (BigDecimal.ZERO.compareTo(amountReceived) < 0) {
                            //customer probably sent coins to address again
                            p.setFailure(Payment.Failure.INVALID_AMOUNT_RECEIVED);
                            p.setState(Payment.State.FAILED);
                            p.setCryptoAmountReceived(amountReceived);
                            p.setCryptoAmountMiningFee(Client.calculateMiningFee(tx));
                            p.setTxId(tx.getTxHash());
                            if (payment.getRequest() instanceof PaymentRequestInternal) {
                                final PaymentRequestInternal req = (PaymentRequestInternal) payment.getRequest();
                                final IPaymentListener listener = req.getListener();
                                if (listener != null) {
                                    listener.paymentFailed(p);
                                }
                            }
                            unregisterPayment(p);
                        } else {
                            //somebody probably moved coins from the address
                        }
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
            watcher.removeWallet(payment.getRequest().getCryptoAddress());
        }
    }

    private String getReceivingAddress(String xpubAccount, int chainIndex) {
        synchronized (INDEXES_LOCK) {
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

                address = wt.getWalletAddressFromAccountXPUB(xpubAccount, chainIndex, index);

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
