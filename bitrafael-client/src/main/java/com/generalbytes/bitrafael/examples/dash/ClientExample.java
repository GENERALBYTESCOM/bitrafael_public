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
package com.generalbytes.bitrafael.examples.dash;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;

public class ClientExample {
    public static void main(String[] args) {
        String cryptoCurrency = IClient.DASH;
        WalletTools wt = new WalletTools();
        String seeed = wt.generateSeedMnemonicSeparatedBySpaces();
        IMasterPrivateKey m = wt.getMasterPrivateKey(seeed, "TREZOR", cryptoCurrency, IWalletTools.STANDARD_BIP44);
        String walletPrivateKey = wt.getWalletPrivateKey(m, cryptoCurrency, 0, 0, 0);
        System.out.println("walletPrivateKey = " + walletPrivateKey);
        String walletAddressFromPrivateKey = wt.getWalletAddressFromPrivateKey(walletPrivateKey, cryptoCurrency);
        System.out.println("walletAddressFromPrivateKey = " + walletAddressFromPrivateKey);

        String addr = wt.getWalletAddressFromAccountPUB("drkpRyug7FHRmqscP7njUP2dhQzbsswWCospdXNy7knCKjQQ4uLnRZh99dgTACrXujDHo2ECNeEheW4KZeDCnqV9XL4ndr532gFGTwWR1jWAnBh", cryptoCurrency, 0, 0);
        System.out.println("addr = " + addr);//should be XrAwEffseCKgQPQhYqXuscBaoUnHqkKxQz
        /*
        IClient c = new Client("https://beta.coin.cz", cryptoCurrency);
        final String testXpub = "Ltub2ZAmuQgUPyAhZahH9yYmdPRUFBBLC9a77iUWEvDh6VfY5webf8UD8rcaiJYpSaBZHEyM5PZ5iXLpa4CQU9Umcp8LokYUCAEqXG8yUqaaqoL";
        final AccountBalance account = c.getAccountBalance(testXpub);
        String nextReceivingAddress = account.getNextReceivingAddress();
        final int nextReceivingIndex = account.getNextReceivingIndex();
        System.out.println("nextReceivingIndex = " + nextReceivingIndex);
        System.out.println("nextReceivingAddress = " + nextReceivingAddress);
        */



    }
}
