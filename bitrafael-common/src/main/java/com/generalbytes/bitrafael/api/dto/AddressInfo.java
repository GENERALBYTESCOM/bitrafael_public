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

import java.util.ArrayList;
import java.util.List;

public class AddressInfo {
    private String address;
    private long numberOfTransactions;
    private long finalBalance;
    private long totalReceived;
    private long totalSent;
    private List<TxInfo> txInfos;

    public AddressInfo() {
    }

    public AddressInfo(String address, long numberOfTransactions, long finalBalance, long totalReceived, long totalSent) {
        this.address = address;
        this.numberOfTransactions = numberOfTransactions;
        this.finalBalance = finalBalance;
        this.totalReceived = totalReceived;
        this.totalSent = totalSent;
    }

    public String getAddress() {
        return address;
    }

    public long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public long getFinalBalance() {
        return finalBalance;
    }

    public long getTotalReceived() {
        return totalReceived;
    }

    public long getTotalSent() {
        return totalSent;
    }

    public List<TxInfo> getTxInfos() {
        return txInfos;
    }

    public void addTXInfo(TxInfo txInfo) {
        if (txInfo == null) {
            return;
        }
        if (txInfos == null) {
            txInfos = new ArrayList<TxInfo>();
        }
        txInfos.add(txInfo);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNumberOfTransactions(long numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public void setFinalBalance(long finalBalance) {
        this.finalBalance = finalBalance;
    }

    public void setTotalReceived(long totalReceived) {
        this.totalReceived = totalReceived;
    }

    public void setTotalSent(long totalSent) {
        this.totalSent = totalSent;
    }

    public void setTxInfos(List<TxInfo> txInfos) {
        this.txInfos = txInfos;
    }

    @Override
    public String toString() {
        return "AddressInfo{" +
                "address='" + address + '\'' +
                ", numberOfTransactions=" + numberOfTransactions +
                ", finalBalance=" + finalBalance +
                ", totalReceived=" + totalReceived +
                ", totalSent=" + totalSent +
                '}';
    }
}
