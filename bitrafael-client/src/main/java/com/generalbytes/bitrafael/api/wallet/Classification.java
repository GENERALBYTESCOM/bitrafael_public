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
package com.generalbytes.bitrafael.api.wallet;


public class Classification {


    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ADDRESS = 1;
    public static final int TYPE_PRIVATE_KEY_IN_WIF = 2;
    public static final int TYPE_XPUB = 3;

    private int type;
    private String cryptoCurrency;
    private String cleanData;

    public Classification(int type) {
        this.type = type;
    }

    public Classification(int type, String cryptoCurrency, String cleanData) {
        this.type = type;
        this.cryptoCurrency = cryptoCurrency;
        this.cleanData = cleanData;
    }

    public int getType() {
        return type;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public String getCleanData() {
        return cleanData;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "type=" + type +
                ", cryptoCurrency='" + cryptoCurrency + '\'' +
                ", cleanData='" + cleanData + '\'' +
                '}';
    }
}
