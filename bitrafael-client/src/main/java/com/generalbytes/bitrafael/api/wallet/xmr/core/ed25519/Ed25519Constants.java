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

import com.generalbytes.bitrafael.api.wallet.xmr.core.Utils;

import java.math.BigInteger;

public final class Ed25519Constants
{
    public static final BigInteger L = BigInteger.valueOf(2L).pow(252).add(new BigInteger("27742317777372353535851937790883648493"));
    public static final BigInteger P = BigInteger.valueOf(2L).pow(255).subtract(BigInteger.valueOf(19L));
    public static final BigInteger B_X = new BigInteger("15112221349535400772501151409588531511454012693041857206046113283949847762202");
    public static final BigInteger B_Y = new BigInteger("46316835694926478169428394003475163141307993866256225615783033603165251855960");
    public static final BigInteger[] B = { B_X, B_Y };
    public static final byte[] L_BYTES = Utils.as256BitLe(L);
    public static final byte[] P_BYTES = Utils.as256BitLe(P);
    public static final BigInteger D = new BigInteger("-4513249062541557337682894930092624173785641285191125241628941591882900924598840740");
    public static final byte[] D_BYTES = Utils.as256BitLe(D.mod(P));
    public static final byte[] BASE_P_BYTES = new byte[32];

    static
    {
        BASE_P_BYTES[0] = 88;
        for (int i = 1; i < BASE_P_BYTES.length; i++) {
            BASE_P_BYTES[i] = 102;
        }
    }

    public static final BigInteger I = new BigInteger("19681161376707505956807079304988542015446066515923890162744021073123829784752");
    public static final byte[] I_BYTES = Utils.as256BitLe(I);
}