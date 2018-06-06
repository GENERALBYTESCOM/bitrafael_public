package com.generalbytes.bitrafael.api.wallet.bch;

import com.generalbytes.bitrafael.api.wallet.Classification;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsBCHTest {

    @Test
    public void classifyWithPrefixTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "bitcoincash:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("bitcoincash", classification.getPrefix());
        Assert.assertEquals("qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixNewAddressTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixOldAddressTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyInvalidAddressWithPrefixTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "bitcoincash:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s5";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("bitcoincash", classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyInvalidAddressWithoutPrefixTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s5";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixInvalidOldAddressTest() {
        WalletToolsBCH wt = new WalletToolsBCH();
        String input = "3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHV";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }
}
