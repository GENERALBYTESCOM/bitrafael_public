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
import com.generalbytes.bitrafael.api.wallet.dash.WalletToolsDASH;
import com.generalbytes.bitrafael.api.wallet.ltc.WalletToolsLTC;
import com.generalbytes.bitrafael.api.wallet.xmr.Account;
import com.generalbytes.bitrafael.api.wallet.xmr.WalletToolsXMR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WalletTools implements IWalletTools{
    private Map<String,IWalletTools> tools = new HashMap<String,IWalletTools>();
    private Map<String,IClassificator> classificators = new HashMap<String,IClassificator>();
    private WalletToolsXMR xmrwt = new WalletToolsXMR();

    public WalletTools() {
        tools.put(IClient.BTC,new WalletToolsBTC());
        classificators.put(IClient.BTC,new WalletToolsBTC());
        tools.put(IClient.LTC,new WalletToolsLTC());
        classificators.put(IClient.LTC,new WalletToolsLTC());
        tools.put(IClient.DASH,new WalletToolsDASH());
        classificators.put(IClient.DASH,new WalletToolsDASH());
        classificators.put(IClient.XMR, xmrwt);
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
        if (IClient.XMR.equalsIgnoreCase(cryptoCurrency)) {
            Account account = xmrwt.getAccount(privateKey);
            if (account != null) {
                return account.getAddress().toString();
            }
            return null;
        }
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


    @Override
    public Classification classify(String input, String cryptoCurrencyHint) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n","");

        if (cryptoCurrencyHint == null) {
            return classify(input);
        }

        Classification result = new Classification(Classification.TYPE_UNKNOWN);
        if (IClient.BTC.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.BTC).classify(input);
        }else if (IClient.LTC.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.LTC).classify(input);
        }else if (IClient.DASH.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.DASH).classify(input);
        }else if (IClient.XMR.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.XMR).classify(input);
        }

        return result;
    }

    @Override
    public Classification classify(String input) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n","");
        if (input.toLowerCase().startsWith("bitcoin")) {
            return classificators.get(IClient.BTC).classify(input);
        }else if (input.toLowerCase().startsWith("litecoin")) {
            return classificators.get(IClient.LTC).classify(input);
        }else if (input.toLowerCase().startsWith("dash")) {
            return classificators.get(IClient.DASH).classify(input);
        }else if (input.toLowerCase().startsWith("xmr") || input.toLowerCase().startsWith("monero")) {
            return classificators.get(IClient.XMR).classify(input);
        }

        //not specified
        Classification result = new Classification(Classification.TYPE_UNKNOWN);
        if (input.startsWith("1") || input.startsWith("3") || input.startsWith("5") || (input.startsWith("K") && input.length() > 50) || (input.startsWith("L") && input.length() > 50) || input.startsWith("xpub")) {
            result = classificators.get(IClient.BTC).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("4") && input.length() >=95)) {
            return classificators.get(IClient.XMR).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("L") || input.startsWith("3") || input.startsWith("6") || input.startsWith("M") || input.startsWith("Ltub") || input.startsWith("T"))) {
            return classificators.get(IClient.LTC).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("X"))) {
            return classificators.get(IClient.DASH).classify(input);
        }

        return result;
    }

    @Override
    public Set<String> supportedCryptoCurrencies() {
        final HashSet<String> result = new HashSet<String>();
        result.add(IClient.LTC);
        result.add(IClient.BTC);
        result.add(IClient.DASH);
        result.add(IClient.XMR);
        return result;
    }}
