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

public class TxTemplateInput {
    private String address;
    private long amount;
    private String previousOutputTx;
    private long previousOutputIndex;
    private TxSignature signature;

    public TxTemplateInput() {
    }

    public TxTemplateInput(String address) {
        this.address = address;
    }

    public TxTemplateInput(String address, long amount) {
        this.address = address;
        this.amount = amount;
    }

    public TxTemplateInput(String address, long amount, String previousOutputTx, long previousOutputIndex, TxSignature signature) {
        this.address = address;
        this.amount = amount;
        this.previousOutputTx = previousOutputTx;
        this.previousOutputIndex = previousOutputIndex;
        this.signature = signature;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setPreviousOutputTx(String previousOutputTx) {
        this.previousOutputTx = previousOutputTx;
    }

    public void setPreviousOutputIndex(long previousOutputIndex) {
        this.previousOutputIndex = previousOutputIndex;
    }

    public void setSignature(TxSignature signature) {
        this.signature = signature;
    }

    public long getAmount() {
        return amount;
    }

    public String getPreviousOutputTx() {
        return previousOutputTx;
    }

    public long getPreviousOutputIndex() {
        return previousOutputIndex;
    }

    public TxSignature getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "TxTemplateInput{" +
                "address='" + address + '\'' +
                ", amount=" + amount +
                ", previousOutputTx='" + previousOutputTx + '\'' +
                ", previousOutputIndex=" + previousOutputIndex +
                ", signature=" + signature +
                '}';
    }
}
