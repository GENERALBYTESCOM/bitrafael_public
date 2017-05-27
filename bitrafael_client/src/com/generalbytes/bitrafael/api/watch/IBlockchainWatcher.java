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
package com.generalbytes.bitrafael.api.watch;


public interface IBlockchainWatcher {
    void addBlockchainListener(IBlockchainWatcherListener listener);
    void removeBlockchainListener(IBlockchainWatcherListener listener);
    void addWallet(String walletAddress, String cryptoCurrency, IBlockchainWatcherWalletListener listener, Object tag);
    Object removeWallet(String walletAddress, String cryptoCurrency);
    Object removeWallet(IBlockchainWatcherWalletListener listener);

    void addTransaction(String transactionHash, String cryptoCurrency, IBlockchainWatcherTransactionListener listener, Object tag);
    Object removeTransaction(String transactionHash, String cryptoCurrency);
    Object removeTransaction(IBlockchainWatcherTransactionListener listener);
}
