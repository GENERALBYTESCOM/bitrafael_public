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

import java.util.Map;

public class TxFees {
    private long totalFee;
    private Map<String,Long> transactionFees;

    public TxFees() {
    }

    public TxFees(long totalFee, Map<String, Long> transactionFees) {
        this.totalFee = totalFee;
        this.transactionFees = transactionFees;
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    public Map<String, Long> getTransactionFees() {
        return transactionFees;
    }

    public void setTransactionFees(Map<String, Long> transactionFees) {
        this.transactionFees = transactionFees;
    }

    @Override
    public String toString() {
        return "TxFees{" +
                "totalFee=" + totalFee +
                ", transactionFees=" + transactionFees +
                '}';
    }
}
