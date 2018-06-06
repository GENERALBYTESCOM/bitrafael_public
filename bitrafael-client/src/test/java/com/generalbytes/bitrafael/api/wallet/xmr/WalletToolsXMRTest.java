package com.generalbytes.bitrafael.api.wallet.xmr;

import com.generalbytes.bitrafael.api.wallet.Classification;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsXMRTest {

    @Test
    public void classifyXMRPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "xmr:42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("xmr", classification.getPrefix());
        Assert.assertEquals("42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("XMR", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyInvalidAddressXMRPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "xmr:42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a5";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("xmr", classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyMoneroPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "monero:42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("monero", classification.getPrefix());
        Assert.assertEquals("42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("XMR", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyInvalidAddressMoneroPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "monero:42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a5";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("monero", classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyNoMoneroPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a9", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("XMR", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyInvalidAddressNoMoneroPrefixTest() {
        WalletToolsXMR wt = new WalletToolsXMR();
        String input = "42eftEhNJfbNmnZdB1JcvxavpPU2CrWJoJwuaBSubUZqMp14pNcqEHWdkb8kvT93Th9ntpcukkXu9Ljwuj3cCkHcFSDu5a5";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }
}
