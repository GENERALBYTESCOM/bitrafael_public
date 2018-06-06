package com.generalbytes.bitrafael.api.wallet.btc;

import com.generalbytes.bitrafael.api.wallet.Classification;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsBTCTest {

    @Test
    public void classifyWithPrefixTest() {
        WalletToolsBTC wt = new WalletToolsBTC();
        String input = "bitcoin:3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("bitcoin", classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixTest() {
        WalletToolsBTC wt = new WalletToolsBTC();
        String input = "3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }
}
