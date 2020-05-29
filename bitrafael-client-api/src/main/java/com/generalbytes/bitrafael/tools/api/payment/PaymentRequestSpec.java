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
package com.generalbytes.bitrafael.tools.api.payment;

import java.math.BigDecimal;

public class PaymentRequestSpec {
    private BigDecimal fiatAmount;
    private BigDecimal fiatToleranceAmount;
    private String fiatCurrency;
    private String cryptoCurrency;
    private String accountXPUBForReceivingPayment;
    private Object tag;
    private int safeNumberOfBlockConfirmations;
    private long validityDurationInSeconds;

    public PaymentRequestSpec(BigDecimal fiatAmount, BigDecimal fiatToleranceAmount, String fiatCurrency, String cryptoCurrency,  String accountXPUBForReceivingPayment) {
        this(fiatAmount,fiatToleranceAmount, fiatCurrency, cryptoCurrency, accountXPUBForReceivingPayment,null,3,15 * 60); //15 minutes
    }

    public PaymentRequestSpec(BigDecimal fiatAmount, BigDecimal fiatToleranceAmount, String fiatCurrency, String cryptoCurrency, String accountXPUBForReceivingPayment, Object tag, int safeNumberOfBlockConfirmations, long validityDurationInSeconds) {
        this.fiatAmount = fiatAmount;
        this.fiatCurrency = fiatCurrency;
        this.cryptoCurrency = cryptoCurrency;
        this.accountXPUBForReceivingPayment = accountXPUBForReceivingPayment;
        this.tag = tag;
        this.safeNumberOfBlockConfirmations = safeNumberOfBlockConfirmations;
        this.validityDurationInSeconds = validityDurationInSeconds;
        this.fiatToleranceAmount = fiatToleranceAmount;
    }


    public BigDecimal getFiatAmount() {
        return fiatAmount;
    }

    public String getFiatCurrency() {
        return fiatCurrency;
    }

    public Object getTag() {
        return tag;
    }

    public String getAccountXPUBForReceivingPayment() {
        return accountXPUBForReceivingPayment;
    }

    public int getSafeNumberOfBlockConfirmations() {
        return safeNumberOfBlockConfirmations;
    }

    public long getValidityDurationInSeconds() {
        return validityDurationInSeconds;
    }

    public BigDecimal getFiatToleranceAmount() {
        return fiatToleranceAmount;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String toString() {
        return "PaymentRequestSpec{" +
                "fiatAmount=" + fiatAmount +
                ", fiatToleranceAmount=" + fiatToleranceAmount +
                ", fiatCurrency='" + fiatCurrency + '\'' +
                ", cryptoCurrency='" + cryptoCurrency + '\'' +
                ", accountXPUBForReceivingPayment='" + accountXPUBForReceivingPayment + '\'' +
                ", tag=" + tag +
                ", safeNumberOfBlockConfirmations=" + safeNumberOfBlockConfirmations +
                ", validityDurationInSeconds=" + validityDurationInSeconds +
                '}';
    }
}
