package com.generalbytes.bitrafael.api.dto;

import java.math.BigDecimal;

/**
 * Created by b00lean on 17.5.16.
 */
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
