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
package com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519;

import java.math.BigInteger;

public class ReferenceEd25519Ops
        implements Ed25519Ops
{
    public static final BigInteger q = Ed25519Constants.P;
    private static final int b = 256;
    private static final BigInteger qm2 = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564819947");
    private static final BigInteger qp3 = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564819952");
    private static final BigInteger un = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564819967");
    private static final BigInteger BI_2 = BigInteger.valueOf(2L);

    public static BigInteger expMod(BigInteger b, BigInteger e, BigInteger m)
    {
        if (e.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }
        BigInteger t = expMod(b, e.divide(BI_2), m).pow(2).mod(m);
        if (e.testBit(0)) {
            t = t.multiply(b).mod(m);
        }
        return t;
    }

    public static BigInteger inv(BigInteger x)
    {
        return expMod(x, qm2, q);
    }

    public static BigInteger xRecover(BigInteger y)
    {
        BigInteger y2 = y.multiply(y);
        BigInteger xx = y2.subtract(BigInteger.ONE).multiply(inv(Ed25519Constants.D.multiply(y2).add(BigInteger.ONE)));
        BigInteger x = expMod(xx, qp3.divide(BigInteger.valueOf(8L)), q);
        if (!x.multiply(x).subtract(xx).mod(q).equals(BigInteger.ZERO)) {
            x = x.multiply(Ed25519Constants.I).mod(q);
        }
        if (!x.mod(BI_2).equals(BigInteger.ZERO)) {
            x = q.subtract(x);
        }
        return x;
    }

    public static BigInteger[] edwards(BigInteger[] P, BigInteger[] Q)
    {
        BigInteger x1 = P[0];
        BigInteger y1 = P[1];
        BigInteger x2 = Q[0];
        BigInteger y2 = Q[1];
        BigInteger dTemp = Ed25519Constants.D.multiply(x1).multiply(x2).multiply(y1).multiply(y2);
        BigInteger x3 = x1.multiply(y2).add(x2.multiply(y1)).multiply(inv(BigInteger.ONE.add(dTemp)));
        BigInteger y3 = y1.multiply(y2).add(x1.multiply(x2)).multiply(inv(BigInteger.ONE.subtract(dTemp)));
        return new BigInteger[] { x3.mod(q), y3.mod(q) };
    }

    public static BigInteger[] doublePoint(BigInteger[] point)
    {
        BigInteger x = point[0];
        BigInteger y = point[1];

        BigInteger x2 = x.modPow(BI_2, Ed25519Constants.P);
        BigInteger y2 = y.modPow(BI_2, Ed25519Constants.P);

        BigInteger y2subX2 = y2.subtract(x2);
        BigInteger y2addX2 = y2.add(x2);

        BigInteger xR = BI_2.multiply(x).multiply(y).mod(Ed25519Constants.P).multiply(inv(y2subX2)).mod(Ed25519Constants.P);
        BigInteger yR = y2addX2.multiply(inv(BI_2.subtract(y2subX2))).mod(Ed25519Constants.P);
        return new BigInteger[] { xR, yR };
    }

    private static BigInteger[] scalarMult(BigInteger[] P, BigInteger e)
    {
        if (e.equals(BigInteger.ZERO)) {
            return new BigInteger[] { BigInteger.ZERO, BigInteger.ONE };
        }
        BigInteger[] Q = scalarMult(P, e.divide(BI_2));
        Q = doublePoint(Q);
        if (e.testBit(0)) {
            Q = edwards(Q, P);
        }
        return Q;
    }

    public static byte[] encodeInt(BigInteger y)
    {
        byte[] in = y.toByteArray();
        byte[] out = new byte[Math.max(in.length, 32)];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[(in.length - 1 - i)];
        }
        return out;
    }

    public static byte[] encodePoint(BigInteger[] P)
    {
        BigInteger x = P[0];
        BigInteger y = P[1];
        byte[] out = encodeInt(y);
        if (!x.mod(BI_2).equals(BigInteger.ZERO))
        {
            int tmp34_33 = (out.length - 1); byte[] tmp34_29 = out;tmp34_29[tmp34_33] = ((byte)(tmp34_29[tmp34_33] | 0x80));
        }
        return out;
    }

    public static int bit(byte[] h, int i)
    {
        return h[(i / 8)] >> i % 8 & 0x1;
    }

    public static boolean isOnCurve(BigInteger[] P)
    {
        BigInteger x = P[0];
        BigInteger y = P[1];
        BigInteger xx = x.multiply(x);
        BigInteger yy = y.multiply(y);
        BigInteger dxxyy = Ed25519Constants.D.multiply(yy).multiply(xx);
        return xx.negate().add(yy).subtract(BigInteger.ONE).subtract(dxxyy).mod(q).equals(BigInteger.ZERO);
    }

    public static BigInteger decodeInt(byte[] s)
    {
        byte[] out = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            out[i] = s[(s.length - 1 - i)];
        }
        return new BigInteger(out).and(un);
    }

    public static BigInteger[] decodePoint(byte[] s)
    {
        byte[] yByte = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            yByte[i] = s[(s.length - 1 - i)];
        }
        BigInteger y = new BigInteger(yByte).and(un);
        BigInteger x = xRecover(y);
        if (bit(s, 255) == 1) {
            x = q.subtract(x);
        }
        return new BigInteger[] { x, y };
    }

    public byte[] scalarMultG(byte[] scalar)
    {
        return encodePoint(scalarMult(Ed25519Constants.B, decodeInt(scalar)));
    }

    public byte[] scalarMultPoint(byte[] scalar, byte[] y)
    {
        return encodePoint(scalarMult(decodePoint(y), decodeInt(scalar)));
    }

    public byte[] add(byte[] y1, byte[] y2)
    {
        BigInteger[] p1 = decodePoint(y1);
        BigInteger[] p2 = decodePoint(y2);
        return encodePoint(edwards(p1, p2));
    }
}