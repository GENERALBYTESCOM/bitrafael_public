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

package com.generalbytes.bitrafael.api.client;

import java.math.BigDecimal;

public class ClientExample {
    public static void main(String[] args) {
        BitRafaelBTCClient c = new BitRafaelBTCClient("https://coin.cz");
        BigDecimal b1 = c.getBalance("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        BigDecimal b2 = c.getBalanceConfirmed("1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN");
        System.out.println("balances = " + b1 + " " + b2);
        final String txHash = c.send("5JFL....private_key_for_1tEsTvxKTYsejxMQmAEMMNXB5M5JWTXAN", new BigDecimal("0.0002"), "34ZzYw5xB8JTFcECJrFo12sCEGK9St11bU");
        System.out.println("txHash = " + txHash);


    }
}
