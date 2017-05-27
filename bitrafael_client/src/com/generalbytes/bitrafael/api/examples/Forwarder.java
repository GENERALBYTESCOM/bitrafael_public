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
import com.generalbytes.bitrafael.api.dto.TxInfo;
import com.generalbytes.bitrafael.api.wallet.WalletTools;
import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherWalletListener;
import com.generalbytes.bitrafael.api.watch.BlockchainWatcher;

import java.math.BigDecimal;

public class Forwarder {
    public static void main(String[] args) {
        WalletTools wt = new WalletTools();
        BlockchainWatcher bw = new BlockchainWatcher();
        final String cryptoCurrency = ExampleConfig.getConfig().getCryptoCurrency();
        final IClient c = new Client("https://coin.cz", cryptoCurrency);

        final String[] privatekeys = {
                "L54zVtKQ3kY8CuoEjhijsgTiGZVWhEHDcCeTZc7ssYjwMLemQ5xu",
                "L2dp5ombew15aEtTjHGAPEZP4nvMNJENCiwUHgi9HJHmj923e7vY",
                "L4L8c5s1Wqzi1gKTJ8iExJbzgRLBbsD5z4Kr6Hn7Kr94JoipQzF5",
                "L2LHh5t4un3oHkJnwsrXg6jDXYJFgHKvMXL4GhTfn21gHvz64Jum",
                "KwPEN93LCYeBDee3yjSGfmi8QaECs5wrv5Yv59o1S5LMMYdoKBPq",
                "KzQWKBGobymynHrJZzmorhtS3hzs5AXnAe7XG35X4rrkwHAyNMEX",
                "L1sCd7mek2u1wDPzfnL8iwiqTq9tQVeky785KbEqR8QFT3mEjq3a",
                "KyCMXa5KBS4yeqimdyiyphFi9YnGkcUAxy76mjgYxNUpzvNEYdWg",
                "L1DKyeb4tXxBaxDnrt58Yn2AimbJbYSps2VN63Ltv517XrUzPR2A",
                "KwERsxzcYFX7oihYCGeiPDLhTyxGuTP73utzrYTfETDeceJXFAnU",
                "L4Bfx3a7ovZTtDyX3pf2cz7jMtS2uZP8mRKoZbqHuATpUwjMfyA2"};
        final String toAddress = "19juqmttHLW3PiVuziUiPeP75xCi7uP2FH";

        for (int i = 0; i < privatekeys.length; i++) {
            final String privatekey = privatekeys[i];
            final String fromAddress = wt.getWalletAddressFromPrivateKey(privatekey,cryptoCurrency);

            System.out.println("Watching " + fromAddress + " for transactions...");
            bw.addWallet(fromAddress,cryptoCurrency, new AbstractBlockchainWatcherWalletListener() {
                @Override
                public void walletContainsChanged(String walletAddress, String cryptoCurrency,Object tag, TxInfo tx) {
                    System.out.println("New transaction on " + tag + " (" + walletAddress +") txhash: " + tx.getTxHash());
                    final BigDecimal addressBalance = c.getAddressBalance(fromAddress);
                    if (addressBalance.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println("Sending coins to " + toAddress);
                        final BigDecimal fee = new BigDecimal("0.0002");
                        final String send = c.send(privatekey, addressBalance.subtract(fee), toAddress, fee);
                        System.out.println("send = " + send);
                    }
                }
            },"MyWallet_" + i);
            bw.start();

        }


        System.out.println("Awaiting payments...");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
