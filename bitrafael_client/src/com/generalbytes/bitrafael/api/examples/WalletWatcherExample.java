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

import com.generalbytes.bitrafael.api.dto.TxInfo;
import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherWalletListener;
import com.generalbytes.bitrafael.api.watch.BlockchainWatcher;


public class WalletWatcherExample {
    public static void main(String[] args) {
        String cryptoCurrency = ExampleConfig.getConfig().getCryptoCurrency();
        BlockchainWatcher bw = new BlockchainWatcher(cryptoCurrency);
        bw.start();
        String testAddress = "13zq3yaBAiuUaEUPxhYwk5bXNadgnGeP28";
        bw.addWallet(testAddress, cryptoCurrency, new AbstractBlockchainWatcherWalletListener() {
            @Override
            public void walletContainsChanged(String walletAddress, String cryptoCurrency, Object tag, TxInfo tx) {
                System.out.println("New transaction on " + tag + " (" + walletAddress +") txhash: " + tx.getTxHash());
            }
        },"MyWallet");

        System.out.println("Send some bitcoins to: " + testAddress + " and see what happens.");
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
