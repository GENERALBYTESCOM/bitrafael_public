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

import com.generalbytes.bitrafael.api.IBitRafaelBitcoinAPI;
import com.generalbytes.bitrafael.api.dto.TxSignature;
import com.generalbytes.bitrafael.api.dto.TxTemplateInput;
import com.generalbytes.bitrafael.api.dto.TxTemplateOutput;
import com.generalbytes.bitrafael.api.dto.rest.AddressBalanceResponse;
import com.generalbytes.bitrafael.api.dto.rest.TxReceiptResponse;
import com.generalbytes.bitrafael.api.dto.rest.TxTemplateRequest;
import com.generalbytes.bitrafael.api.dto.rest.TxTemplateResponse;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;

public class BitRafaelBTCClient {
    private String server;
    private IBitRafaelBitcoinAPI api;
    private static final BigDecimal ONE_BTC_IN_SATOSHIS = new BigDecimal("100000000");
    private static final BigDecimal MINIMUM_FEE = new BigDecimal("0.0001");


    public BitRafaelBTCClient(String server) {
        this.server = server;
        api = RestProxyFactory.createProxy(IBitRafaelBitcoinAPI.class, server + "/api");
    }

    private static BigDecimal satoshisToBigDecimal(long satoshis) {
        return new BigDecimal(satoshis).divide(ONE_BTC_IN_SATOSHIS);
    }

    private static long bigDecimalToSatoshis(BigDecimal amount) {
        return amount.multiply(ONE_BTC_IN_SATOSHIS).longValueExact();
    }

    public BigDecimal getBalance(String address) {
        try {
            final AddressBalanceResponse addressBalance = api.getAddressBalance(address);
            if (addressBalance != null && addressBalance.isSuccess() && addressBalance.getData() != null) {
                return satoshisToBigDecimal(addressBalance.getData().getTotal());
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public BigDecimal getBalanceConfirmed(String address) {
        try {
            final AddressBalanceResponse addressBalance = api.getAddressBalance(address);
            if (addressBalance != null && addressBalance.isSuccess() && addressBalance.getData() != null) {
                return satoshisToBigDecimal(addressBalance.getData().getTotal());
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }



    public String send(String fromPrivateKey, BigDecimal amount, String toAddress) {
        return send(fromPrivateKey, amount, toAddress, null);
    }

    public String send(String fromPrivateKey, BigDecimal amount, String toAddress, BigDecimal fee) {
        return send(new String[]{fromPrivateKey}, new BigDecimal[] {amount}, new String[]{toAddress}, new BigDecimal[] {amount}, fee);
    }

    public String send(String[] fromPrivateKeys, BigDecimal[] fromAmounts, String[] toAddresses, BigDecimal[] toAmounts, BigDecimal fee) {
        try {
            //build input data for template
            DumpedPrivateKey[] dpks = new DumpedPrivateKey[fromPrivateKeys.length];
            TxTemplateInput[] tinputs = new TxTemplateInput[fromPrivateKeys.length];

            for (int i = 0; i < fromPrivateKeys.length; i++) {
                String fromPrivateKey = fromPrivateKeys[i];
                final DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), fromPrivateKey);
                dpks[i] = dp;
                final TxTemplateInput ti = new TxTemplateInput(new Address(MainNetParams.get(), dp.getKey().getPubKeyHash()).toBase58());
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
                    (fee == null ? 0 : bigDecimalToSatoshis(fee))));


            if (txTemplateResponse != null && txTemplateResponse.isSuccess() && txTemplateResponse.getData() != null) {
                //sign template
                final TxTemplateInput[] inputs = txTemplateResponse.getData().getInputs();
                for (int i = 0; i < inputs.length; i++) {
                    TxTemplateInput input = inputs[i];
                    final TxSignature signature = input.getSignature();
                    for (int j = 0; j < dpks.length; j++) {
                        DumpedPrivateKey dp = dpks[j];
                        String address = new Address(MainNetParams.get(), dp.getKey().getPubKeyHash()).toBase58();
                        if (address.equals(signature.getAddress())) {
                            //sign input with this key
                            signature.setPublicKey(dp.getKey().getPublicKeyAsHex());
                            signature.setSignature(Utils.HEX.encode(dp.getKey().sign(Sha256Hash.wrap(signature.getHashToSign())).encodeToDER()));
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
}
