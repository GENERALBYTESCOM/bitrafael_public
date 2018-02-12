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

package com.generalbytes.bitrafael.api.dto.rest;

import com.generalbytes.bitrafael.api.dto.TxTemplateInput;
import com.generalbytes.bitrafael.api.dto.TxTemplateOutput;

public class TxTemplateRequest {
    private TxTemplateInput[] froms;
    private TxTemplateOutput[] tos;
    private String changeAddress;
    private long fee;

    public TxTemplateRequest() {
    }

    public TxTemplateRequest(TxTemplateInput[] froms, TxTemplateOutput[] tos, String changeAddress, long fee) {
        this.froms = froms;
        this.tos = tos;
        this.changeAddress = changeAddress;
        this.fee = fee;
    }

    public TxTemplateInput[] getFroms() {
        return froms;
    }

    public TxTemplateOutput[] getTos() {
        return tos;
    }

    public String getChangeAddress() {
        return changeAddress;
    }

    public long getFee() {
        return fee;
    }

    public void setFroms(TxTemplateInput[] froms) {
        this.froms = froms;
    }

    public void setTos(TxTemplateOutput[] tos) {
        this.tos = tos;
    }

    public void setChangeAddress(String changeAddress) {
        this.changeAddress = changeAddress;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }
}
