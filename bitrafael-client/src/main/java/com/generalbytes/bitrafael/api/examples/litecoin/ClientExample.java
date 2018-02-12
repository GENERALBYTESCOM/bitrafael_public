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
package com.generalbytes.bitrafael.api.examples.litecoin;

import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.AccountBalance;

public class ClientExample {
    public static void main(String[] args) {
        String cryptoCurrency = IClient.LTC;
        IClient c = new Client("https://coin.cz", cryptoCurrency);
        final String testXpub = "Ltub2ZAmuQgUPyAhZahH9yYmdPRUFBBLC9a77iUWEvDh6VfY5webf8UD8rcaiJYpSaBZHEyM5PZ5iXLpa4CQU9Umcp8LokYUCAEqXG8yUqaaqoL";
        final AccountBalance account = c.getAccountBalance(testXpub);
        String nextReceivingAddress = account.getNextReceivingAddress();
        final int nextReceivingIndex = account.getNextReceivingIndex();
        System.out.println("nextReceivingIndex = " + nextReceivingIndex);
        System.out.println("nextReceivingAddress = " + nextReceivingAddress);

    }
}
