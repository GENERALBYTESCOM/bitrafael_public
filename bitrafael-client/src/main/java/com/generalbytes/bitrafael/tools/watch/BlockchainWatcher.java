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

package com.generalbytes.bitrafael.tools.watch;


import com.generalbytes.bitrafael.server.api.Client;
import com.generalbytes.bitrafael.server.api.api.IClient;
import com.generalbytes.bitrafael.server.api.dto.TxInfo;
import com.generalbytes.bitrafael.tools.api.watch.IBlockchainWatcher;
import com.generalbytes.bitrafael.tools.api.watch.IBlockchainWatcherListener;
import com.generalbytes.bitrafael.tools.api.watch.IBlockchainWatcherTransactionListener;
import com.generalbytes.bitrafael.tools.api.watch.IBlockchainWatcherWalletListener;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BlockchainWatcher implements IBlockchainWatcher {
    private static final int WATCH_BLOCKS_PERIOD_IN_MS = 1 * 60 * 1000;
    private static final int WATCH_WALLETS_PERIOD_IN_MS = 1000;

    private final List<TransactionRecord> transactionRecords = new LinkedList<TransactionRecord>();
    private final List<WalletRecord> walletRecords = new LinkedList<WalletRecord>();
    private final List<IBlockchainWatcherListener> listeners = new LinkedList<IBlockchainWatcherListener>();
    private Map<String, IClient> clients = null;
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private Map<String,Long> lastBlockChainHeights = null;


    class TransactionRecord {
        private String transactionHash;
        private String cryptoCurrency;
        private IBlockchainWatcherTransactionListener listener;
        private Object tag;
        private int lastNumberOfConfirmations;


        TransactionRecord(String transactionHash, String cryptoCurrency, IBlockchainWatcherTransactionListener listener, Object tag) {
            this.transactionHash = transactionHash;
            this.cryptoCurrency = cryptoCurrency;
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

        public String getCryptoCurrency() {
            return cryptoCurrency;
        }
    }

    class WalletRecord {
        private String walletAddress;
        private String cryptoCurrency;
        private IBlockchainWatcherWalletListener listener;
        private Object tag;
        private TxInfo lastTxInfo;
        private List<TxInfo> oldTxInfos = new ArrayList<TxInfo>();


        public WalletRecord(String walletAddress, String cryptoCurrency, IBlockchainWatcherWalletListener listener, Object tag, TxInfo lastTxInfo) {
            this.walletAddress = walletAddress;
            this.cryptoCurrency = cryptoCurrency;
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

        public String getCryptoCurrency() {
            return cryptoCurrency;
        }
    }

    public BlockchainWatcher() {
        this(IClient.BTC);
    }

    public BlockchainWatcher(String cryptoCurrency) {
        this(new String[]{cryptoCurrency});
    }

    public BlockchainWatcher(String[] cryptoCurrencies) {
        lastBlockChainHeights = new HashMap<String,Long>();
        clients = new HashMap<String,IClient>();
        for (int i = 0; i < cryptoCurrencies.length; i++) {
            String cryptoCurrency = cryptoCurrencies[i];
            if (IClient.BTC.equalsIgnoreCase(cryptoCurrency)) {
                clients.put(IClient.BTC, new Client(System.getProperty("coin.cz", "https://coin.cz"), IClient.BTC));
                lastBlockChainHeights.put(IClient.BTC,-1L);
            }else if (IClient.LTC.equalsIgnoreCase(cryptoCurrency)) {
                clients.put(IClient.LTC, new Client(System.getProperty("coin.cz", "https://coin.cz"), IClient.LTC));
                lastBlockChainHeights.put(IClient.LTC,-1L);
            }
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
    public void addWallet(String walletAddress, String cryptoCurrency, IBlockchainWatcherWalletListener listener, Object tag) {
        final TxInfo last = getClient(cryptoCurrency).getAddressLastTransactionInfo(walletAddress);
        final WalletRecord wr = new WalletRecord(walletAddress, cryptoCurrency, listener, tag, last);
        synchronized (walletRecords) {
            walletRecords.add(wr);
        }
    }

    private IClient getClient(String cryptoCurrency) {
        return clients.get(cryptoCurrency);
    }

    @Override
    public Object removeWallet(String walletAddress, String cryptoCurrency) {
        synchronized (walletRecords) {
            for (int i = 0; i < walletRecords.size(); i++) {
                WalletRecord record = walletRecords.get(i);
                if (record.getWalletAddress().equals(walletAddress)) {
                    walletRecords.remove(record);
                    record.getListener().removedWalletFromWatch(record.walletAddress, record.cryptoCurrency, record.tag);
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
                    walletRecord.getListener().removedWalletFromWatch(walletRecord.walletAddress, walletRecord.cryptoCurrency,  walletRecord.tag);
                    return walletRecord.getTag();
                }
            }
        }
        return null;
    }

    public void addTransaction(String transactionHash, String cryptoCurrency, IBlockchainWatcherTransactionListener l, Object tag) {
        synchronized (transactionRecords) {
            final TransactionRecord e = new TransactionRecord(transactionHash, cryptoCurrency, l, tag);

            transactionRecords.add(e);
        }
    }

    public Object removeTransaction(String transactionHash, String cryptoCurrency) {
        synchronized (transactionRecords) {
            for (int i = 0; i < transactionRecords.size(); i++) {
                TransactionRecord transactionRecord = transactionRecords.get(i);
                if (transactionRecord.getTransactionHash().equals(transactionHash) && transactionRecord.getCryptoCurrency().equalsIgnoreCase(cryptoCurrency)) {
                    transactionRecords.remove(transactionRecord);
                    transactionRecord.getListener().removedTransactionFromWatch(transactionRecord.transactionHash, transactionRecord.cryptoCurrency, transactionRecord.tag);
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
                    transactionRecord.getListener().removedTransactionFromWatch(transactionRecord.transactionHash, transactionRecord.cryptoCurrency, transactionRecord.tag);
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
        final Set<String> cryptoCurrencies = lastBlockChainHeights.keySet();
        for (String cryptoCurrency : cryptoCurrencies) {
            long currentBlockChainHeight  = getClient(cryptoCurrency).getCurrentBlockchainHeight();
            long lastBlockChainHeight = lastBlockChainHeights.get(cryptoCurrency);
            if (currentBlockChainHeight != lastBlockChainHeight && currentBlockChainHeight != 0) {
                if (lastBlockChainHeight == -1) {
                    //skip first iteration
                    lastBlockChainHeight = currentBlockChainHeight;
                    lastBlockChainHeights.put(cryptoCurrency,lastBlockChainHeight);
                }else {
                    lastBlockChainHeight = currentBlockChainHeight;
                    lastBlockChainHeights.put(cryptoCurrency,lastBlockChainHeight);
                    synchronized (listeners) {
                        for (int i = 0; i < listeners.size(); i++) {
                            IBlockchainWatcherListener listener = listeners.get(i);
                            listener.newBlockMined(lastBlockChainHeight,cryptoCurrency);
                        }
                    }
                    List<TransactionRecord> res = null;
                    synchronized (transactionRecords) {
                        res = new LinkedList<TransactionRecord>(this.transactionRecords);
                    }
                    if (!res.isEmpty()) {
                        for (int i = 0; i < res.size(); i++) {
                            TransactionRecord transactionRecord = res.get(i);
                            if (cryptoCurrency.equalsIgnoreCase(transactionRecord.getCryptoCurrency())) {
                                checkRecord(transactionRecord, currentBlockChainHeight);
                            }
                        }
                    }
                }
            }

        }
    }

    private void checkForWalletChanges() {
        List<WalletRecord> rec = null;
        synchronized (walletRecords) {
            rec = new ArrayList<WalletRecord>(walletRecords);
            if (rec.isEmpty()) {
                return;
            }
        }
        final Set<String> cryptoCurrencies = clients.keySet();
        for (String cryptoCurrency : cryptoCurrencies) {
            List<String> addresses = new ArrayList<String>();
            for (int i = 0; i < walletRecords.size(); i++) {
                WalletRecord record = walletRecords.get(i);
                if (cryptoCurrency.equalsIgnoreCase(record.getCryptoCurrency())) {
                    addresses.add(record.getWalletAddress());
                }
            }
            final Map<String, TxInfo> results = getClient(cryptoCurrency).getAddressesLastTransactionInfos(addresses);
            if (results == null || results.isEmpty()) {
                continue;
            }
            for (int i = 0; i < rec.size(); i++) {
                WalletRecord record = rec.get(i);
                final TxInfo linfo = results.get(record.getWalletAddress());

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
                                record.getListener().walletContainsChanged(record.getWalletAddress(), record.getCryptoCurrency(), record.getTag(), record.getLastTxInfo());
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkRecord(TransactionRecord transactionRecord, long currentBlockChainHeight) {
        String txHash = transactionRecord.getTransactionHash();
        if (transactionRecord.getListener() != null) {
            transactionRecord.getListener().newBlockMined(txHash, transactionRecord.cryptoCurrency, transactionRecord.tag, currentBlockChainHeight);
        }
        long transactionHeight = getClient(transactionRecord.getCryptoCurrency()).getTransactionHeight(txHash);
        if (transactionHeight != 0) {
            //transaction is in block
            int numberOfConfirmations = 1 + (int)(currentBlockChainHeight - transactionHeight);
            if (numberOfConfirmations > transactionRecord.lastNumberOfConfirmations) {
                transactionRecord.lastNumberOfConfirmations = numberOfConfirmations;
                if (transactionRecord.getListener() != null) {
                    transactionRecord.getListener().numberOfConfirmationsChanged(txHash, transactionRecord.cryptoCurrency, transactionRecord.tag, numberOfConfirmations);
                }
            }
        }
    }

}
