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

public class TxReceipt {
    private String txHash;
    private String txHex;
    private boolean broadcasted;

    public TxReceipt() {
    }

    public TxReceipt(String txHash, String txHex, boolean broadcasted) {
        this.txHash = txHash;
        this.txHex = txHex;
        this.broadcasted = broadcasted;
    }


    public String getTxHash() {
        return txHash;
    }

    public String getTxHex() {
        return txHex;
    }

    public boolean isBroadcasted() {
        return broadcasted;
    }

    @Override
    public String toString() {
        return "TxReceipt{" +
                "txHash='" + txHash + '\'' +
                ", txHex='" + txHex + '\'' +
                ", broadcasted=" + broadcasted +
                '}';
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public void setTxHex(String txHex) {
        this.txHex = txHex;
    }

    public void setBroadcasted(boolean broadcasted) {
        this.broadcasted = broadcasted;
    }
}
