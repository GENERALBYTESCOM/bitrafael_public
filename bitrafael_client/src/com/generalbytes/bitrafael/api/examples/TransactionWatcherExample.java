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

import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherTransactionListener;
import com.generalbytes.bitrafael.api.watch.BlockchainWatcher;
import com.generalbytes.bitrafael.api.watch.IBlockchainWatcherListener;


public class TransactionWatcherExample {
    public static void main(String[] args) {
        BlockchainWatcher bw = new BlockchainWatcher();
        bw.start();
        bw.addBlockchainListener(new IBlockchainWatcherListener() {
            @Override
            public void newBlockMined(long blockHeight) {
                System.out.println("New Block is mined: " + blockHeight);
            }
        });

        bw.addTransaction("50f8b0b9c76a155989783775bd61889a0f6d20b3d99978971f67ba9b0943286f",new AbstractBlockchainWatcherTransactionListener() {
            @Override
            public void numberOfConfirmationsChanged(String transactionHash, Object tag, int numberOfConfirmations) {
                System.out.println("numberOfConfirmationsChanged " + transactionHash + " = " + numberOfConfirmations );
            }

            @Override
            public void newBlockMined(String transactionHash, Object tag, long blockHeight) {
                System.out.println("New block mined: " + transactionHash + " = " + blockHeight);
            }

        },"KUK");
        System.out.println("Wait at least 30 mins to see how confirmations increase...");
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
