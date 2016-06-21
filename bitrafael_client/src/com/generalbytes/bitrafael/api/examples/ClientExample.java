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

package com.generalbytes.bitrafael.api.examples;

import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.TxFeesInfo;
import com.generalbytes.bitrafael.api.dto.TxInfo;

import java.math.BigDecimal;

public class ClientExample {
    public static void main(String[] args) {
        IClient c = new Client("https://coin.cz");

        final TxFeesInfo fees = c.getRecommendedTransactionFeesPerByte();
        System.out.println("Recommended transaction fees per byte: " + c.getRecommendedTransactionFeesPerByte());

        //test currency conversion related functions
        BigDecimal amount = c.convertAmount(BigDecimal.ONE, "USD", "BTC");
        System.out.println("1 USD = " + amount + " BTC");
        amount = c.convertAmount(BigDecimal.ONE, "BTC", "USD");
        System.out.println("1 BTC = " + amount + " USD");

        amount = c.convertAmount(BigDecimal.ONE, "mBTC", "BTC");
        System.out.println("1 mBTC = " + amount + " BTC");
        amount = c.convertAmount(BigDecimal.ONE, "BTC", "mBTC");
        System.out.println("1 BTC = " + amount + " mBTC");

        BigDecimal b1 = c.getAddressBalance("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        BigDecimal b2 = c.getAddressBalanceConfirmed("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        System.out.println("balances = " + b1 + " " + b2);
        //following line will always cause error as the private key is not set
        String txHash = c.send("5JFL....private_key_for_1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN", new BigDecimal("0.0002"), "34ZzYw5xB8JTFcECJrFo12sCEGK9St11bU");
        System.out.println("txHash = " + txHash);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (txHash== null) {
            txHash = "edcfc87234dd501f244791e1b112210914a36d6f3d820e7d122e5db1903001c0";
        }
        final long transactionConfirmations = c.getTransactionConfirmations(txHash);
        System.out.println("transactionConfirmations = " + transactionConfirmations);
        final long transactionHeight = c.getTransactionHeight(txHash);
        System.out.println("transactionHeight = " + transactionHeight);
        final long currentBlockchainHeight = c.getCurrentBlockchainHeight();
        System.out.println("currentBlockchainHeight = " + currentBlockchainHeight);
        final TxInfo txinfo = c.getAddressLastTransactionInfo("17oM8y8YEARHHpi6TmoXjLEcF5VMmdGqrR");
        System.out.println("txinfo = " + txinfo);
    }
}
