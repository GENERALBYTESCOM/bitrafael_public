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
package com.generalbytes.bitrafael.api.transaction;

import com.generalbytes.bitrafael.api.dto.InputInfo;
import com.generalbytes.bitrafael.api.dto.OutputInfo;
import com.generalbytes.bitrafael.api.dto.TxInfo;

import java.util.ArrayList;
import java.util.List;

public class Transaction implements ITransaction{
    private TxInfo parentTxInfo;

    private String relativeToAddress;
    private boolean directionSend;
    private long txAmount;
    private long fee;

    public Transaction(TxInfo parentTxInfo, String relativeToAddress, boolean directionSend, long txAmount, long fee) {
        this.parentTxInfo = parentTxInfo;
        this.relativeToAddress = relativeToAddress;
        this.directionSend = directionSend;
        this.txAmount = txAmount;
        this.fee = fee;
    }

    public static List<Transaction> buildTransactions(List<TxInfo> txInfos, String relativeToAddress) {
        List<Transaction> result = new ArrayList<Transaction>();
        for (int i = 0; i < txInfos.size(); i++) {
            TxInfo txInfo = txInfos.get(i);
            result.add(buildTransaction(txInfo,relativeToAddress));
        }
        return result;
    }

    public static Transaction buildTransaction(TxInfo txInfo, String relativeToAddress) {
        if (txInfo == null) {
            return null;
        }
        if (relativeToAddress != null) {
            long inputs = 0;
            long outputs = 0;
            long addrInput = 0;
            long addrOutput = 0;
            final List<InputInfo> inputInfos = txInfo.getInputInfos();
            for (int j = 0; j < inputInfos.size(); j++) {
                InputInfo inputInfo = inputInfos.get(j);
                inputs += inputInfo.getValue();
                if (inputInfo.getAddress().equals(relativeToAddress)) {
                    addrInput += inputInfo.getValue();
                }
            }
            final List<OutputInfo> outputInfos = txInfo.getOutputInfos();
            for (int j = 0; j < outputInfos.size(); j++) {
                OutputInfo outputInfo = outputInfos.get(j);
                outputs += outputInfo.getValue();
                if (outputInfo.getAddress().equals(relativeToAddress)) {
                    addrOutput += outputInfo.getValue();
                }
            }

            long txAmount = inputs > 0 ? addrOutput - addrInput : outputs;
            long fee = inputs - outputs;
            boolean directionSend = addrOutput - addrInput < 0;
            return new Transaction(txInfo,relativeToAddress,directionSend,txAmount,fee);
        }else{
            long inputs = 0;
            long outputs = 0;
            long addrInput = 0;
            long addrOutput = 0;

            String address = guessAddress(txInfo);

            final List<InputInfo> inputInfos = txInfo.getInputInfos();
            for (int j = 0; j < inputInfos.size(); j++) {
                InputInfo inputInfo = inputInfos.get(j);
                inputs+=inputInfo.getValue();
                if (inputInfo.getAddress().equals(address)) {
                    addrInput+=inputInfo.getValue();
                }
            }
            final List<OutputInfo> outputInfos = txInfo.getOutputInfos();
            for (int j = 0; j < outputInfos.size(); j++) {
                OutputInfo outputInfo = outputInfos.get(j);
                outputs+=outputInfo.getValue();
                if (outputInfo.getAddress().equals(address)) {
                    addrOutput+=outputInfo.getValue();
                }
            }
            long fee = inputs - outputs;
            boolean directionSend = addrOutput - addrInput < 0;
            long txAmount = inputs > 0 ? addrOutput - addrInput : outputs;
            return new Transaction(txInfo,address,directionSend,txAmount,fee);
        }
    }

    private static String guessAddress(TxInfo txInfo) {
        final List<InputInfo> inputInfos = txInfo.getInputInfos();
        final List<OutputInfo> outputInfos = txInfo.getOutputInfos();
        for (int i = 0; i < inputInfos.size(); i++) {
            InputInfo inputInfo =  inputInfos.get(i);
            for (int j = 0; j < outputInfos.size(); j++) {
                OutputInfo outputInfo = outputInfos.get(j);
                if (outputInfo.getAddress().equals(inputInfo.getAddress())) {
                    return outputInfo.getAddress();
                }
            }
        }
        return inputInfos.get(0).getAddress();
    }


    @Override
    public String getTxHash() {
        return parentTxInfo.getTxHash();
    }

    @Override
    public String getBlockHash() {
        return parentTxInfo.getBlockHash();
    }

    @Override
    public long getTimestamp() {
        return parentTxInfo.getTimestamp();
    }

    @Override
    public long getReceivedTimestamp() {
        return parentTxInfo.getReceivedTimestamp();
    }

    @Override
    public long getSize() {
        return parentTxInfo.getSize();
    }

    @Override
    public List<InputInfo> getInputInfos() {
        return parentTxInfo.getInputInfos();
    }

    @Override
    public List<OutputInfo> getOutputInfos() {
        return parentTxInfo.getOutputInfos();
    }

    @Override
    public long getBlockHeight() {
        return parentTxInfo.getBlockHeight();
    }

    @Override
    public long getConfirmations() {
        return parentTxInfo.getConfirmations();
    }

    @Override
    public boolean isDirectionSend() {
        return directionSend;
    }

    @Override
    public long getAmount() {
        return txAmount;
    }

    @Override
    public long getFee() {
        return fee;
    }

    public String getRelativeToAddress() {
        return relativeToAddress;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "directionSend=" + directionSend +
                ", txAmount=" + txAmount +
                ", relativeToAddress='" + relativeToAddress + '\'' +
                '}';
    }
}
