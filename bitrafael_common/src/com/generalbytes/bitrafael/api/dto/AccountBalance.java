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

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class AccountBalance {
    private String xpub;

    @JsonProperty("next_receiving_address")
    @XmlElement(name="next_receiving_address")
    private String nextReceivingAddress;

    @JsonProperty("next_receiving_index")
    @XmlElement(name="next_receiving_index")
    private int nextReceivingIndex;

    @JsonProperty("next_change_address")
    @XmlElement(name="next_change_address")
    private String nextChangeAddress;

    @JsonProperty("next_change_index")
    @XmlElement(name="next_change_index")
    private int nextChangeIndex;

    private long total;
    @JsonProperty("total_confirmed")
    @XmlElement(name="total_confirmed")
    private long totalConfirmed;

    public AccountBalance() {
    }


    public AccountBalance(String xpub, String nextReceivingAddress, int nextReceivingIndex, String nextChangeAddress, int nextChangeIndex, long total, long totalConfirmed) {
        this.xpub = xpub;
        this.nextReceivingAddress = nextReceivingAddress;
        this.nextReceivingIndex = nextReceivingIndex;
        this.nextChangeAddress = nextChangeAddress;
        this.nextChangeIndex = nextChangeIndex;
        this.total = total;
        this.totalConfirmed = totalConfirmed;
    }


    public String getXpub() {
        return xpub;
    }

    public void setXpub(String xpub) {
        this.xpub = xpub;
    }


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @XmlTransient
    public long getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(long totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public String getNextReceivingAddress() {
        return nextReceivingAddress;
    }

    public void setNextReceivingAddress(String nextReceivingAddress) {
        this.nextReceivingAddress = nextReceivingAddress;
    }

    public int getNextReceivingIndex() {
        return nextReceivingIndex;
    }

    public void setNextReceivingIndex(int nextReceivingIndex) {
        this.nextReceivingIndex = nextReceivingIndex;
    }

    public String getNextChangeAddress() {
        return nextChangeAddress;
    }

    public void setNextChangeAddress(String nextChangeAddress) {
        this.nextChangeAddress = nextChangeAddress;
    }

    public int getNextChangeIndex() {
        return nextChangeIndex;
    }

    public void setNextChangeIndex(int nextChangeIndex) {
        this.nextChangeIndex = nextChangeIndex;
    }

    @Override
    public String toString() {
        return "AccountBalance{" +
                "xpub='" + xpub + '\'' +
                ", nextReceivingAddress='" + nextReceivingAddress + '\'' +
                ", nextReceivingIndex=" + nextReceivingIndex +
                ", nextChangeAddress='" + nextChangeAddress + '\'' +
                ", nextChangeIndex=" + nextChangeIndex +
                ", total=" + total +
                ", totalConfirmed=" + totalConfirmed +
                '}';
    }
}
