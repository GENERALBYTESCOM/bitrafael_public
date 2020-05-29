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

import java.math.BigDecimal;

public class AmountsPair {
    private BigDecimal fromAmount;
    private String fromCurrency; //fiat or crypto

    private BigDecimal toAmount; //can be null in request case
    private String toCurrency; //fiat or crypto

    public AmountsPair() {
    }

    public AmountsPair(BigDecimal fromAmount, String fromCurrency, BigDecimal toAmount, String toCurrency) {
        this.fromAmount = fromAmount;
        this.fromCurrency = fromCurrency;
        this.toAmount = toAmount;
        this.toCurrency = toCurrency;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public String getToCurrency() {
        return toCurrency;
    }
}
