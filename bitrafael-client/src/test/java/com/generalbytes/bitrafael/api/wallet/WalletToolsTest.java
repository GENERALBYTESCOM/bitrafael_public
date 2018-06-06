package com.generalbytes.bitrafael.api.wallet;

import com.generalbytes.bitrafael.api.wallet.bch.WalletToolsBCH;
import com.generalbytes.bitrafael.api.wallet.btc.WalletToolsBTC;
import com.generalbytes.bitrafael.api.wallet.dash.WalletToolsDASH;
import com.generalbytes.bitrafael.api.wallet.ltc.WalletToolsLTC;
import com.generalbytes.bitrafael.api.wallet.xmr.WalletToolsXMR;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsTest {

    @Test
    public void classifyXMRPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyMoneroPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyMoneroNoPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyDASHWithPrefixTest() {
        WalletTools wt = new WalletTools();
        String input = "dash:XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("dash", classification.getPrefix());
        Assert.assertEquals("XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("DASH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyDASHWithoutPrefixTest() {
        WalletTools wt = new WalletTools();
        String input = "XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("DASH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyLTCWithPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyLTCWithBitcoinPrefixTest() {
        WalletTools wt = new WalletTools();
        String input = "bitcoin:MGnCpAEA9mJmPRKP61tAYKpnenBzb2xMAh";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("bitcoin", classification.getPrefix());
        Assert.assertEquals(null, classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(null, classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void classifyLTCWithoutPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyBCHWithPrefixTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyBCHWithoutPrefixNewAddressTest() {
        WalletTools wt = new WalletTools();
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
    public void classifyBCHWithPrefixOldAddressTest() {
        WalletTools wt = new WalletTools();
        String input = "bitcoincash:3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals("bitcoincash", classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyBCHWithoutPrefixOldAddressTest() {
        WalletTools wt = new WalletTools();
        String input = "3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyBTCWithPrefixTest() {
        final WalletTools wt = new WalletTools();
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
    public void classifyBTCWithoutPrefixTest() {
        final WalletTools wt = new WalletTools();
        String input = "3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("3MoQNyXoitg3rLhsu3acMhPSq4gfJjVqHM", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyBCHNewWithPrefixTest() {
        final WalletTools wt = new WalletTools();
        final String address = "bitcoincash:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyBTCTest() {
        final WalletTools wt = new WalletTools();
        final String address = "1HGbVZAiUuvYQxxaU9d6EVZyPRDGHvWn7V";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BTC", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyBCHNewWithoutPrefixTest() {
        final WalletTools wt = new WalletTools();
        final String address = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertEquals("BCH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }

    @Test
    public void classifyInvalidBTCWithPrefixTest() {
        final WalletTools wt = new WalletTools();
        final String address = "bitcoin:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        final Classification classification = wt.classify(address);
        Assert.assertNotNull(classification);
        Assert.assertNull(classification.getCryptoCurrency());
        Assert.assertNull(classification.getCleanData());
        Assert.assertEquals("bitcoin", classification.getPrefix());
        Assert.assertTrue(classification.isContainsPrefix());
        Assert.assertEquals(Classification.TYPE_UNKNOWN, classification.getType());
    }

    @Test
    public void substringTest() {
        final WalletTools wt = new WalletTools();
        String input = "bitcoincash:qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        input = input.trim().replace("\n","");
        boolean containsPrefix = false;
        String prefix = null;
        if (input.contains(":")) {
            prefix = input.substring(0, input.indexOf(":"));
            input = input.substring(input.indexOf(":") + 1);
            containsPrefix = true;
        }

        Assert.assertTrue(containsPrefix);
        Assert.assertEquals("bitcoincash", prefix);
        Assert.assertEquals("qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7", input);
    }
}
