package com.generalbytes.bitrafael.api.wallet.dash;

import com.generalbytes.bitrafael.api.wallet.Classification;
import com.generalbytes.bitrafael.api.wallet.bch.WalletToolsBCH;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsDASHTest {

    @Test
    public void classifyDASHWithPrefixTest() {
        WalletToolsDASH wt = new WalletToolsDASH();
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
        WalletToolsDASH wt = new WalletToolsDASH();
        String input = "XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL";
        Classification classification = wt.classify(input);

        Assert.assertNotNull(classification);
        Assert.assertEquals(null, classification.getPrefix());
        Assert.assertEquals("XpikEQvwPKWeFSji3JMsjgEiaYJ3vwAzSL", classification.getCleanData());
        Assert.assertFalse(classification.isContainsPrefix());
        Assert.assertEquals("DASH", classification.getCryptoCurrency());
        Assert.assertEquals(Classification.TYPE_ADDRESS, classification.getType());
    }
}
