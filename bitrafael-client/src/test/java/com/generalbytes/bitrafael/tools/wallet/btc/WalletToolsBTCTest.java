package com.generalbytes.bitrafael.tools.wallet.btc;

import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsBTCTest {

    private WalletToolsBTC walletToolsBTC = new WalletToolsBTC();
    private MasterPrivateKeyBTC masterPrivateKey =  walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP44);
    private MasterPrivateKeyBTC masterPrivateKeyBIP49 = walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP49);
    private MasterPrivateKeyBTC masterPrivateKeyBIP84 = walletToolsBTC.getMasterPrivateKey("have nothing", "password", "BTC", IWalletTools.STANDARD_BIP84);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("xprv9s21ZrQH143K3vrXj7RW2pKgDjYifyseBrZjGSnxm6ik7JeSeQMn3mE7VjvfBQhMj8hBZja7cgs2EshvQQp2Akguei7WPkJiHH7CQtMs9mJ", masterPrivateKey.getPRV());
        Assert.assertEquals("xpub6C4oSHiKXiakT4f7Ukj5FDzFmgpNrM3P895aLuymmtvvMuLp5vum1MRrh9SnEzaQBBCY7homGSefdc1HJU46vPzLj8jVcGFzBGC4ZQfVVui", masterPrivateKey.getPUB());
        Assert.assertEquals("yprvABrGsX5C9januE3eZUD8EuRBPhhAcbs96y5x3qgr976dAQTfu4XLfptFWwtFBKMH8mozKDAg5MDa8AKV87E2xzNWX3ovyf8CZ1AqoQkm1S2", masterPrivateKeyBIP49.getPRV());
        Assert.assertEquals("ypub6X7hZfC6J19wYnayxCXjFUZnAY3N7aiMPVnsokGnmYfG6EVp7a41u6CTmeyCvQymrJGg5E1DEB6vUBBdSqNDWd6yjGdAULxs3tdsdGbeD7J", masterPrivateKeyBIP49.getPUB());
        Assert.assertEquals("zprvAWgYBBk7JR8GkXEmPpzkSzWgZfqcZDre25cAqEajX7UWDWGu9iguHtYPY9qqBE1CYQvo4gmEY1a81Sw3qoe3mE47PPWMZZwgpjEVC3n6nc6", masterPrivateKeyBIP84.getPRV());
        Assert.assertEquals("zpub6rNmoPmb84rzpKmb73iptk4PuUFUSMWhFdArUrtyXSNnNV433XaujCLc1XboQjgX29LoFKQTG34UQShZ4TCLKx8xqA1VWt8XXUTSrrjtJNe", masterPrivateKeyBIP84.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletToolsBTC.getWalletAddressFromAccountPUB(masterPrivateKey.getPUB(), "BTC", 2, 3));
        Assert.assertEquals("38tMYJ2LtiikEp4MWwyctUK1kCMFk2W7R6", walletToolsBTC.getWalletAddressFromAccountPUB(masterPrivateKeyBIP49.getPUB(), "BTC", 2, 3));
        Assert.assertEquals("bc1q97ef9zxjxqxsvg6ttskum7702gwyvn2f896was", walletToolsBTC.getWalletAddressFromAccountPUB(masterPrivateKeyBIP84.getPUB(), "BTC", 2, 3));
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsBTC.getWalletAddressFromPrivateKey(walletToolsBTC.getWalletPrivateKey(masterPrivateKey, "BTC", 0, 2, 3), "BTC");
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletAddressFromPrv);
    }

    @Test
    public void getWalletAddress() {
        Assert.assertEquals("1CPrAD3jcUsfmFRedto6m9sG53zvCBLQS8", walletToolsBTC.getWalletAddress(masterPrivateKey, "BTC", 0, 2, 3));
        Assert.assertEquals("38tMYJ2LtiikEp4MWwyctUK1kCMFk2W7R6", walletToolsBTC.getWalletAddress(masterPrivateKeyBIP49, "BTC", 0, 2, 3));
        Assert.assertEquals("bc1q97ef9zxjxqxsvg6ttskum7702gwyvn2f896was", walletToolsBTC.getWalletAddress(masterPrivateKeyBIP84, "BTC", 0, 2, 3));
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