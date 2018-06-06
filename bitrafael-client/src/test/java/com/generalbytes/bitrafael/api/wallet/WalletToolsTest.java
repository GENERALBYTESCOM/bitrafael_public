package com.generalbytes.bitrafael.api.wallet;

import org.junit.Assert;
import org.junit.Test;

public class WalletToolsTest {

    @Test
    public void classify1Test() {
        final WalletTools wt = new WalletTools();
        final String address = "bitcoincash:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        System.out.println(classification);
    }

    @Test
    public void classify2Test() {
        final WalletTools wt = new WalletTools();
        final String address = "1HGbVZAiUuvYQxxaU9d6EVZyPRDGHvWn7V";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
    }

    @Test
    public void classify3Test() {
        final WalletTools wt = new WalletTools();
        final String address = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
    }
}
