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

package com.generalbytes.bitrafael.api.watch;


import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.TxInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BlockchainWatcher implements IBlockchainWatcher{
    private static final int WATCH_BLOCKS_PERIOD_IN_MS = 1 * 60 * 1000;
    private static final int WATCH_WALLETS_PERIOD_IN_MS = 1000;

    private final List<TransactionRecord> transactionRecords = new LinkedList<TransactionRecord>();
    private final List<WalletRecord> walletRecords = new LinkedList<WalletRecord>();
    private final List<IBlockchainWatcherListener> listeners = new LinkedList<IBlockchainWatcherListener>();
    private IClient client = new Client("https://coin.cz");
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private long lastBlockChainHeight = -1;



    class TransactionRecord {
        private String transactionHash;
        private IBlockchainWatcherTransactionListener listener;
        private Object tag;
        private int lastNumberOfConfirmations;


        TransactionRecord(String transactionHash, IBlockchainWatcherTransactionListener listener, Object tag) {
            this.transactionHash = transactionHash;
            this.listener = listener;
            this.tag = tag;
            this.lastNumberOfConfirmations = -1;
        }

        public String getTransactionHash() {
            return transactionHash;
        }

        public Object getTag() {
            return tag;
        }

        public IBlockchainWatcherTransactionListener getListener() {
            return listener;
        }
    }

    class WalletRecord {
        private String walletAddress;
        private IBlockchainWatcherWalletListener listener;
        private Object tag;
        private TxInfo lastTxInfo;
        private List<TxInfo> oldTxInfos = new ArrayList<>();


        public WalletRecord(String walletAddress, IBlockchainWatcherWalletListener listener, Object tag, TxInfo lastTxInfo) {
            this.walletAddress = walletAddress;
            this.listener = listener;
            this.tag = tag;
            this.lastTxInfo = lastTxInfo;
            if (lastTxInfo != null) {
                this.oldTxInfos.add(lastTxInfo);
            }
        }


        public String getWalletAddress() {
            return walletAddress;
        }

        public Object getTag() {
            return tag;
        }

        public IBlockchainWatcherWalletListener getListener() {
            return listener;
        }

        public TxInfo getLastTxInfo() {
            return lastTxInfo;
        }

        public void setLastTxInfo(TxInfo lastTxInfo) {
            this.lastTxInfo = lastTxInfo;
        }

        public List<TxInfo> getOldTxInfos() {
            return oldTxInfos;
        }

    }

    @Override
    public void addBlockchainListener(IBlockchainWatcherListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeBlockchainListener(IBlockchainWatcherListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void addWallet(String walletAddress, IBlockchainWatcherWalletListener listener, Object tag) {
        final TxInfo last = client.getAddressLastTransactionInfo(walletAddress);
        final WalletRecord wr = new WalletRecord(walletAddress, listener, tag, last);
        synchronized (walletRecords) {
            walletRecords.add(wr);
        }
    }

    @Override
    public Object removeWallet(String walletAddress) {
        synchronized (walletRecords) {
            for (int i = 0; i < walletRecords.size(); i++) {
                WalletRecord record = walletRecords.get(i);
                if (record.getWalletAddress().equals(walletAddress)) {
                    walletRecords.remove(record);
                    record.getListener().removedWalletFromWatch(record.walletAddress, record.tag);
                    return record.getTag();
                }
            }
        }
        return null;
    }

    @Override
    public Object removeWallet(IBlockchainWatcherWalletListener listener) {
        synchronized (walletRecords) {
            for (int i = 0; i < walletRecords.size(); i++) {
                WalletRecord walletRecord = walletRecords.get(i);
                if (walletRecord.getListener() == listener) {
                    walletRecords.remove(walletRecord);
                    walletRecord.getListener().removedWalletFromWatch(walletRecord.walletAddress, walletRecord.tag);
                    return walletRecord.getTag();
                }
            }
        }
        return null;
    }

    public void addTransaction(String transactionHash, IBlockchainWatcherTransactionListener l, Object tag) {
        synchronized (transactionRecords) {
            final TransactionRecord e = new TransactionRecord(transactionHash, l, tag);

            transactionRecords.add(e);
        }
    }

    public Object removeTransaction(String transactionHash) {
        synchronized (transactionRecords) {
            for (int i = 0; i < transactionRecords.size(); i++) {
                TransactionRecord transactionRecord = transactionRecords.get(i);
                if (transactionRecord.getTransactionHash().equals(transactionHash)) {
                    transactionRecords.remove(transactionRecord);
                    transactionRecord.getListener().removedTransactionFromWatch(transactionRecord.transactionHash, transactionRecord.tag);
                    return transactionRecord.getTag();
                }
            }
        }
        return null;
    }

    public Object removeTransaction(IBlockchainWatcherTransactionListener listener) {
        synchronized (transactionRecords) {
            for (int i = 0; i < transactionRecords.size(); i++) {
                TransactionRecord transactionRecord = transactionRecords.get(i);
                if (transactionRecord.getListener() == listener) {
                    transactionRecords.remove(transactionRecord);
                    transactionRecord.getListener().removedTransactionFromWatch(transactionRecord.transactionHash, transactionRecord.tag);
                    return transactionRecord.getTag();
                }
            }
        }
        return null;
    }

    public void stop() {
        if (service != null) {
            service.shutdownNow();
        }
    }

    public synchronized void start() {
        stop();
        service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("BlockchainWatcher");
                thread.setDaemon(true);
                return thread;
            }
        });

        //check for new blocks
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                checkForNewBlocks();
            }
        },0,WATCH_BLOCKS_PERIOD_IN_MS, TimeUnit.MILLISECONDS);

        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                checkForWalletChanges();
            }
        },0,WATCH_WALLETS_PERIOD_IN_MS, TimeUnit.MILLISECONDS);


    }

    private void checkForNewBlocks() {
        long currentBlockChainHeight  = client.getCurrentBlockchainHeight();
        if (currentBlockChainHeight != lastBlockChainHeight && currentBlockChainHeight != 0) {
            if (lastBlockChainHeight == -1) {
                //skip first iteration
                lastBlockChainHeight = currentBlockChainHeight;
            }else {
                lastBlockChainHeight = currentBlockChainHeight;
                synchronized (listeners) {
                    for (int i = 0; i < listeners.size(); i++) {
                        IBlockchainWatcherListener listener = listeners.get(i);
                        listener.newBlockMined(lastBlockChainHeight);
                    }
                }
                List<TransactionRecord> res = null;
                synchronized (transactionRecords) {
                    res = new LinkedList<TransactionRecord>(this.transactionRecords);
                }
                if (!res.isEmpty()) {
                    for (int i = 0; i < res.size(); i++) {
                        TransactionRecord transactionRecord = res.get(i);
                        checkRecord(transactionRecord, currentBlockChainHeight);
                    }
                }
            }
        }
    }

    private void checkForWalletChanges() {
        List<WalletRecord> rec = null;
        synchronized (walletRecords) {
            rec = new ArrayList<WalletRecord>(walletRecords);
        }

        for (int i = 0; i < rec.size(); i++) {
            WalletRecord record = rec.get(i);
            final TxInfo linfo = client.getAddressLastTransactionInfo(record.getWalletAddress());
            if (linfo != null) {
                if (record.getLastTxInfo() == null || !linfo.getTxHash().equals(record.getLastTxInfo().getTxHash())) {
                    boolean isNewTx = true;
                    final List<TxInfo> oldTxInfos = record.getOldTxInfos();
                    for (int j = 0; j < oldTxInfos.size(); j++) {
                        TxInfo txInfo = oldTxInfos.get(j);
                        if (txInfo.getTxHash().equals(linfo.getTxHash())) {
                            isNewTx = false;
                            break;
                        }
                    }
                    record.setLastTxInfo(linfo);
                    if (isNewTx) {
                        oldTxInfos.add(linfo);
                        if (record.getListener() != null) {
                            record.getListener().walletContainsChanged(record.getWalletAddress(), record.getTag(), record.getLastTxInfo());
                        }
                    }
                }
            }
        }
    }

    private void checkRecord(TransactionRecord transactionRecord, long currentBlockChainHeight) {
        String txHash = transactionRecord.getTransactionHash();
        if (transactionRecord.getListener() != null) {
            transactionRecord.getListener().newBlockMined(txHash, transactionRecord.tag, currentBlockChainHeight);
        }
        long transactionHeight = client.getTransactionHeight(txHash);
        if (transactionHeight != 0) {
            //transaction is in block
            int numberOfConfirmations = 1 + (int)(currentBlockChainHeight - transactionHeight);
            if (numberOfConfirmations > transactionRecord.lastNumberOfConfirmations) {
                transactionRecord.lastNumberOfConfirmations = numberOfConfirmations;
                if (transactionRecord.getListener() != null) {
                    transactionRecord.getListener().numberOfConfirmationsChanged(txHash, transactionRecord.tag, numberOfConfirmations);
                }
            }
        }
    }

}
