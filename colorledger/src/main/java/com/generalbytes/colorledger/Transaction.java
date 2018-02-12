/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.colorledger;


import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class Transaction implements Serializable{
    private byte[] nonce;
    private String fromAddress;
    private byte[] fromPublicKey;
    private String toAddress;
    private String coinColor;
    private long amount;
    private byte[] metaData;

    byte[] signature;
    private String hash;

    public Transaction(byte[] nonce, String fromAddress, byte[] fromPublicKey, String toAddress, String coinColor, long amount) {
        this(nonce,fromAddress,fromPublicKey,toAddress,coinColor,amount,null,null);
    }

    public Transaction(byte[] nonce, String fromAddress, byte[] fromPublicKey, String toAddress, String coinColor, long amount, byte[] metaData, byte[] signature) {
        this.nonce = nonce;
        this.fromAddress = fromAddress;
        this.fromPublicKey = fromPublicKey;
        this.toAddress = toAddress;
        this.coinColor = coinColor;
        this.amount = amount;
        this.metaData = metaData != null ? metaData : new byte[]{};
        this.signature = signature;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public byte[] getFromPublicKey() {
        return fromPublicKey;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getCoinColor() {
        return coinColor;
    }

    public long getAmount() {
        return amount;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getMetaData() {
        return metaData;
    }

    public String getHash() {
        if (hash == null) {
            byte[] txb = toByteArray();
            hash = Sha256Hash.of(txb).toString();
        }
        return hash;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(getDataToSign());
            bos.write(signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private byte[] getDataToSign() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(nonce);
            bos.write(fromAddress.getBytes());
            bos.write(fromPublicKey);
            bos.write(toAddress.getBytes());
            bos.write(coinColor.getBytes());
            bos.write(ByteBuffer.allocate(8).putLong(amount).array());
            bos.write(metaData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public void sign(ECKey key) {
        final ECKey.ECDSASignature s = key.sign(Sha256Hash.of(getDataToSign()));
        signature = s.encodeToDER();
    }

    public boolean verify(){
        Address a = new Address(MainNetParams.get(), Utils.sha256hash160(fromPublicKey));
        if (a.toBase58().equals(fromAddress)) {
            final ECKey.ECDSASignature sig = ECKey.ECDSASignature.decodeFromDER(signature);
            return ECKey.verify(Sha256Hash.hash(getDataToSign()), sig, fromPublicKey);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "nonce=" + Hex.toHexString(nonce) +
                ", fromAddress='" + fromAddress + '\'' +
                ", fromPublicKey=" + Hex.toHexString(fromPublicKey) +
                ", toAddress='" + toAddress + '\'' +
                ", coinColor='" + coinColor + '\'' +
                ", amount=" + amount +
                ", data=" + Hex.toHexString(metaData) +
                ", signature=" + Hex.toHexString(signature) +
                ", hash='" + getHash() + '\'' +
                '}';
    }
}
