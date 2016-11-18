/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.colorledger;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import java.security.SecureRandom;

public class ColorWallet {
    private String privateKey;
    private String address;
    private ECKey key;
    private static SecureRandom random = new SecureRandom();

    public ColorWallet() {
        this(new ECKey());
    }

    public ColorWallet(ECKey key) {
        this.key = key;
        address = new Address(MainNetParams.get(),key.getPubKeyHash()).toString();
        privateKey = key.getPrivateKeyAsWiF(MainNetParams.get());
    }

    public ColorWallet(String privateKey) {
        this(DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey).getKey());
    }

    public String getAddress() {
        return address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public Transaction createTransaction(String toAddress, String coinColor, long amount) {
        byte[] nonce = generateNonce();
        Transaction t = new Transaction(nonce,address,key.getPubKey(),toAddress,coinColor,amount, null);
        t.sign(key);
        return t;
    }

    private byte[] generateNonce() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    @Override
    public String toString() {
        return "ColorWallet{" +
                "privateKey='" + privateKey + '\'' +
                ", address='" + address + '\'' +
                '}';
    }


}
