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


public interface IWalletTools {
    public static final int PURPOSE_BIP44 = 44 ;
    public static final int COIN_TYPE_BITCOIN = 0;
    public static final int COIN_TYPE_LITECOIN = 2;
    public static final int CHAIN_EXTERNAL = 0;
    public static final int CHAIN_CHANGE = 1;

    public String generateSeedMnemonicSeparatedBySpaces();
    public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency);
    public IMasterPrivateKey getMasterPrivateKey(String xprv, String cryptoCurrency);
    public String getAccountXPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex);


    String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index);
    String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index);
    String getWalletAddressFromAccountXPUB(String accountXPUB, String cryptoCurrency, int chainIndex, int index);
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency);
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency);
    boolean isAddressValid(String address, String cryptoCurrency);
}