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
    public static final int TYPE_SEED_MNEMONIC = 4;


    private int type;
    private String cryptoCurrency;
    private String cleanData;
    private boolean containsPrefix;
    private String prefix;

    public Classification(int type, boolean containsPrefix, String prefix) {
        this.type = type;
        this.containsPrefix = containsPrefix;
        this.prefix = prefix;
    }

    public Classification(int type, String cryptoCurrency, String cleanData, boolean containsPrefix, String prefix) {
        this.type = type;
        this.cryptoCurrency = cryptoCurrency;
        this.cleanData = cleanData;
        this.containsPrefix = containsPrefix;
        this.prefix = prefix;
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

    public boolean isContainsPrefix() {
        return containsPrefix;
    }

    public void setContainsPrefix(boolean containsPrefix) {
        this.containsPrefix = containsPrefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "type=" + type +
                ", cryptoCurrency='" + cryptoCurrency + '\'' +
                ", cleanData='" + cleanData + '\'' +
                ", containsPrefix='" + containsPrefix + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
