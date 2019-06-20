package com.generalbytes.bitrafael.api.wallet.btc;

import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsBTCTest {

    private WalletToolsBTC walletToolsBTC = new WalletToolsBTC();
    private MasterPrivateKeyBTC masterPrivateKey = walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP44);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("xprv9s21ZrQH143K3vrXj7RW2pKgDjYifyseBrZjGSnxm6ik7JeSeQMn3mE7VjvfBQhMj8hBZja7cgs2EshvQQp2Akguei7WPkJiHH7CQtMs9mJ", masterPrivateKey.getPRV());
        Assert.assertEquals("xpub6C4oSHiKXiakT4f7Ukj5FDzFmgpNrM3P895aLuymmtvvMuLp5vum1MRrh9SnEzaQBBCY7homGSefdc1HJU46vPzLj8jVcGFzBGC4ZQfVVui", masterPrivateKey.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletToolsBTC.getWalletAddressFromAccountPUB(
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP44).getPUB(),
                "BTC", 2, 3));
        Assert.assertEquals("38tMYJ2LtiikEp4MWwyctUK1kCMFk2W7R6", walletToolsBTC.getWalletAddressFromAccountPUB(
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP49).getPUB(),
                "BTC", 2, 3));
        Assert.assertEquals(null, walletToolsBTC.getWalletAddressFromAccountPUB( // TODO
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP84).getPUB(),
                "BTC", 2, 3));
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsBTC.getWalletAddressFromPrivateKey(walletToolsBTC.getWalletPrivateKey(masterPrivateKey, "BTC", 0, 2, 3), "BTC");
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletAddressFromPrv);
    }

    @Test
    public void getWalletAddress() {
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletToolsBTC.getWalletAddress(
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP44),
                "BTC", 0, 2, 3));
        Assert.assertEquals("38tMYJ2LtiikEp4MWwyctUK1kCMFk2W7R6", walletToolsBTC.getWalletAddress(
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP49),
                "BTC", 0, 2, 3));
        Assert.assertEquals("bechadress", walletToolsBTC.getWalletAddress( // TODO
                walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP84),
                "BTC", 0, 2, 3));
    }

    @Test
    public void classify() {
        Assert.assertEquals("BTC", walletToolsBTC.classify("3CuvPRVeG3jWhm1dLSACKDN9PMPuY6FVXT", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("112pN943KyuQY26epMwFFvcj85EVVc2fnJ", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("bc1qlgp42vadfqkdjr0wdwvfnpqlvs5cg3xnl5zukw", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("BC1QLGP42VADFQKDJR0WDWVFNPQLVS5CG3XNL5ZUKW", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("bitcoin:3CuvPRVeG3jWhm1dLSACKDN9PMPuY6FVXT", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("bitcoin:112pN943KyuQY26epMwFFvcj85EVVc2fnJ", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("bitcoin:bc1qlgp42vadfqkdjr0wdwvfnpqlvs5cg3xnl5zukw", "BTC").getCryptoCurrency());
        Assert.assertEquals("BTC", walletToolsBTC.classify("bitcoin:BC1QLGP42VADFQKDJR0WDWVFNPQLVS5CG3XNL5ZUKW", "BTC").getCryptoCurrency());

        Assert.assertNull(walletToolsBTC.classify("bitcoin:bc1QLGP42VADFQKDJR0WDWVFNPQLVS5CG3XNL5ZUKW", "BTC").getCryptoCurrency());
    }
}