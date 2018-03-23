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
package com.generalbytes.bitrafael.api.examples.xmr;

import com.generalbytes.bitrafael.api.wallet.xmr.Account;
import com.generalbytes.bitrafael.api.wallet.xmr.Address;
import com.generalbytes.bitrafael.api.wallet.xmr.WalletToolsXMR;

public class MoneroExample {
    public static void main(String[] args) {
        WalletToolsXMR wt = new WalletToolsXMR();

        String seed = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println("seed = " + seed);
        Account account = wt.getAccount(seed);
        System.out.println("account = " + account);
        boolean addressValid = wt.isAddressValid(account.getAddress().toString());
        System.out.println("addressValid = " + addressValid);

        Account addr = wt.getAccount(account.getSeedInMnemonic());
        Address parsedAddr = Address.parse(addr.getAddress().toString());
        System.out.println("parsedAddr = " + parsedAddr);
        parsedAddr.setPaymentId(1337);
        System.out.println("parsedAddr with paymentId = " + parsedAddr);

    }
}
