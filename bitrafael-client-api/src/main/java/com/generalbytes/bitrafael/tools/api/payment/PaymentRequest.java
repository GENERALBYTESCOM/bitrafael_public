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
import java.net.URLEncoder;

public class PaymentRequest {
    private PaymentRequestSpec spec;
    private BigDecimal cryptoAmount;
    private BigDecimal cryptoToleranceAmount;
    private String cryptoCurrency;
    private String cryptoAddress;


    public PaymentRequest(PaymentRequestSpec spec, BigDecimal cryptoAmount, BigDecimal cryptoToleranceAmount, String cryptoCurrency, String cryptoAddress) {
        this.spec = spec;
        this.cryptoAmount = cryptoAmount;
        this.cryptoCurrency = cryptoCurrency;
        this.cryptoAddress = cryptoAddress;
        this.cryptoToleranceAmount = cryptoToleranceAmount;
    }

    public PaymentRequestSpec getSpec() {
        return spec;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public String getPaymentURL() {
        return getPaymentURL(null,null);
    }

    public String getPaymentURL(String label) {
        return getPaymentURL(label,null);
    }

    public String getPaymentURL(String label, String message) {
        return "bitcoin:" + getCryptoAddress() + "?amount=" + getCryptoAmount().stripTrailingZeros().toPlainString() + (label == null ? "": "&label=" + URLEncoder.encode(label)) + (message == null ? "": "&message=" + URLEncoder.encode(message));
    }

    public BigDecimal getCryptoToleranceAmount() {
        return cryptoToleranceAmount;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "spec=" + spec +
                ", cryptoAmount=" + cryptoAmount +
                ", cryptoToleranceAmount=" + cryptoToleranceAmount +
                ", cryptoCurrency='" + cryptoCurrency + '\'' +
                ", cryptoAddress='" + cryptoAddress + '\'' +
                '}';
    }
}
