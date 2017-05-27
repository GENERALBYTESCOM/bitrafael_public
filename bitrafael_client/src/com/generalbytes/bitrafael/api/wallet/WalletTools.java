/*************************************************************************************
 * Copyright (C) 2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.bitrafael.api.wallet;


import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.btc.WalletToolsBTC;
import com.generalbytes.bitrafael.api.wallet.ltc.WalletToolsLTC;

import java.util.HashMap;
import java.util.Map;

public class WalletTools implements IWalletTools{
    private Map<String,IWalletTools> tools = new HashMap<>();

    public WalletTools() {
        tools.put(IClient.BTC,new WalletToolsBTC());
        tools.put(IClient.LTC,new WalletToolsLTC());
    }

    private IWalletTools getDefaultWalletTools() {
        return tools.get(IClient.BTC);
    }

    @Override
    public String generateSeedMnemonicSeparatedBySpaces() {
        return getDefaultWalletTools().generateSeedMnemonicSeparatedBySpaces();
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency) {
        return tools.get(cryptoCurrency).getMasterPrivateKey(seedMnemonicSeparatedBySpaces,password, cryptoCurrency);
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String xprv, String cryptoCurrency) {
        return tools.get(cryptoCurrency).getMasterPrivateKey(xprv, cryptoCurrency);
    }

    @Override
    public String getAccountXPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        return tools.get(cryptoCurrency).getAccountXPUB(master, cryptoCurrency, accountIndex);
    }

    @Override
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        return tools.get(cryptoCurrency).getWalletAddress(master, cryptoCurrency, accountIndex, chainIndex, index);
    }

    @Override
    public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        return tools.get(cryptoCurrency).getWalletPrivateKey(master, cryptoCurrency, accountIndex, chainIndex, index);
    }

    @Override
    public String getWalletAddressFromAccountXPUB(String accountXPUB, String cryptoCurrency, int chainIndex, int index) {
        return tools.get(cryptoCurrency).getWalletAddressFromAccountXPUB(accountXPUB, cryptoCurrency, chainIndex, index);
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        return tools.get(cryptoCurrency).getWalletAddressFromPrivateKey(privateKey, cryptoCurrency);
    }

    @Override
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        return tools.get(cryptoCurrency).sign(privateKey, hashToSign,cryptoCurrency);
    }

    @Override
    public boolean isAddressValid(String address, String cryptoCurrency) {
        return tools.get(cryptoCurrency).isAddressValid(address, cryptoCurrency);
    }
}
