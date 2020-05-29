/*************************************************************************************
 * Copyright (C) 2017 GENERAL BYTES s.r.o. All rights reserved.
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
import com.generalbytes.bitrafael.server.api.dto.AccountBalance;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.api.wallet.IXPUBMgr;

import java.util.HashMap;
import java.util.Map;

public class XPUBMgr implements IXPUBMgr {
    private IClient client;
    private WalletTools wt = new WalletTools();
    private final Map<String,Integer> indexes = new HashMap<String,Integer>();

    public XPUBMgr(IClient client) {
        this.client = client;
    }

    @Override
    public String getNextWalletAddressFromAccountXPUB(String accountXPUB, String cryptoCurrency, int chainIndex) {
        if (accountXPUB == null) {
            return null;
        }
        accountXPUB = accountXPUB.trim();
        String key = accountXPUB + "_" + chainIndex;
        Integer lastIndex = null;
        Integer newIndex = null;

        synchronized (key.intern()){
            synchronized (indexes) {
                lastIndex = indexes.get(key);
            }
            if (lastIndex != null) {
                newIndex=lastIndex+1;
            }else{
                final AccountBalance accountBalance = client.getAccountBalance(accountXPUB);
                if (accountBalance == null) {
                    newIndex = 0;
                }else{
                    if (chainIndex == IWalletTools.CHAIN_EXTERNAL) {
                        newIndex = accountBalance.getNextReceivingIndex();
                    }else {
                        newIndex = accountBalance.getNextChangeIndex();
                    }
                }
            }
            synchronized (indexes) {
                indexes.put(key, newIndex);
            }
        }

        return wt.getWalletAddressFromAccountPUB(accountXPUB, cryptoCurrency, chainIndex, newIndex);
    }
}
