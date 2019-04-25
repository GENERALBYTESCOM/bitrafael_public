package com.generalbytes.bitrafael.api.wallet.eth;

import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import org.junit.Assert;
import org.junit.Test;

public class WalletToolsETHTest {

    private WalletToolsETH walletToolsETH = new WalletToolsETH();
    private MasterPrivateKeyETH masterPrivateKey = walletToolsETH.getMasterPrivateKey("have nothing", "password", "ETH", IWalletTools.STANDARD_BIP44);

    @Test
    public void getMasterPrivateKey() {
        Assert.assertEquals("xprv9s21ZrQH143K3vrXj7RW2pKgDjYifyseBrZjGSnxm6ik7JeSeQMn3mE7VjvfBQhMj8hBZja7cgs2EshvQQp2Akguei7WPkJiHH7CQtMs9mJ", masterPrivateKey.getPRV());
        Assert.assertEquals("xpub6DAMckKDeQnkHC1RiaQ61v93RXaP5eRiFzwCRixsUDxceF2omZ3E3kGKugU5qYFyDBFNRqsDLt9H2rNZTE9z8tE8H3Fvr7Teq35FAEnexJb", masterPrivateKey.getPUB());
    }

    @Test
    public void getWalletAddressFromAccountPUB() {
        String walletAddressFromPub = walletToolsETH.getWalletAddressFromAccountPUB(masterPrivateKey.getPUB(), "ETH", 2, 3);
        Assert.assertEquals("1ALZ3ojSmz3632LG7j3xbHeJpuy4ZXbPn9", walletAddressFromPub);
    }

    @Test
    public void getWalletAddressFromPrivateKey() {
        String walletAddressFromPrv = walletToolsETH.getWalletAddressFromPrivateKey(walletToolsETH.getWalletPrivateKey(masterPrivateKey, "ETH", 0, 2, 3), "ETH");
        Assert.assertEquals(null, walletAddressFromPrv); // not supported yet
    }

    @Test
    public void getWalletAddress() {
        String walletAddress = walletToolsETH.getWalletAddress(masterPrivateKey, "ETH", 0, 2, 3);
        Assert.assertEquals("0x184FCD3649D971e7f5d11989E5Dbc72C8a887507", walletAddress);
    }

    @Test
    public void classify() {
        Assert.assertEquals("ETH", walletToolsETH.classify("0x184FCD3649D971e7f5d11989E5Dbc72C8a887507", "ETH").getCryptoCurrency());
    }
}