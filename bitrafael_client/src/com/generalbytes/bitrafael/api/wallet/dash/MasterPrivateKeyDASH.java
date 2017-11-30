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

package com.generalbytes.bitrafael.api.wallet.dash;

import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import org.dashj.crypto.DeterministicKey;
import org.dashj.crypto.MnemonicCode;
import org.dashj.params.MainNetParams;

public class MasterPrivateKeyDASH implements IMasterPrivateKey{
    private DeterministicKey key;

    public MasterPrivateKeyDASH(String xprv) {
        key = DeterministicKey.deserializeB58(xprv,MainNetParams.get());
    }

    public long getCreationTimeSeconds() {
        return MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    }

    @Override
    public String toString() {
        return getXPUB();
    }

    public String getXPRV() {
        return key.serializePrivB58(MainNetParams.get());
    }
    public String getXPUB() {
        return key.serializePubB58(MainNetParams.get());
    }

    public boolean hasPrv() {
        return key.hasPrivKey();
    }
}
