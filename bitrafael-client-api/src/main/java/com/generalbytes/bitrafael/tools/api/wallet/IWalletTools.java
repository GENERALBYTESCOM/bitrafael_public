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

package com.generalbytes.bitrafael.tools.api.wallet;


import java.util.Set;

public interface IWalletTools extends IClassificator{
    int STANDARD_BIP44 = 44 ;
    int STANDARD_BIP49 = 49 ;
    int STANDARD_BIP84 = 84 ;
    int COIN_TYPE_BITCOIN = 0;
    int COIN_TYPE_LITECOIN = 2;
    int COIN_TYPE_ETHEREUM = 60;
    int COIN_TYPE_ETHEREUM_CLASSIC = 61;
    int COIN_TYPE_DASH = 5;
    int COIN_TYPE_BCH = 145;
    // For more coin types @see https://github.com/libbitcoin/libbitcoin/wiki/Altcoin-Version-Mappings
    //   and
    // https://github.com/satoshilabs/slips/blob/master/slip-0044.md

    int CHAIN_EXTERNAL = 0;
    int CHAIN_CHANGE = 1;

    String generateSeedMnemonicSeparatedBySpaces();
    IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard);
    IMasterPrivateKey getMasterPrivateKey(String prv, String cryptoCurrency, int standard);
    String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex);


    String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index);
    String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index);
    String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index);

    String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency);
    String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency);

    ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency);
    boolean isAddressValid(String address, String cryptoCurrency);
    Set<String> supportedCryptoCurrencies();
}