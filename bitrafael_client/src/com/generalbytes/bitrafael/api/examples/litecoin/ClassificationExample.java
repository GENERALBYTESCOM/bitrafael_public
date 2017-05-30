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

import com.generalbytes.bitrafael.api.wallet.WalletTools;


public class ClassificationExample {
    public static void main(String[] args) {
        String[] testStrings = {
                "5Kb8kLf9zgWQnogidDA76MzPL6TsZZY36hWXMssSzNydYXYB9KF",
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

        };
        WalletTools wt = new WalletTools();
        for (int i = 0; i < testStrings.length; i++) {
            String testString = testStrings[i];
            System.out.println("               " + testString + "  = " + wt.classify(testString));
        }
    }
}
