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

import java.util.ArrayList;
import java.util.List;

public class BlockInfo {
    private String blockHash;
    private String previousBlockHash;
    private String nextBlockHash;
    private String merkleRoot;
    private long numberOfTransactions;
    private long outputTotal;
    private long transactionFees;
    private long height;
    private long timestamp;
    private long receivedTimestamp;
    private long bits;
    private long size;
    private long version;
    private long nonce;
    private long reward;
    private TxFeesInfo feesInfo;

    private List<TxInfo> txInfos;

    public BlockInfo(String blockHash, String previousBlockHash, String nextBlockHash, String merkleRoot, long numberOfTransactions, long outputTotal, long transactionFees, long height, long timestamp, long receivedTimestamp, long bits, long size, long version, long nonce, long reward) {
        this.blockHash = blockHash;
        this.previousBlockHash = previousBlockHash;
        this.nextBlockHash = nextBlockHash;
        this.merkleRoot = merkleRoot;
        this.numberOfTransactions = numberOfTransactions;
        this.outputTotal = outputTotal;
        this.transactionFees = transactionFees;
        this.height = height;
        this.timestamp = timestamp;
        this.receivedTimestamp = receivedTimestamp;
        this.bits = bits;
        this.size = size;
        this.version = version;
        this.nonce = nonce;
        this.reward = reward;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getNextBlockHash() {
        return nextBlockHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public long getOutputTotal() {
        return outputTotal;
    }

    public long getTransactionFees() {
        return transactionFees;
    }

    public long getHeight() {
        return height;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public long getBits() {
        return bits;
    }

    public long getSize() {
        return size;
    }

    public long getVersion() {
        return version;
    }

    public long getNonce() {
        return nonce;
    }

    public long getReward() {
        return reward;
    }

    public List<TxInfo> getTxInfos() {
        return txInfos;
    }

    public TxFeesInfo getFeesInfo() {
        return feesInfo;
    }

    public void setFeesInfo(TxFeesInfo feesInfo) {
        this.feesInfo = feesInfo;
    }

    public void addTXInfo(TxInfo txInfo) {
        if (txInfo == null) {
            return;
        }
        if (txInfos == null) {
            txInfos = new ArrayList<TxInfo>();
        }
        txInfos.add(txInfo);
    }

    public void setTxInfos(List<TxInfo> txInfos) {
        this.txInfos = txInfos;
    }
}
