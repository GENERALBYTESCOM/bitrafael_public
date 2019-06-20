package com.generalbytes.bitrafael.api.wallet.ltc;

import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsLTCTest {

    private WalletToolsLTC walletToolsLTC = new WalletToolsLTC();
    private MasterPrivateKeyLTC masterPrivateKey = walletToolsLTC.getMasterPrivateKey("have nothing", "password", "LTC", IWalletTools.STANDARD_BIP44);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("Ltpv71G8qDifUiNetuW7B1U8BEYRR11akyYybqbHDwBPkkJq87RKX38LQbvmTWTg48LYeFGTeFDdmmZgENpzoXnzos4FcH4ghhMk5fj6UH7QQng", masterPrivateKey.getPRV());
        Assert.assertEquals("Ltub2ZBNXLSFAmemHAVYx1EmJXXbJrwTNrHBHJ1L7YvhA47Y8D7F473JCfj14WWvVCSeyBeXsQULUfoVrcJUo3PqxLAKfQXZiXF6QVr94zgodFy", masterPrivateKey.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        String walletAddressFromPub = walletToolsLTC.getWalletAddressFromAccountPUB(masterPrivateKey.getPUB(), "LTC", 2, 3);
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletAddressFromPub);
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsLTC.getWalletAddressFromPrivateKey(walletToolsLTC.getWalletPrivateKey(masterPrivateKey, "LTC", 0, 2, 3), "LTC");
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletAddressFromPrv);
    }

    @Test
    public void getWalletAddress() {
        String walletAddress = walletToolsLTC.getWalletAddress(masterPrivateKey, "LTC", 0, 2, 3);
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletAddress);
    }

    @Test
    public void classify() {
        Assert.assertEquals("LTC", walletToolsLTC.classify("ltc1q3cv28h4f33dj7msgm85va0epdw6298rxsparl7", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("litecoin:ltc1q3cv28h4f33dj7msgm85va0epdw6298rxsparl7", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("litecoin:LTC1Q3CV28H4F33DJ7MSGM85VA0EPDW6298RXSPARL7", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("MV5sqqgY5qG3rxBciUQgz9dSYSKiyxN2n3", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("litecoin:MV5sqqgY5qG3rxBciUQgz9dSYSKiyxN2n3", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("LZpVz2zJ9Mq4tUx2UeSiFHRqKfN54BL6yr", "LTC").getCryptoCurrency());
        Assert.assertEquals("LTC", walletToolsLTC.classify("litecoin:LZpVz2zJ9Mq4tUx2UeSiFHRqKfN54BL6yr", "LTC").getCryptoCurrency());

        Assert.assertNull(walletToolsLTC.classify("litecoin:ltc1Q3CV28H4F33DJ7MSGM85VA0EPDW6298RXSPARL7", "LTC").getCryptoCurrency());
        Assert.assertNull(walletToolsLTC.classify("ltc1QQQQQQQQQ3cv28h4f33dj7msgm85va0epdw6298rxsparl7", "LTC").getCryptoCurrency());
        Assert.assertNull(walletToolsLTC.classify("MV5sqqgY5qG3rxBciUXXXXXdSYSKiyxN2n3", "LTC").getCryptoCurrency());
    }
}