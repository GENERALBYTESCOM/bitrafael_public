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

public class TxSignature {
    private String address;
    private String publicKey;
    private String hashToSign;
    private String signature;

    public TxSignature() {
    }

    public TxSignature(String address, String hashToSign) {
        this.address = address;
        this.hashToSign = hashToSign;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getHashToSign() {
        return hashToSign;
    }

    public void setHashToSign(String hashToSign) {
        this.hashToSign = hashToSign;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "TxSignature{" +
                "address='" + address + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", hashToSign='" + hashToSign + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
