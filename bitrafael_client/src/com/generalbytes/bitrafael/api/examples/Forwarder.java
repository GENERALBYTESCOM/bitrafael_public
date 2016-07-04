package com.generalbytes.bitrafael.api.examples;

import com.generalbytes.bitrafael.api.client.Client;
import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.dto.TxInfo;
import com.generalbytes.bitrafael.api.wallet.WalletTools;
import com.generalbytes.bitrafael.api.watch.AbstractBlockchainWatcherWalletListener;
import com.generalbytes.bitrafael.api.watch.BlockchainWatcher;

import java.math.BigDecimal;

/**
 * Created by b00lean on 29.6.16.
 */
public class Forwarder {
    public static void main(String[] args) {
        WalletTools wt = new WalletTools();
        BlockchainWatcher bw = new BlockchainWatcher();
        final IClient c = new Client("https://coin.cz");

        final String[] privatekeys = {
                "L54zVtKQ3kY8CuoEjhijsgTiGZVWhEHDcCeTZc7ssYjwMLemQ5xu",
                "L2dp5ombew15aEtTjHGAPEZP4nvMNJENCiwUHgi9HJHmj923e7vY",
                "L4L8c5s1Wqzi1gKTJ8iExJbzgRLBbsD5z4Kr6Hn7Kr94JoipQzF5",
                "L2LHh5t4un3oHkJnwsrXg6jDXYJFgHKvMXL4GhTfn21gHvz64Jum",
                "KwPEN93LCYeBDee3yjSGfmi8QaECs5wrv5Yv59o1S5LMMYdoKBPq",
                "KzQWKBGobymynHrJZzmorhtS3hzs5AXnAe7XG35X4rrkwHAyNMEX",
                "L1sCd7mek2u1wDPzfnL8iwiqTq9tQVeky785KbEqR8QFT3mEjq3a",
                "KyCMXa5KBS4yeqimdyiyphFi9YnGkcUAxy76mjgYxNUpzvNEYdWg",
                "L1DKyeb4tXxBaxDnrt58Yn2AimbJbYSps2VN63Ltv517XrUzPR2A",
                "KwERsxzcYFX7oihYCGeiPDLhTyxGuTP73utzrYTfETDeceJXFAnU",
                "L4Bfx3a7ovZTtDyX3pf2cz7jMtS2uZP8mRKoZbqHuATpUwjMfyA2"};
        final String toAddress = "19juqmttHLW3PiVuziUiPeP75xCi7uP2FH";

        for (int i = 0; i < privatekeys.length; i++) {
            final String privatekey = privatekeys[i];
            final String fromAddress = wt.getWalletAddressFromPrivateKey(privatekey);

            System.out.println("Watching " + fromAddress + " for transactions...");
            bw.addWallet(fromAddress, new AbstractBlockchainWatcherWalletListener() {
                @Override
                public void walletContainsChanged(String walletAddress, Object tag, TxInfo tx) {
                    System.out.println("New transaction on " + tag + " (" + walletAddress +") txhash: " + tx.getTxHash());
                    final BigDecimal addressBalance = c.getAddressBalance(fromAddress);
                    if (addressBalance.compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println("Sending coins to " + toAddress);
                        final BigDecimal fee = new BigDecimal("0.0002");
                        final String send = c.send(privatekey, addressBalance.subtract(fee), toAddress, fee);
                        System.out.println("send = " + send);
                    }
                }
            },"MyWallet_" + i);
            bw.start();

        }


        System.out.println("Awaiting payments...");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
