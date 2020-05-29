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
package com.generalbytes.bitrafael.examples.bitcoin;

import com.generalbytes.bitrafael.client.Client;
import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;

import java.math.BigDecimal;


public class SendAllCoinsExample {
    public static void main(String[] args) {
        WalletTools wt = new WalletTools();
        IClient c = new Client();
        String cryptoCurrency = IClient.BTC;

        final BigDecimal FEE = new BigDecimal("0.01");
        String privateKey = "5Jt8.............................................";
        final BigDecimal balance = c.getAddressBalance(wt.getWalletAddressFromPrivateKey(privateKey,cryptoCurrency));
        final String txhash = c.send(privateKey, balance.subtract(FEE), "1DiMeHfaPxDAEY13wd5nX8HyKgfuATdaW5", FEE);
        System.out.println("Sent. txhash = " + txhash);
    }
}
