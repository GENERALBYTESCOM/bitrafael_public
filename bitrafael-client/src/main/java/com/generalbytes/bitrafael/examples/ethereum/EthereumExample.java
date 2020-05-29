/*************************************************************************************
 * Copyright (C) 2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.examples.ethereum;


import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.eth.MasterPrivateKeyETH;
import com.generalbytes.bitrafael.tools.wallet.eth.WalletToolsETH;

public class EthereumExample {
    public static void main(String[] args) {
        WalletToolsETH wt = new WalletToolsETH();
        String seed = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println("seed = " + seed);
        MasterPrivateKeyETH masterPrivateKey = wt.getMasterPrivateKey(seed,"",IClient.ETH, IWalletTools.STANDARD_BIP44);
        String pub = masterPrivateKey.getPUB();
        System.out.println("pub = " + pub);
        String prv = masterPrivateKey.getPRV();
        System.out.println("prv = " + prv);
        String walletAddress = wt.getWalletAddress(masterPrivateKey, IClient.ETH, 0, 0, 0);
        System.out.println("walletAddress = " + walletAddress);
        boolean addressValid = wt.isAddressValid(walletAddress, IClient.ETH);
        System.out.println("addressValid = " + addressValid);
    }
}
