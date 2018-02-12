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


import com.google.common.collect.Lists;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Page implements Serializable{
    public static final long VERSION_1 = 1;

    private long version;
    private String hash;
    private String previousHash;

    private long timestamp;
    private byte[] minerPublicKey;
    private List<String> txHashes;
    private byte[] signature;

    public Page(long version, String previousHash, long timestamp, byte[] minerPublicKey, List<String> txHashes) {
        this.version = version;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.minerPublicKey = minerPublicKey;
        this.txHashes = txHashes;
    }

    public String getHash() {
        if (hash == null) {
            byte[] txb = toByteArray();
            hash = Sha256Hash.of(txb).toString();
        }
        return hash;
    }

    public long getVersion() {
        return version;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getMinerPublicKey() {
        return minerPublicKey;
    }

    public List<String> getTxHashes() {
        return txHashes;
    }

    public byte[] getSignature() {
        return signature;
    }

    private byte[] toByteArray() {
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
        Sha256Hash root  = null;
        if (txHashes.size() == 0) {
            root = Sha256Hash.of(new byte[32]);
        }else {
            List<Sha256Hash> transactionHashes = Lists.newArrayList();
            for (int i = 0; i < txHashes.size(); i++) {
                String txhash = txHashes.get(i);
                transactionHashes.add(Sha256Hash.wrap(txhash));
            }
            byte[] includeBits = new byte[2];
            PartialMerkleTree pmt = PartialMerkleTree.buildFromLeaves(MainNetParams.get(), includeBits, transactionHashes);
            root = pmt.getTxnHashAndMerkleRoot(new ArrayList<Sha256Hash>());
        }


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(ByteBuffer.allocate(8).putLong(version).array());
            bos.write(Sha256Hash.wrap(previousHash).getBytes());
            bos.write(ByteBuffer.allocate(8).putLong(timestamp).array());
            bos.write(minerPublicKey);
            bos.write(root.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public void sign(ECKey key) {
        final ECKey.ECDSASignature s = key.sign(Sha256Hash.of(getDataToSign()));
        signature = s.encodeToDER();
    }

    public boolean verify(Set<String> minerAddresses){
        Address a = new Address(MainNetParams.get(), Utils.sha256hash160(minerPublicKey));
        if (minerAddresses.contains(a.toBase58())) {
            final ECKey.ECDSASignature sig = ECKey.ECDSASignature.decodeFromDER(signature);
            return ECKey.verify(Sha256Hash.hash(getDataToSign()), sig, minerPublicKey);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Page{" +
                "version=" + version +
                ", hash='" + getHash() + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", timestamp=" + timestamp +
                ", minerPublicKey=" +  Hex.toHexString(minerPublicKey) +
                ", txHashes=" + txHashes +
                ", signature=" +  Hex.toHexString(signature) +
                '}';
    }
}
