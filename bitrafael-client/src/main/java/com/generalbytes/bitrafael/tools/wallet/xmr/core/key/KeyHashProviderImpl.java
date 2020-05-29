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
package com.generalbytes.bitrafael.tools.wallet.xmr.core.key;

import com.generalbytes.bitrafael.tools.wallet.xmr.core.Utils;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Keccak256;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Constants;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Ops;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.ReferenceEd25519Ops;

import java.math.BigInteger;

public class KeyHashProviderImpl implements KeyHashProvider
{
    private static final BigInteger BI_2 = BigInteger.valueOf(2L);
    private static final BigInteger BI_3 = BigInteger.valueOf(3L);
    private static final BigInteger PQU = Ed25519Constants.P.add(BigInteger.valueOf(3L)).divide(BigInteger.valueOf(8L));
    private static final BigInteger BIG_A = BigInteger.valueOf(486662L);
    private static final BigInteger SQ2 = BI_2.modPow(PQU, Ed25519Constants.P);
    private static final BigInteger EDW = BigInteger.valueOf(486664L).modPow(PQU, Ed25519Constants.P).multiply(Ed25519Constants.I).mod(Ed25519Constants.P);
    private static final byte[] SCALAR_8_LE = new byte[32];
    private final Ed25519Ops ed25519Ops;

    static
    {
        SCALAR_8_LE[0] = 8;
    }

    private final Keccak256 keccak256 = new Keccak256();

    public KeyHashProviderImpl()
    {
        this(new ReferenceEd25519Ops());
    }

    public KeyHashProviderImpl(Ed25519Ops ed25519Ops)
    {
        this.ed25519Ops = ed25519Ops;
    }

    public byte[] hash(PublicKey publicKey)
    {
        this.keccak256.reset();
        this.keccak256.update(publicKey.getEncoded());
        byte[] digest = this.keccak256.digest().array();
        return this.ed25519Ops.scalarMultPoint(SCALAR_8_LE, hashToPoint(digest));
    }

    byte[] hashToPoint(byte[] digest)
    {
        BigInteger r = Utils.leToPositiveBigInt(digest);
        BigInteger v = Ed25519Constants.P.subtract(BIG_A).multiply(mInv(r.pow(2).multiply(BI_2).add(BigInteger.ONE).mod(Ed25519Constants.P))).mod(Ed25519Constants.P);

        BigInteger eps2 = v.modPow(BI_3, Ed25519Constants.P).add(v.modPow(BI_2, Ed25519Constants.P).multiply(BIG_A)).add(v).mod(Ed25519Constants.P);
        BigInteger eps = eps2.modPow(PQU, Ed25519Constants.P);

        boolean sign = false;
        BigInteger y;
        BigInteger x;
        if (!eps.modPow(BI_2, Ed25519Constants.P).subtract(eps2).mod(Ed25519Constants.P).equals(BigInteger.ZERO))
        {
            y = null;
            if (!eps.modPow(BI_2, Ed25519Constants.P).add(eps2).mod(Ed25519Constants.P).equals(BigInteger.ZERO))
            {
                sign = true;
                eps = eps.multiply(Ed25519Constants.I).mod(Ed25519Constants.P);
                x = r.modPow(BI_2, Ed25519Constants.P).multiply(v).multiply(BI_2).mod(Ed25519Constants.P);
                y = r.multiply(eps).multiply(SQ2).mod(Ed25519Constants.P);
            }
            else
            {
                x = v;
                y = eps;
            }
        }
        else
        {
            x = v;
            y = Ed25519Constants.P.subtract(eps);
        }
        BigInteger x2 = x.multiply(mInv(y)).multiply(EDW).mod(Ed25519Constants.P);
        BigInteger y2 = x.subtract(BigInteger.ONE).multiply(mInv(x.add(BigInteger.ONE))).mod(Ed25519Constants.P);
        if (sign != Utils.isEven(x2)) {
            x2 = Ed25519Constants.P.subtract(x2);
        }
        return ReferenceEd25519Ops.encodePoint(new BigInteger[] { x2, y2 });
    }

    private BigInteger mInv(BigInteger value)
    {
        return value.modPow(Ed25519Constants.P.subtract(BI_2), Ed25519Constants.P);
    }
}
