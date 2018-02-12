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

package com.generalbytes.bitrafael.api.dto;

public class OutputInfo {
    private String txHash;
    private int index;
    private String address;
    private long value;

    public OutputInfo() {
    }

    public OutputInfo(String txHash, int index, String address, long value) {
        this.txHash = txHash;
        this.index = index;
        this.address = address;
        this.value = value;
    }

    public String getTxHash() {
        return txHash;
    }

    public int getIndex() {
        return index;
    }

    public String getAddress() {
        return address;
    }

    public long getValue() {
        return value;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
