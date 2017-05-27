package com.generalbytes.bitrafael.api.examples;

import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.api.wallet.WalletTools;

/**
 * Created by b00lean on 24.5.16.
 */
public class WalletToolsExample {
    public static void main(String[] args) {
        IWalletTools wt = new WalletTools();
        String cryptoCurrency = ExampleConfig.getConfig().getCryptoCurrency();
        final IMasterPrivateKey mKey = wt.getMasterPrivateKey("letter advice cage absurd amount doctor acoustic avoid letter advice cage above", "TREZOR");
        System.out.println("mKey = " + mKey);
        final String xpub = mKey.getXPUB();
        System.out.println("xpub = " + xpub);
        final String xprv = mKey.getXPRV();
        System.out.println("xprv = " + xprv);
        final String accountsaddress = wt.getWalletAddress(mKey,cryptoCurrency, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("accountsaddress = " + accountsaddress);
        final String walletPrivateKey = wt.getWalletPrivateKey(mKey,cryptoCurrency, 0, IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletPrivateKey = " + walletPrivateKey);
        final String accountXPUB = wt.getAccountXPUB(mKey, cryptoCurrency,0);
        System.out.println("accountXPUB = " + accountXPUB);
        final String walletAddressFromXPUB = wt.getWalletAddressFromAccountXPUB(accountXPUB, cryptoCurrency,IWalletTools.CHAIN_EXTERNAL, 0);
        System.out.println("walletAddressFromXPUB = " + walletAddressFromXPUB);

        //generate new master key
        final String menmonic = wt.generateSeedMnemonicSeparatedBySpaces();
        System.out.println("New generated menmonic = " + menmonic);


    }
}
