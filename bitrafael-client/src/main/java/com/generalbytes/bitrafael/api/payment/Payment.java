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

package com.generalbytes.bitrafael.api.payment;

import java.math.BigDecimal;


public class Payment {
    private long created;
    private PaymentRequest request;
    private State state;
    private Failure failure = Failure.NONE;
    private String txId;
    private BigDecimal cryptoAmountReceived;
    private BigDecimal cryptoAmountMiningFee;

    public Payment(long created, PaymentRequest request, State state) {
        this.created = created;
        this.request = request;
        this.state = state;
    }

    public long getCreated() {
        return created;
    }

    public PaymentRequest getRequest() {
        return request;
    }

    public State getState() {
        return state;
    }

    public String getTxId() {
        return txId;
    }

    public BigDecimal getCryptoAmountReceived() {
        return cryptoAmountReceived;
    }

    public BigDecimal getCryptoAmountMiningFee() {
        return cryptoAmountMiningFee;
    }

    enum State {
        NEW,
        ARRIVING,
        RECEIVED,
        FAILED,
    }

    enum Failure {
        NONE,
        SOMETHING_ARRIVED_AFTER_TIMEOUT,
        NOTHING_ARRIVED_WITHIN_TIMEOUT,
        NOTHING_RECEIVED_WITHIN_TIMEOUT, // time for amount to have enough confirmations
        INVALID_AMOUNT_RECEIVED //customer sent invalid amount for some reason
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public void setCryptoAmountReceived(BigDecimal cryptoAmountReceived) {
        this.cryptoAmountReceived = cryptoAmountReceived;
    }

    public void setCryptoAmountMiningFee(BigDecimal cryptoAmountMiningFee) {
        this.cryptoAmountMiningFee = cryptoAmountMiningFee;
    }

    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "created=" + created +
                ", request=" + request +
                ", state=" + state +
                ", failure=" + failure +
                ", txId='" + txId + '\'' +
                ", cryptoAmountReceived=" + cryptoAmountReceived +
                ", cryptoAmountMiningFee=" + cryptoAmountMiningFee +
                '}';
    }
}
