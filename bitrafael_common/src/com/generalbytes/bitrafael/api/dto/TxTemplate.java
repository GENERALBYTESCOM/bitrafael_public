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

import java.util.Arrays;

public class TxTemplate {
    private TxTemplateInput[] inputs;
    private long minerFee;
    private String unsignedHex;

    public TxTemplateInput[] getInputs() {
        return inputs;
    }

    public void setInputs(TxTemplateInput[] inputs) {
        this.inputs = inputs;
    }

    public long getMinerFee() {
        return minerFee;
    }

    public void setMinerFee(long minerFee) {
        this.minerFee = minerFee;
    }

    public String getUnsignedHex() {
        return unsignedHex;
    }

    public void setUnsignedHex(String unsignedHex) {
        this.unsignedHex = unsignedHex;
    }

    @Override
    public String toString() {
        return "TxTemplate{" +
                "inputs=" + Arrays.toString(inputs) +
                ", minerFee=" + minerFee +
                ", unsignedHex='" + unsignedHex + '\'' +
                '}';
    }
}
