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
package com.generalbytes.bitrafael.examples;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.Classification;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;


public class ClassificationExample {
    public static void main(String[] args) {
        String[] testStrings = {
                "KxMvHfvs7xi75388Y1h1i1CAbWHnRvaqm243b3bTm1uN79goSeK6",//1GBU9jDHaezEQCZ7tcWW2fqgxrKpgMhmm2
                "L3B17K8TvDLBsyozrBUAer79CB19N5d5NtUBuUsnFFtBmycyJvYx", //149eZmWi1VR4Po1yJVE1hWQ5f14ZyrpxDW
                "5Kb8kLf9zgWQnogidDA76MzPL6TsZZY36hWXMssSzNydYXYB9KF", //1CC3X2gu58d6wXUWMffpuzN9JAfTUWu4Kj
                "1CC3X2gu58d6wXUWMffpuzN9JAfTUWu4Kj",
                "xpub6EVt68TrKV5YPXF9oXfEPsqWc5sRjLFQg7GAtLKwF4oss4sZKRvjQqNGYk4ZvrsC3hzuL87LvB7phibDDQSuCEeTRii4ST8Y28DuyfoFxJB",
                "LiY355FRj3C3F9XK1YjBHAhw4Xy7gEYU6o",
                "6vgRX9DKcdUPAZrKHfob8KQwr6d8wCyB1CNqNAaEFKeZXNT4w4B",
                "6usaU9juDmkE4zpNu4t4eGS6cu4L8nRe9ektX8aHFK3cc7NFvej",
                "3P14159f73E4gFr7JterCCQh9QjiTjiZrG",
                "Ltub2ZAmuQgUPyAhZahH9yYmdPRUFBBLC9a77iUWEvDh6VfY5webf8UD8rcaiJYpSaBZHEyM5PZ5iXLpa4CQU9Umcp8LokYUCAEqXG8yUqaaqoL",
                "litecoin:3P14159f73E4gFr7JterCCQh9QjiTjiZrG",
                "bitcoin:1CC3X2gu58d6wXUWMffpuzN9JAfTUWu4Kj",
                "bitcoin:1CC3X2gu58d6wXUWMffpuzN9JAfTUWu4Kj?amount=222&ddd=d33",
                "iban:XE617J3HKGK4CG10FNU6YNJ68R5P9SEW27M", //ETH iban format
                "MQH3zAjbbcaC36RNpLQhGDEcFMxbKziYro",
                "T7oXc1BjCmaq3Hp6nnQhwKqZcPYZawBrgcMpucXCEzt7ByBqoZvx",
                "xpub6CZZ5vfHsskmdyAiSjQfxBLNg5bwssoKe1dm2B3HGx1Efo2wCMsm6Wr17LEVMzJq3UbyFXg3RjC7EJ5qxz2pG9FkZuyzbsHd1aGwL2WF85A",
                "XbmPwcZCdaRMa4m4JedspBPkUqY1FKRhsX", //dash address
                "drkvjJe5sJgqomjLnD39LKGdme64zzL4d38fDEhkveYsjvqDZzEZSoq6VEE5znetSRvSB6pYAxhTViXJdZ5QygKogD4nsa31hQ8aVuW6psczteC", //dash prv
                "49Fd89xZRHpbDxhMkpyMDWZNKUHG4GjtqDL1yYaQZwp7PEDiRDwk4rdJNDvAyBDRX7BaGfakBZLkEguPPjQLdAWKTJ5t2Pf", //monero address
                "monero:pavements warped ability hexagon sabotage tether juicy unfit trying listen birth rarest birth", //monero short seed
                "monero:pram flying gained wobbly dehydrate rarest evenings goggles buckets awesome slower boxes perfect puddle acumen vocal simplest woozy lectures jingle magically addicted glass governing evenings", //monero long seed
                "82uq8wckyDheRACjnFxbAxfRBD7pRMvF2EEsfNv7A1n3V86eD8KPuGwfmGmXKk6jXQBnpfUG5bGCg1XbFqUipGT28iW6vNL", //monero sub address
                "0x8912358D977e123b51EcAd1fFA0cC4A7e32FF774", //ethereum
                "qz2zt4yt687zgm6qzpwne26sgf6ez2lfru2kx4ptkr", //bitcoincash
                "bitcoin:bc1q8s3h3vw5xufdas890q29lpuca56r0ezqar0mvs?amount=222&ddd=d33", // btc bech32 segwit address - starts with bc1 (bc1q)
                "bitcoin:bc1pveaamy78cq5hvl74zmfw52fxyjun3lh7lgt44j03ygx02zyk8lesgk06f6?amount=222&ddd=d33", // btc taproot address - starts with bc1p



        };
        WalletTools wt = new WalletTools();
        for (int i = 0; i < testStrings.length; i++) {
            String testString = testStrings[i];
            final Classification classify = wt.classify(testString);
            String note = "";
            if (classify != null && classify.getType() == Classification.TYPE_PRIVATE_KEY_IN_WIF) {
                if (classify.getCryptoCurrency().equalsIgnoreCase(IClient.BTC)) {
                    org.bitcoinj.core.DumpedPrivateKey dp = org.bitcoinj.core.DumpedPrivateKey.fromBase58(org.bitcoinj.params.MainNetParams.get(),testString);
                    note = " >>> " + org.bitcoinj.core.Address.fromKey(org.bitcoinj.params.MainNetParams.get(), dp.getKey(), org.bitcoinj.script.Script.ScriptType.P2PKH);
                }else if (classify.getCryptoCurrency().equalsIgnoreCase(IClient.LTC)) {
                    org.litecoinj.core.DumpedPrivateKey dp = org.litecoinj.core.DumpedPrivateKey.fromBase58(org.litecoinj.params.MainNetParams.get(),classify.getCleanData());
                    note = " >>> " + org.litecoinj.core.Address.fromKey(org.litecoinj.params.MainNetParams.get(), dp.getKey(), org.litecoinj.script.Script.ScriptType.P2PKH);
                }
            }else if (classify.getType() == Classification.TYPE_UNKNOWN){
                System.err.println("Error parsing " + testString);
            }
            System.out.println("               " + testString + "  = " + classify + " "+ note);
        }
    }
}
