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
package com.generalbytes.bitrafael.api.wallet.xmr;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.Classification;
import com.generalbytes.bitrafael.api.wallet.IClassificator;
import com.generalbytes.bitrafael.api.wallet.xmr.core.Utils;
import com.generalbytes.bitrafael.api.wallet.xmr.core.common.Keccak256;
import com.generalbytes.bitrafael.api.wallet.xmr.core.ed25519.Ed25519Constants;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.KeyFactory;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PrivateKey;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PublicKey;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class WalletToolsXMR implements IClassificator{


    public String generateSeedMnemonicSeparatedBySpaces() {
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            BigInteger val = Utils.randomBigInt(prng, Ed25519Constants.L);
            //seed should be already reduced no need to reduce it
            byte[] entropy = Utils.as256BitLe(val);
            String seedWords = XMRMnemonicUtility.toMnemonic(entropy);
            return seedWords;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Account getAccount(String seedMnemonicSeparatedBySpaces) {
        byte[] seed = XMRMnemonicUtility.toEntropy(seedMnemonicSeparatedBySpaces);
        if (seed != null) {

            if (seed.length != 256/8) { //if seed is not long enough make it 256bit (MyMonero case)
                Keccak256 keccak256 = new Keccak256();
                keccak256.update(seed);
                seed = keccak256.digest().array();
            }

            byte[] b = Utils.sc_reduce32(seed); //just to be sure (seed should be already valid)
            Keccak256 keccak256 = new Keccak256();
            keccak256.update(seed);
            byte[] a = Utils.sc_reduce32(keccak256.digest().array()); //calculate viewkey

            PrivateKey secretA = null;
            PublicKey publicA = null;
            PrivateKey secretB = null;
            PublicKey publicB = null;
            try {
                KeyFactory keyFactory = new KeyFactory();
                secretA = keyFactory.decodePrivateKey(a);
                publicA = keyFactory.generatePublicKey(secretA);
                secretB = keyFactory.decodePrivateKey(b);
                publicB = keyFactory.generatePublicKey(secretB);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            return new Account(seedMnemonicSeparatedBySpaces, seed, secretB, secretA, publicB, publicA);
        }
        return null;
    }

    public boolean isAddressValid(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        Address parsed = Address.parse(address);
        return parsed != null;
    }

    @Override
    public Classification classify(String input, String cryptoCurrencyHint) {
        return classify(input);
    }

    @Override
    public Classification classify(String input) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n","");
        if (input.contains(":")) {
            //remove leading protocol
            input = input.substring(input.indexOf(":") + 1);
        }

        //remove leading slashes
        if (input.startsWith("//")) {
            input = input.substring("//".length());
        }

        //remove things after
        if (input.contains("?")) {
            input = input.substring(0,input.indexOf("?"));
        }

        if (input.startsWith("4") && input.length()>=95) {
            //most likely address lets check it
            Address parse = Address.parse(input);
            if (parse == null) {
                return new Classification(Classification.TYPE_ADDRESS, IClient.XMR,input);
            }
        }
        return new Classification(Classification.TYPE_UNKNOWN);
    }


}
