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

import java.util.List;

public class ColorLedger implements IColorLedger {
    private IColorLedgerStorage storage;

    public ColorLedger(IColorLedgerStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean addEmitter(String color, String address) {
        return storage.addEmitter(color, address);
    }

    @Override
    public boolean removeEmitter(String color, String address) {
        return storage.removeEmitter(color,address);
    }

    @Override
    public boolean transferCoins(Transaction tx) {
        if (!storage.hasTransaction(tx.getHash())) {
            if (tx.verify()) {

                boolean checkBalance = !isEmitter(tx.getCoinColor(), tx.getFromAddress());
                if (checkBalance) {
                    if (getBalance(tx.getFromAddress(),tx.getCoinColor()) < tx.getAmount()) {
                        return false; //not enough coins
                    }
                }
                return storage.addTransaction(tx);
            }else{
                return false;//failed verification
            }
        }
        return false;
    }

    private boolean isEmitter(String coinColor, String address) {
        final List<String> emitters = storage.getEmitters(coinColor);
        for (int i = 0; i < emitters.size(); i++) {
            String e = emitters.get(i);
            if (e.equals(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getBalance(String address, String coinColor) {
        final List<Transaction> transactions = getTransactions(address, coinColor);
        long balance = 0;
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.getCoinColor().equals(coinColor)) {
                if (t.getFromAddress().equals(address)) {
                    balance -= t.getAmount();
                }
                if (t.getToAddress().equals(address)) {
                    balance += t.getAmount();
                }
            }
        }
        return balance;
    }

    @Override
    public Transaction getTransaction(String txHash) {
        return storage.getTransaction(txHash);
    }

    @Override
    public List<Transaction> getTransactions(String address, String coinColor) {
        return storage.getTransactions(address, coinColor);
    }
}
