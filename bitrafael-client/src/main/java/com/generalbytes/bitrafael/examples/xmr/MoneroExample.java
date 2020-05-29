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
package com.generalbytes.bitrafael.examples.xmr;

import com.generalbytes.bitrafael.tools.wallet.xmr.Account;
import com.generalbytes.bitrafael.tools.wallet.xmr.Address;
import com.generalbytes.bitrafael.tools.wallet.xmr.WalletToolsXMR;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.Utils;

public class MoneroExample {
    public static void main(String[] args) {
        WalletToolsXMR wt = new WalletToolsXMR();

        String seed = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println("seed = " + seed);
        Account account = wt.getAccount(seed,0);
        System.out.println("account = " + account);
        boolean addressValid = wt.isAddressValid(account.getAddress().toString());
        System.out.println("addressValid = " + addressValid);

        Account addr = wt.getAccount(account.getSeedInMnemonic(),0);
        Address parsedAddr = Address.parse(addr.getAddress().toString());
        System.out.println("parsedAddr = " + parsedAddr);
        parsedAddr.setPaymentId(1337);
        System.out.println("parsedAddr with paymentId = " + parsedAddr);

        //try double seeding (two seed formats with same result)
        String[] seeds = wt.generateShortAndLongSeedMnemonicsSeparatedBySpaces();
        String shortSeed = seeds[0];
        String longSeed = seeds[1];

        System.out.println("shortSeed = " + shortSeed);
        System.out.println("longSeed = " + longSeed);
        Account shortAccount = wt.getAccount(shortSeed,0);
        Account longAccount = wt.getAccount(longSeed,0);
        if (shortAccount.getPublicSpendKey().equals(longAccount.getPublicSpendKey())){
            System.out.println("Seeds are equal!");
            String shortViewKey = Utils.bytesToHex(shortAccount.getPrivateViewKey().getEncoded());
            String longViewKey = Utils.bytesToHex(longAccount.getPrivateViewKey().getEncoded());
            System.out.println("shortViewKey = " + shortViewKey);
            System.out.println("longViewKey = " + longViewKey);
        }else{
            System.out.println("Something is wrong with the world!");
        }
    }
}
