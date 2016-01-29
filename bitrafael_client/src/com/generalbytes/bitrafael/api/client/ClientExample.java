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

import com.generalbytes.bitrafael.api.wallet.MasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.BitRafaelWalletTools;

import java.math.BigDecimal;

public class ClientExample {
    public static void main(String[] args) {
          BitRafaelWalletTools wt = new BitRafaelWalletTools();
        final MasterPrivateKey mKey = wt.getMasterPrivateKey("letter advice cage absurd amount doctor acoustic avoid letter advice cage above", "TREZOR");
        System.out.println("mKey = " + mKey);
        final String walletAddress = wt.getWalletAddress(mKey,0, 0, 0, 0);
        System.out.println("walletAddress = " + walletAddress);
        final String walletPrivateKey = wt.getWalletPrivateKey(mKey,0, 0, 0, 0);
        System.out.println("walletPrivateKey = " + walletPrivateKey);
        final String addressFromPrivateKey = wt.getAddressFromPrivateKey(walletPrivateKey);
        System.out.println("addressFromPrivateKey = " + addressFromPrivateKey);

        BitRafaelBTCClient c = new BitRafaelBTCClient("http://localhost:5556");
        BigDecimal b1 = c.getBalance("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        BigDecimal b2 = c.getBalanceConfirmed("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        System.out.println("balances = " + b1 + " " + b2);
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


    }
}
