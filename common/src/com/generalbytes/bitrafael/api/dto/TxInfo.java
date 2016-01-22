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

import java.util.ArrayList;
import java.util.List;

public class TxInfo {
    private String txHash;
    private String blockHash;
    private long timestamp;
    private long receivedTimestamp;

    private long size;

    private List<InputInfo> inputInfos;
    private List<OutputInfo> outputInfos;

    public TxInfo(String txHash, String blockHash, long timestamp, long receivedTimestamp, long size) {
        this.txHash = txHash;
        this.blockHash = blockHash;
        this.timestamp = timestamp;
        this.receivedTimestamp = receivedTimestamp;
        this.size = size;
    }

    public void addInputInfo(InputInfo ii) {
        if (inputInfos == null) {
            inputInfos = new ArrayList<InputInfo>();
        }
        inputInfos.add(ii);
    }

    public void addOutputInfo(OutputInfo oi) {
        if (outputInfos == null) {
            outputInfos = new ArrayList<OutputInfo>();
        }
        outputInfos.add(oi);

    }

    public String getTxHash() {
        return txHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public long getSize() {
        return size;
    }

    public List<InputInfo> getInputInfos() {
        return inputInfos;
    }

    public List<OutputInfo> getOutputInfos() {
        return outputInfos;
    }

    @Override
    public String toString() {
        return "TxInfo{" +
                "txHash='" + txHash + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", timestamp=" + timestamp +
                ", receivedTimestamp=" + receivedTimestamp +
                ", size=" + size +
                '}';
    }
}
