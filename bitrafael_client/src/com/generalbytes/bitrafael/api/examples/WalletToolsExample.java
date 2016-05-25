package com.generalbytes.bitrafael.api.examples;

import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.api.wallet.MasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.WalletTools;

/**
 * Created by b00lean on 24.5.16.
 */
public class WalletToolsExample {
    public static void main(String[] args) {
        IWalletTools wt = new WalletTools();
        final MasterPrivateKey mKey = wt.getMasterPrivateKey("letter advice cage absurd amount doctor acoustic avoid letter advice cage above", "TREZOR");
        System.out.println("mKey = " + mKey);
        final String xpub = mKey.getXPUB();
        System.out.println("xpub = " + xpub);
        final String xprv = mKey.getXPRV();
        System.out.println("xprv = " + xprv);
        final String accountsaddress = wt.getWalletAddress(mKey, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("accountsaddress = " + accountsaddress);
        final String walletPrivateKey = wt.getWalletPrivateKey(mKey, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletPrivateKey = " + walletPrivateKey);
        final String accountXPUB = wt.getAccountXPUB(mKey, 0);
        System.out.println("accountXPUB = " + accountXPUB);
        final String walletAddressFromXPUB = wt.getWalletAddressFromAccountXPUB(accountXPUB, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletAddressFromXPUB = " + walletAddressFromXPUB);


    }
}
