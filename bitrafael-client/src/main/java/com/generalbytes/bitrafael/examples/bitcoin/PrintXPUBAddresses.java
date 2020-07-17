package com.generalbytes.bitrafael.examples.bitcoin;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;

public class PrintXPUBAddresses {
    public static void main(String[] args) {
        IWalletTools wt = new WalletTools();
        String cryptoCurrency = IClient.BTC;
        String xpub = "xpub6BosfCnifzxcFwrSzQiqu2DBVTshkCXacvNsWGYJVVhhawA7d4R5WSWGFNbi8Aw6ZRc1brxMyWMzG3DSSSSoekkudhUd9yLb6qx39T9nMdj";
        for (int i=0;i<3000;i++) {
            String address = wt.getWalletAddressFromAccountPUB(xpub, cryptoCurrency, 0, i);
            System.out.println( i + ". " + address);
        }
    }
}
