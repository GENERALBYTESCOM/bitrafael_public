package com.generalbytes.bitrafael.api.wallet.bch;

import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsBCHTest {

    private WalletToolsBCH walletToolsBCH = new WalletToolsBCH();
    private MasterPrivateKeyBCH masterPrivateKey = walletToolsBCH.getMasterPrivateKey("abandon abandon abandon", "password", "BCH", IWalletTools.STANDARD_BIP44);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("xprv9s21ZrQH143K25nXfd4XuZ6d955Y7XQ7GzSrew7bEYCx1eC6JixjXoaULDjgpB4BJaLL4BmCTka4L9Jq5rxstLcXLnVkGK4tkMk8D9iv1y8", masterPrivateKey.getPRV());
        Assert.assertEquals("xpub6C9nRehjbvKLDD1Rzjshv5HaTm9Q1qFnraUZ34uVtbQzgAMFffuhTxN2umR6gfjWYyf4egfQVCbRegE9gMYCwbK3rsV8Xz1LU2KAHUvNaYB", masterPrivateKey.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        String walletAddressFromPub = walletToolsBCH.getWalletAddressFromAccountPUB(masterPrivateKey.getPUB(), "BCH", 2, 3);
        Assert.assertEquals("qq85rv80tn99qhzf2dzl5c6rcleavlnz7yay4yxumv", walletAddressFromPub);
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsBCH.getWalletAddressFromPrivateKey(walletToolsBCH.getWalletPrivateKey(masterPrivateKey, "BCH", 0, 2, 3), "BCH");
        Assert.assertEquals("qq85rv80tn99qhzf2dzl5c6rcleavlnz7yay4yxumv", walletAddressFromPrv);
    }

    @Test
    public void getWalletAddress() {
        String walletAddress = walletToolsBCH.getWalletAddress(masterPrivateKey, "BCH", 0, 2, 3);
        Assert.assertEquals("qq85rv80tn99qhzf2dzl5c6rcleavlnz7yay4yxumv", walletAddress);
    }

    @Test
    public void classify() {
        Assert.assertEquals("BCH", walletToolsBCH.classify("qp8xmf4rpa40vzpvf282njskd50yfng72vkz0rwau3", "BCH").getCryptoCurrency());
        Assert.assertEquals("qp8xmf4rpa40vzpvf282njskd50yfng72vkz0rwau3", walletToolsBCH.classify("bitcoincash:qp8xmf4rpa40vzpvf282njskd50yfng72vkz0rwau3", "BCH").getCleanData());
    }
}