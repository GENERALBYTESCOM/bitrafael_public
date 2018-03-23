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
package com.generalbytes.bitrafael.api.wallet.xmr.core.ed25519;

public abstract interface Ed25519Ops
{
    public abstract byte[] scalarMultG(byte[] paramArrayOfByte);

    public abstract byte[] scalarMultPoint(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

    public abstract byte[] add(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
}

