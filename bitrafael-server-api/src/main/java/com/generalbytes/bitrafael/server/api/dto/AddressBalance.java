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

package com.generalbytes.bitrafael.server.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class AddressBalance {
    private String address;
    private long total;

    @JsonProperty("total_confirmed")
    @XmlElement(name="total_confirmed")
    private long totalConfirmed;

    public AddressBalance() {
    }

    public AddressBalance(String address, long total, long totalConfirmed) {
        this.address = address;
        this.total = total;
        this.totalConfirmed = totalConfirmed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public String toString() {
        return "AddressBalance{" +
                "address='" + address + '\'' +
                ", total=" + total +
                ", totalConfirmed=" + totalConfirmed +
                '}';
    }
}
