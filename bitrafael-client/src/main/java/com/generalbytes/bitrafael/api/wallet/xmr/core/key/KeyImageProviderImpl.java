/*************************************************************************************
 * Copyright (C) 2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.api.wallet.xmr.core.key;

import com.generalbytes.bitrafael.api.wallet.xmr.core.ed25519.Ed25519Ops;
import com.generalbytes.bitrafael.api.wallet.xmr.core.ed25519.ReferenceEd25519Ops;

public class KeyImageProviderImpl implements KeyImageProvider
{
    private final Ed25519Ops ed25519Ops;
    private final KeyHashProvider keyHashProvider;

    public KeyImageProviderImpl()
    {
        this(new ReferenceEd25519Ops(), new KeyHashProviderImpl());
    }

    public KeyImageProviderImpl(Ed25519Ops ed25519Ops, KeyHashProvider keyHashProvider)
    {
        this.ed25519Ops = ed25519Ops;
        this.keyHashProvider = keyHashProvider;
    }

    public byte[] getKeyImage(PrivateKey privateKey, PublicKey publicKey)
    {
        return this.ed25519Ops.scalarMultPoint(privateKey.getEncoded(), this.keyHashProvider.hash(publicKey));
    }
}
