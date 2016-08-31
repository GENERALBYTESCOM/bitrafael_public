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

public class TxFeesInfo {
    private long fastestFee; //fastestFee: The lowest fee (in satoshis per byte) that will currently result in the fastest transaction confirmations (usually 0 to 1 block delay).
    private long halfHourFee; //halfHourFee: The lowest fee (in satoshis per byte) that will confirm transactions within half an hour (with 90% probability).
    private long hourFee; //hourFee: The lowest fee (in satoshis per byte) that will confirm transactions within an hour (with 90% probability).

    public TxFeesInfo() {
    }

    public TxFeesInfo(long fastestFee, long halfHourFee, long hourFee) {
        this.fastestFee = fastestFee;
        this.halfHourFee = halfHourFee;
        this.hourFee = hourFee;
    }

    public long getFastestFee() {
        return fastestFee;
    }

    public long getHalfHourFee() {
        return halfHourFee;
    }

    public long getHourFee() {
        return hourFee;
    }

    public void setFastestFee(long fastestFee) {
        this.fastestFee = fastestFee;
    }

    public void setHalfHourFee(long halfHourFee) {
        this.halfHourFee = halfHourFee;
    }

    public void setHourFee(long hourFee) {
        this.hourFee = hourFee;
    }

    @Override
    public String toString() {
        return "TxFeesInfo{" +
                "fastestFee=" + fastestFee +
                ", halfHourFee=" + halfHourFee +
                ", hourFee=" + hourFee +
                '}';
    }
}
