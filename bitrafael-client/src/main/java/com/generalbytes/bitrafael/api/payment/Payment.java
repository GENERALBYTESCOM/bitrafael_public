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

    public enum State {
        UNKNOWN(0),
        NEW(1),
        ARRIVING(2),
        RECEIVED(3),
        FAILED(4);

        int id;
        State(int id) {
            this.id = id;
        }

        public static State getById(Long id) {
            for(State e : values()) {
                if(e.id == id) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public int getId() {
            return id;
        }

    }

    public enum Failure {
        NONE(0),
        SOMETHING_ARRIVED_AFTER_TIMEOUT(1),
        NOTHING_ARRIVED_WITHIN_TIMEOUT(2),
        NOTHING_RECEIVED_WITHIN_TIMEOUT(3), // time for amount to have enough confirmations
        INVALID_AMOUNT_RECEIVED(4); //customer sent invalid amount for some reason

        int id;

        Failure(int id) {
            this.id = id;
        }

        public static Failure getById(Long id) {
            for(Failure e : values()) {
                if(e.id == id) {
                    return e;
                }
            }
            return NONE;
        }

        public int getId() {
            return id;
        }
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
