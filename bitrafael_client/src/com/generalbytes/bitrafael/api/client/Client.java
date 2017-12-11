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


package com.generalbytes.bitrafael.api.client;

import com.generalbytes.bitrafael.api.IBitrafaelAPI;
import com.generalbytes.bitrafael.api.IBitrafaelBitcoinAPI;
import com.generalbytes.bitrafael.api.IBitrafaelDashAPI;
import com.generalbytes.bitrafael.api.IBitrafaelLitecoinAPI;
import com.generalbytes.bitrafael.api.dto.*;
import com.generalbytes.bitrafael.api.dto.rest.*;


import com.generalbytes.bitrafael.api.wallet.ISignature;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.api.wallet.WalletTools;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Utils;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Client implements IClient {
    private String server;
    private String cryptoCurrency;
    private IBitrafaelAPI api;
    private IWalletTools walletTools;
    private static final BigDecimal ONE_BTC_IN_SATOSHIS = new BigDecimal("100000000");

    public Client() {
        this("https://coin.cz");
    }

    public Client(String server) {
        this(server, IClient.BTC);
    }

    public Client(String server, String cryptoCurrency) {
        this.server = server;
        this.cryptoCurrency = cryptoCurrency;
        walletTools = new WalletTools();
        if (BTC.equalsIgnoreCase(cryptoCurrency)) {
            api = RestProxyFactory.createProxy(IBitrafaelBitcoinAPI.class, server + "/api");
        }else if (LTC.equalsIgnoreCase(cryptoCurrency)) {
            api = RestProxyFactory.createProxy(IBitrafaelLitecoinAPI.class, server + "/api");
        }else if (DASH.equalsIgnoreCase(cryptoCurrency)) {
            api = RestProxyFactory.createProxy(IBitrafaelDashAPI.class, server + "/api");
        }
    }

    public static BigDecimal satoshisToBigDecimal(long satoshis) {
        return new BigDecimal(satoshis).divide(ONE_BTC_IN_SATOSHIS);
    }

    public static long bigDecimalToSatoshis(BigDecimal amount) {
        if (amount.compareTo(FEE_LOW) == 0) {
            return -1;
        }else if (amount.compareTo(FEE_MEDIUM) == 0) {
            return -2;
        }else if (amount.compareTo(FEE_HIGH) == 0) {
            return -3;
        }
        return amount.multiply(ONE_BTC_IN_SATOSHIS).longValueExact();
    }

    public static BigDecimal calculateMiningFee(TxInfo txInfo) {
        final List<InputInfo> inputInfos = txInfo.getInputInfos();
        final List<OutputInfo> outputInfos = txInfo.getOutputInfos();
        long input  = 0;
        long output = 0;
        for (int i = 0; i < inputInfos.size(); i++) {
            InputInfo inputInfo = inputInfos.get(i);
            input += inputInfo.getValue();
        }

        for (int i = 0; i < outputInfos.size(); i++) {
            OutputInfo outputInfo = outputInfos.get(i);
            output+=outputInfo.getValue();
        }
        return satoshisToBigDecimal(input - output);
    }



    @Override
    public BigDecimal getAddressBalance(String address) {
        try {
            final AddressBalanceResponse addressBalance = api.getAddressBalance(address);
            if (addressBalance != null && addressBalance.isSuccess() && addressBalance.getData() != null) {
                return satoshisToBigDecimal(addressBalance.getData().getTotal());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public AccountBalance getAccountBalance(String xpub) {
        try {
            final AccountBalanceResponse accountBalance = api.getAccountBalance(xpub);
            if (accountBalance != null && accountBalance.isSuccess() && accountBalance.getData() != null) {
                return accountBalance.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getAddressBalanceConfirmed(String address) {
        try {
            final AddressBalanceResponse addressBalance = api.getAddressBalance(address);
            if (addressBalance != null && addressBalance.isSuccess() && addressBalance.getData() != null) {
                return satoshisToBigDecimal(addressBalance.getData().getTotal());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public long getTransactionHeight(String txHash){
        try {
            final TxInfoResponse response = api.getTransactionInfo(txHash);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData().getBlockHeight();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1;
    }

    @Override
    public long getTransactionConfirmations(String txHash){
        try {
            final TxInfoResponse response = api.getTransactionInfo(txHash);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData().getConfirmations();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1;
    }

    @Override
    public TxFeesInfo getRecommendedTransactionFeesPerByte() {
        try {
            final TxFeesInfoResponse response = api.getTransactionFeesInfo();
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public RiskLevel getTransactionRiskLevel(String txHash) {
        try {
            final TxRiskLevelInfoResponse response = api.getTransactionRiskLevel(txHash);

            if (response != null && response.isSuccess() && response.getData() != null) {
                final TxRiskLevelInfo data = response.getData();
                return RiskLevel.valueOf(data.getRisk());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return RiskLevel.high;
    }

    @Override
    public TxInfo getAddressLastTransactionInfo(String address){
        try {
            final TxInfoResponse response = api.getAddressLastTransactionInfo(address);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, TxInfo> getAddressesLastTransactionInfos(List<String> addresses) {
        try {
            final TxInfosResponse response = api.getAddressesLastTransactionInfos(addresses);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public AddressInfo getAddressInfo(String address, int limit) {
        try {
            final AddressInfoResponse response = api.getAddressInfo(address, limit);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, AddressInfo> getAddressesInfo(List<String> addresses, int limit) {
        try {
            final AddressesInfoResponse response = api.getAddressesInfos(addresses, limit);
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    @Override
    public long getCurrentBlockchainHeight() {
        try {
            final BlockchainHeightResponse response = api.getCurrentBlockchainHeight();
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return -1;
    }


    @Override
    public String send(String fromPrivateKey, BigDecimal amount, String toAddress) {
        return send(fromPrivateKey, amount, toAddress, null);
    }

    @Override
    public String send(String fromPrivateKey, BigDecimal amount, String toAddress, BigDecimal fee) {
        return send(new String[]{fromPrivateKey}, new BigDecimal[] {amount}, new String[]{toAddress}, new BigDecimal[] {amount}, fee);
    }

    @Override
    public String send(String[] fromPrivateKeys, BigDecimal[] fromAmounts, String[] toAddresses, BigDecimal[] toAmounts, BigDecimal fee) {
        try {
            //build input data for template
            String[] privateKeyAddresses = new String[fromPrivateKeys.length];
            TxTemplateInput[] tinputs = new TxTemplateInput[fromPrivateKeys.length];

            for (int i = 0; i < fromPrivateKeys.length; i++) {
                String fromPrivateKey = fromPrivateKeys[i];
                final String walletAddressFromPrivateKey = walletTools.getWalletAddressFromPrivateKey(fromPrivateKey, cryptoCurrency);
                privateKeyAddresses[i] = walletAddressFromPrivateKey;
                final TxTemplateInput ti = new TxTemplateInput(walletAddressFromPrivateKey);
                ti.setAmount(bigDecimalToSatoshis(fromAmounts[i]));
                tinputs[i] = ti;
            }

            TxTemplateOutput[] toutputs = new TxTemplateOutput[toAddresses.length];
            for (int i = 0; i < toAddresses.length; i++) {
                String toAddress = toAddresses[i];
                toutputs[i] = new TxTemplateOutput(toAddress, bigDecimalToSatoshis(toAmounts[i]));
            }

            //build template
            final TxTemplateResponse txTemplateResponse = api.buildTransactionTemplate(new TxTemplateRequest(
                    tinputs,
                    toutputs,
                    tinputs[0].getAddress(),
                    (fee == null ? bigDecimalToSatoshis(IClient.FEE_LOW) : bigDecimalToSatoshis(fee))));


            if (txTemplateResponse != null && txTemplateResponse.isSuccess() && txTemplateResponse.getData() != null) {
                //sign template
                final TxTemplateInput[] inputs = txTemplateResponse.getData().getInputs();
                for (int i = 0; i < inputs.length; i++) {
                    TxTemplateInput input = inputs[i];
                    final TxSignature signature = input.getSignature();
                    for (int j = 0; j < privateKeyAddresses.length; j++) {
                        String address = privateKeyAddresses[j];
                        if (address.equals(signature.getAddress())) {
                            //sign input with this key
                            ISignature sig = walletTools.sign(fromPrivateKeys[j], Utils.HEX.decode(signature.getHashToSign()), cryptoCurrency);
                            signature.setPublicKey(Utils.HEX.encode(sig.getPublicKey()));
                            signature.setSignature(Utils.HEX.encode(sig.getSignature()));
                        }
                    }
                }
                //send signed transaction template to be validated and broadcasted
                final TxReceiptResponse txReceiptResponse = api.sendTransaction(txTemplateResponse.getData(), true);
                if (txReceiptResponse != null && txReceiptResponse.isSuccess()) {
                    return txReceiptResponse.getData().getTxHash();
                }
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    @Override
    public BigDecimal convertAmount(BigDecimal fromAmount, String fromCurrency, String toCurrency) {
        final ArrayList<AmountsPair> amountsPairs = new ArrayList<AmountsPair>();
        amountsPairs.add(new AmountsPair(fromAmount,fromCurrency,null,toCurrency));
        final List<AmountsPair> result = convertAmounts(amountsPairs);
        if (result != null) {
            return result.get(0).getToAmount();
        }
        return null;
    }

    @Override
    public List<AmountsPair> convertAmounts(List<AmountsPair> amountsPairs) {
        try {
            final ConvertAmountsResponse res = api.convertAmounts(amountsPairs);
            if (res.isSuccess()) {
                return res.getData();
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    public static final String formatAmount(long amountInSatoshis, String currency, long time) {
        if ( currency == null || IClient.BTC.equalsIgnoreCase(currency) || IClient.LTC.equalsIgnoreCase(currency)) {
            if (amountInSatoshis == 0) {
                return "0 " + currency;
            }
            return new BigDecimal(Coin.valueOf(amountInSatoshis).toPlainString()).stripTrailingZeros().toPlainString() + " " + currency;
        } else if (("m"+ IClient.BTC).equalsIgnoreCase(currency) || ("m"+ IClient.LTC).equalsIgnoreCase(currency)) {
            if (amountInSatoshis == 0) {
                return "0 " + currency;
            }
            return new BigDecimal(Coin.valueOf(amountInSatoshis).toPlainString()).multiply(new BigDecimal("1000")).stripTrailingZeros().toPlainString() + " " + currency;
        }
        return null;
    }

}
