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
package com.generalbytes.bitrafael.api.examples.bitcoin;

import com.google.common.base.Joiner;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;

import java.security.SecureRandom;
import java.util.List;

public class VanitySeeder {
    private static final String PREFIX = "1AA";
    public static void main(String[] args) {
        SecureRandom prng = new SecureRandom();

        System.out.println("Searching for addresses with prefix: " + PREFIX + "...");
        for (int thread=0;thread<50;thread++) {
            new Thread(() -> {
                long takes=0;
                while(true) {
                    try {
                        byte[] entropy = new byte[32];
                        prng.nextBytes(entropy);
                        List<String> words = MnemonicCode.INSTANCE.toMnemonic(Sha256Hash.of(entropy).getBytes());
                        byte[] seedBytes = MnemonicCode.toSeed(words, "TREZOR");
                        final DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
                        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(44, true));
                        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(0, true));
                        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(0, true));
                        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(0, false));
                        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(0, false));

                        String address = Address.fromKey(MainNetParams.get(), walletKey, Script.ScriptType.P2PKH).toString();
                        if (address.startsWith(PREFIX)) {
                            System.out.println("Wallet address of account 0 = " + address + " seed: " + Joiner.on(" ").join(words));
                            System.exit(0);
                        }
                        takes++;
                        if (takes % 1000L == 0L) {
                            System.out.println("takes = " + takes);
                        }
                    } catch (MnemonicException.MnemonicLengthException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
