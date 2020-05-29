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

package com.generalbytes.bitrafael.tools.wallet;


import com.generalbytes.bitrafael.server.api.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.*;
import com.generalbytes.bitrafael.tools.wallet.bch.WalletToolsBCH;
import com.generalbytes.bitrafael.tools.wallet.btc.WalletToolsBTC;
import com.generalbytes.bitrafael.tools.wallet.dash.WalletToolsDASH;
import com.generalbytes.bitrafael.tools.wallet.eth.WalletToolsETH;
import com.generalbytes.bitrafael.tools.wallet.ltc.WalletToolsLTC;
import com.generalbytes.bitrafael.tools.wallet.xmr.Account;
import com.generalbytes.bitrafael.tools.wallet.xmr.WalletToolsXMR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WalletTools implements IWalletTools {
    private Map<String,IWalletTools> tools = new HashMap<String,IWalletTools>();
    private Map<String, IClassificator> classificators = new HashMap<String,IClassificator>();
    private WalletToolsXMR xmrwt = new WalletToolsXMR();

    public WalletTools() {
        tools.put(IClient.BTC,new WalletToolsBTC());
        classificators.put(IClient.BTC,new WalletToolsBTC());

        tools.put(IClient.LTC,new WalletToolsLTC());
        classificators.put(IClient.LTC,new WalletToolsLTC());

        tools.put(IClient.DASH,new WalletToolsDASH());
        classificators.put(IClient.DASH,new WalletToolsDASH());

        tools.put(IClient.BCH,new WalletToolsBCH());
        classificators.put(IClient.BCH,new WalletToolsBCH());

        classificators.put(IClient.XMR, xmrwt);

        WalletToolsETH wETH = new WalletToolsETH();
        tools.put(IClient.ETH, wETH);
        tools.put(IClient.ETC, wETH);
        classificators.put(IClient.ETH, wETH);
    }

    private IWalletTools getDefaultWalletTools() {
        return tools.get(IClient.BTC);
    }

    @Override
    public String generateSeedMnemonicSeparatedBySpaces() {
        return getDefaultWalletTools().generateSeedMnemonicSeparatedBySpaces();
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard) {
        return tools.get(cryptoCurrency).getMasterPrivateKey(seedMnemonicSeparatedBySpaces,password, cryptoCurrency, standard);
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return tools.get(cryptoCurrency).getMasterPrivateKey(prv, cryptoCurrency, standard);
    }

    @Override
    public String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        return tools.get(cryptoCurrency).getAccountPUB(master, cryptoCurrency, accountIndex);
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
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        return tools.get(cryptoCurrency).getWalletAddressFromAccountPUB(accountPUB, cryptoCurrency, chainIndex, index);
    }

    @Override
    public String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency) {
        return tools.get(cryptoCurrency).generateWalletPrivateKeyWithPrefix(prefix, cryptoCurrency);
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        if (IClient.XMR.equalsIgnoreCase(cryptoCurrency)) {
            Account account = xmrwt.getAccount(privateKey,0);
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
        }else if (IClient.ETH.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.ETH).classify(input);
        }else if (IClient.ETC.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.ETC).classify(input);
        }else if (IClient.BCH.equalsIgnoreCase(cryptoCurrencyHint)) {
            result = classificators.get(IClient.BCH).classify(input);
        }

        return result;
    }

    @Override
    public Classification classify(String input) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n","");
        if (input.toLowerCase().startsWith("bitcoincash:")) {
            return classificators.get(IClient.BCH).classify(input);
        }else if (input.toLowerCase().startsWith("bitcoin:")) {
            return classificators.get(IClient.BTC).classify(input);
        }else if (input.toLowerCase().startsWith("litecoin:")) {
            return classificators.get(IClient.LTC).classify(input);
        }else if (input.toLowerCase().startsWith("dash:")) {
            return classificators.get(IClient.DASH).classify(input);
        }else if (input.toLowerCase().startsWith("xmr:") || input.toLowerCase().startsWith("monero:")) {
            return classificators.get(IClient.XMR).classify(input);
        }else if (input.toLowerCase().startsWith("ethereum:") || input.toLowerCase().startsWith("iban:")) {
            return classificators.get(IClient.ETH).classify(input);
        }
        if (input.contains(":")) {
            //refuse to intepret protocol that we do not support
            return null;
        }

        //protocol not specified lets guess
        Classification result = new Classification(Classification.TYPE_UNKNOWN);
        if (input.startsWith("1") || input.startsWith("3") || input.startsWith("5") || (input.startsWith("K") && input.length() > 50) || (input.startsWith("L") && input.length() > 50) || input.startsWith("xpub") || input.startsWith("ypub") || input.startsWith("zpub") || input.startsWith("xprv") || input.startsWith("yprv") || input.startsWith("zprv")) {
            result = classificators.get(IClient.BTC).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("4") || input.startsWith("8")) && input.length() >=95) {
            return classificators.get(IClient.XMR).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("L") || input.startsWith("3") || input.startsWith("6") || input.startsWith("M") || input.startsWith("Ltub") || input.startsWith("Mtub") || input.startsWith("T") || input.startsWith("Ltpv") || input.startsWith("Mtpv"))) {
            return classificators.get(IClient.LTC).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("0x"))) {
            return classificators.get(IClient.ETH).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("XE"))) {
            //try first ethereum then dash
            Classification c = classificators.get(IClient.ETH).classify(input);
            if (c.getType() == Classification.TYPE_UNKNOWN) {
                return classificators.get(IClient.DASH).classify(input);
            }
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("X") || input.startsWith("drk"))) {
            return classificators.get(IClient.DASH).classify(input);
        }

        if (result.getType() == Classification.TYPE_UNKNOWN && (input.startsWith("q") || input.startsWith("p"))) {
            return classificators.get(IClient.BCH).classify(input);
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
        result.add(IClient.ETH);
        result.add(IClient.ETC);
        result.add(IClient.BCH);
        return result;
    }

    public static int getCoinTypeByCryptoCurrency(String cryptoCurrency) {
        if (IClient.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BITCOIN;
        }else if (IClient.LTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_LITECOIN;
        }else if (IClient.ETH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_ETHEREUM;
        }else if (IClient.ETC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_ETHEREUM_CLASSIC;
        }else if (IClient.DASH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_DASH;
        }else if (IClient.BCH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BCH;
        }
        return COIN_TYPE_BITCOIN;
    }

}
