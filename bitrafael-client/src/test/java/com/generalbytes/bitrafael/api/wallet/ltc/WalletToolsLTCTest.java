package com.generalbytes.bitrafael.api.wallet.ltc;

import com.generalbytes.bitrafael.api.wallet.Classification;
import com.generalbytes.bitrafael.api.wallet.bch.WalletToolsBCH;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsLTCTest {

    @Test
    public void classifyWithPrefixTest() {
        WalletToolsLTC wt = new WalletToolsLTC();
        String input = "litecoin:MGnCpAEA9mJmPRKP61tAYKpnenBzb2xMAh";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("litecoin", classification.getPrefix());
        Assert.assertEquals("MGnCpAEA9mJmPRKP61tAYKpnenBzb2xMAh", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("LTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixTest() {
        WalletToolsLTC wt = new WalletToolsLTC();
        String input = "MGnCpAEA9mJmPRKP61tAYKpnenBzb2xMAh";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("MGnCpAEA9mJmPRKP61tAYKpnenBzb2xMAh", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("LTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyWithPrefixInvalidAddressTest() {
        WalletToolsLTC wt = new WalletToolsLTC();
        String input = "litecoin:MGnCpAEA9mJmPRKP61tAYKpnenBzb2xM9h";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("litecoin", classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyWithoutPrefixInvalidAddressTest() {
        WalletToolsLTC wt = new WalletToolsLTC();
        String input = "MGnCpAEA9mJmPRKP61tAYKpnenBzb2xM9h";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }
}
