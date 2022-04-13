package com.generalbytes.bitrafael.tools.wallet.ltc;

import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsLTCTest {

    private WalletToolsLTC walletToolsLTC = new WalletToolsLTC();
    private MasterPrivateKeyLTC masterPrivateKey = walletToolsLTC.getMasterPrivateKey("have nothing", "password", "LTC", IWalletTools.STANDARD_BIP44);
    private MasterPrivateKeyLTC masterPrivateKeyBIP49 = walletToolsLTC.getMasterPrivateKey("have nothing", "password", "LTC", IWalletTools.STANDARD_BIP49);
    private MasterPrivateKeyLTC masterPrivateKeyBIP84 = walletToolsLTC.getMasterPrivateKey("have nothing", "password", "LTC", IWalletTools.STANDARD_BIP84);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("Ltpv71G8qDifUiNetuW7B1U8BEYRR11akyYybqbHDwBPkkJq87RKX38LQbvmTWTg48LYeFGTeFDdmmZgENpzoXnzos4FcH4ghhMk5fj6UH7QQng", masterPrivateKey.getPRV());
        Assert.assertEquals("Ltub2ZBNXLSFAmemHAVYx1EmJXXbJrwTNrHBHJ1L7YvhA47Y8D7F473JCfj14WWvVCSeyBeXsQULUfoVrcJUo3PqxLAKfQXZiXF6QVr94zgodFy", masterPrivateKey.getPUB());
        Assert.assertEquals("Mtpv7L6Q8tPadPv8kChE1NFkPKdvayA2hbYUWx7W1L5H8kgiBDEYmhHu2fauUiRG42zU3tPGPipCERvE7fSZXED1c6jrUcm7HcBEMPnjrtGfo9M", masterPrivateKeyBIP49.getPRV());
        Assert.assertEquals("Mtub2sWDx6NNDH6D5yYZgw2AeiCQLHD3TeGBiDq6WqiZ3A12mgSzatwV8ebF8oT44ErNLfQJvpjFf8fDHcGovyXxjfj4oEuc3TWDE37wLYiBX7P", masterPrivateKeyBIP49.getPUB());
        Assert.assertEquals("zprvAWgYBBk7JR8GkXEmPpzkSzWgZfqcZDre25cAqEajX7UWDWGu9iguHtYPY9qqBE1CYQvo4gmEY1a81Sw3qoe3mE47PPWMZZwgpjEVC3n6nc6", masterPrivateKeyBIP84.getPRV());
        Assert.assertEquals("zpub6rH6hJp6e6e9DYDu3MdksiF3Ggxscj1Dk9sfLjvrLhaktAJGo5Rh65FWbaahKu5vpRmfKRSEPRs5DGBMzaU8etgdEuz2jMjcW4vvyqSXpFZ", masterPrivateKeyBIP84.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletToolsLTC.getWalletAddressFromAccountPUB(masterPrivateKey.getPUB(), "LTC", 2, 3));
        Assert.assertEquals("M9LCmeZyEVNdqXu8HPEZVMpcJxKCC6PNG4", walletToolsLTC.getWalletAddressFromAccountPUB(masterPrivateKeyBIP49.getPUB(), "LTC", 2, 3));
        Assert.assertEquals("ltc1qt0zprx7p3sm3zeqg93wdty5r6agtaxlwtttmpl", walletToolsLTC.getWalletAddressFromAccountPUB(masterPrivateKeyBIP84.getPUB(), "LTC", 2, 3));
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsLTC.getWalletAddressFromPrivateKey(walletToolsLTC.getWalletPrivateKey(masterPrivateKey, "LTC", 0, 2, 3), "LTC");
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletAddressFromPrv);
    }

    @Test
    public void getWalletAddress() {
        Assert.assertEquals("LTxvErfUMbp7zc2wc79GM8YpvJQEzVH4vA", walletToolsLTC.getWalletAddress(masterPrivateKey, "LTC", 0, 2, 3));
        Assert.assertEquals("M9LCmeZyEVNdqXu8HPEZVMpcJxKCC6PNG4", walletToolsLTC.getWalletAddress(masterPrivateKeyBIP49, "LTC", 0, 2, 3));
        Assert.assertEquals("ltc1qt0zprx7p3sm3zeqg93wdty5r6agtaxlwtttmpl", walletToolsLTC.getWalletAddress(masterPrivateKeyBIP84, "LTC", 0, 2, 3));
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