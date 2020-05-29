/*************************************************************************************
 * Copyright (C) 2016-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.examples.bitcoin;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;

public class WalletToolsExample {
    public static void main(String[] args) {
        IWalletTools wt = new WalletTools();
        String cryptoCurrency = IClient.BTC;
        IMasterPrivateKey mKey = wt.getMasterPrivateKey("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about", "", cryptoCurrency, IWalletTools.STANDARD_BIP49);
        System.out.println("mKey = " + mKey);
        String xpub = mKey.getPUB();
        System.out.println("xpub = " + xpub);
        String xprv = mKey.getPRV();
        System.out.println("xprv = " + xprv);
        final String accountsaddress = wt.getWalletAddress(mKey,cryptoCurrency, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("accountsaddress = " + accountsaddress);
        final String walletPrivateKey = wt.getWalletPrivateKey(mKey,cryptoCurrency, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletPrivateKey = " + walletPrivateKey);
        final String accountXPUB = wt.getAccountPUB(mKey, cryptoCurrency,0);
        System.out.println("accountXPUB = " + accountXPUB);
        final String walletAddressFromXPUB = wt.getWalletAddressFromAccountPUB(accountXPUB, cryptoCurrency,IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletAddressFromXPUB = " + walletAddressFromXPUB);

        //generate new master key
        final String menmonic = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println("New generated menmonic = " + menmonic);

    }
}
