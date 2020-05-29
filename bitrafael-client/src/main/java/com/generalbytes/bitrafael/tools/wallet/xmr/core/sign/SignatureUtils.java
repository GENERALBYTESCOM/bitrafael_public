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
package com.generalbytes.bitrafael.tools.wallet.xmr.core.sign;

import com.generalbytes.bitrafael.tools.wallet.xmr.core.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class SignatureUtils
{
    public static final int BYTES_IN_BIGINT = 32;

    public static byte[] packSignature(BigInteger[] c, BigInteger[] r)
    {
        if (c.length != r.length) {
            throw new IllegalArgumentException();
        }
        byte[] result = new byte[32 * c.length + 32 * r.length];
        int dstPos = 0;
        for (int i = 0; i < c.length; i++)
        {
            byte[] cBytes = toLeByteArray(c[i]);
            byte[] rBytes = toLeByteArray(r[i]);
            System.arraycopy(cBytes, 0, result, dstPos, 32);
            dstPos += 32;
            System.arraycopy(rBytes, 0, result, dstPos, 32);
            dstPos += 32;
        }
        return result;
    }

    public static byte[] toLeByteArray(BigInteger bi)
    {
        byte[] in = bi.toByteArray();
        if (in.length > 32) {
            throw new IllegalArgumentException("Not a 256 bit number: " + bi);
        }
        byte[] out = new byte[32];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[(in.length - 1 - i)];
        }
        return out;
    }

    public static BigInteger[][] unpackSignature(byte[] signature)
    {
        ArrayList<BigInteger> cList = new ArrayList<>(signature.length / 64);
        ArrayList<BigInteger> rList = new ArrayList(signature.length / 64);
        for (int ofs = 0; ofs < signature.length;)
        {
            byte[] cBytes = Arrays.copyOfRange(signature, ofs, ofs + 32);
            Utils.reverse(cBytes);
            cList.add(new BigInteger(1, cBytes));
            ofs += 32;
            byte[] rBytes = Arrays.copyOfRange(signature, ofs, ofs + 32);
            Utils.reverse(rBytes);
            rList.add(new BigInteger(1, rBytes));
            ofs += 32;
        }
        return new BigInteger[][] { (BigInteger[])cList.toArray(new BigInteger[cList.size()]), (BigInteger[])rList.toArray(new BigInteger[rList.size()]) };
    }

}
