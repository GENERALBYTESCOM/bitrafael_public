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
package com.generalbytes.bitrafael.tools.wallet.xmr.core;


import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Constants;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;


public final class Utils {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    public static byte[] xor(byte[] arr1, byte[] arr2)
    {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Arrays length doesn't match.");
        }
        byte[] result = new byte[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = ((byte)(arr1[i] ^ arr2[i]));
        }
        return result;
    }

    public static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[(j * 2)] = HEX_DIGITS[(v >>> 4)];
            hexChars[(j * 2 + 1)] = HEX_DIGITS[(v & 0xF)];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex)
    {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[(i / 2)] = ((byte)((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16)));
        }
        return data;
    }

    public static void encodeBELong(long val, byte[] buf, int off)
    {
        buf[off] = ((byte)(int)(val >>> 56));
        buf[(off + 1)] = ((byte)(int)(val >>> 48));
        buf[(off + 2)] = ((byte)(int)(val >>> 40));
        buf[(off + 3)] = ((byte)(int)(val >>> 32));
        buf[(off + 4)] = ((byte)(int)(val >>> 24));
        buf[(off + 5)] = ((byte)(int)(val >>> 16));
        buf[(off + 6)] = ((byte)(int)(val >>> 8));
        buf[(off + 7)] = ((byte)(int)val);
    }

    public static void encodeBEInt(int val, byte[] buf, int off)
    {
        buf[off] = ((byte)(val >>> 24));
        buf[(off + 1)] = ((byte)(val >>> 16));
        buf[(off + 2)] = ((byte)(val >>> 8));
        buf[(off + 3)] = ((byte)val);
    }

    public static void encodeLEInt(int val, byte[] buf, int off)
    {
        buf[off] = ((byte)val);
        buf[(off + 1)] = ((byte)(val >>> 8));
        buf[(off + 2)] = ((byte)(val >>> 16));
        buf[(off + 3)] = ((byte)(val >>> 24));
    }

    public static int decodeBEInt(byte[] buf, int off)
    {
        return (buf[off] & 0xFF) << 24 | (buf[(off + 1)] & 0xFF) << 16 | (buf[(off + 2)] & 0xFF) << 8 | buf[(off + 3)] & 0xFF;
    }

    public static int decodeLEInt(byte[] buf, int off)
    {
        return buf[off] & 0xFF | (buf[(off + 1)] & 0xFF) << 8 | (buf[(off + 2)] & 0xFF) << 16 | (buf[(off + 3)] & 0xFF) << 24;
    }

    public static long decodeBELong(byte[] buf, int off)
    {
        return (buf[off] & 0xFF) << 56 | (buf[(off + 1)] & 0xFF) << 48 | (buf[(off + 2)] & 0xFF) << 40 | (buf[(off + 3)] & 0xFF) << 32 | (buf[(off + 4)] & 0xFF) << 24 | (buf[(off + 5)] & 0xFF) << 16 | (buf[(off + 6)] & 0xFF) << 8 | buf[(off + 7)] & 0xFF;
    }

    public static void encodeLELong(long val, byte[] buf, int off)
    {
        buf[off] = ((byte)(int)val);
        buf[(off + 1)] = ((byte)(int)(val >>> 8));
        buf[(off + 2)] = ((byte)(int)(val >>> 16));
        buf[(off + 3)] = ((byte)(int)(val >>> 24));
        buf[(off + 4)] = ((byte)(int)(val >>> 32));
        buf[(off + 5)] = ((byte)(int)(val >>> 40));
        buf[(off + 6)] = ((byte)(int)(val >>> 48));
        buf[(off + 7)] = ((byte)(int)(val >>> 56));
    }

    public static long decodeLELong(byte[] buf, int off)
    {
        return buf[off] & 0xFF | (buf[(off + 1)] & 0xFF) << 8 | (buf[(off + 2)] & 0xFF) << 16 | (buf[(off + 3)] & 0xFF) << 24 | (buf[(off + 4)] & 0xFF) << 32 | (buf[(off + 5)] & 0xFF) << 40 | (buf[(off + 6)] & 0xFF) << 48 | (buf[(off + 7)] & 0xFF) << 56;
    }

    public static byte[] reverse(byte[] arr)
    {
        int i = 0;
        for (int j = arr.length - 1; i < arr.length / 2; j--)
        {
            byte tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;i++;
        }
        return arr;
    }

    public static byte[] concat(byte[]... bytes)
    {
        int len = 0;
        for (byte[] aByte : bytes) {
            len += aByte.length;
        }
        byte[] result = new byte[len];
        int dstPos = 0;
        for (byte[] b : bytes)
        {
            System.arraycopy(b, 0, result, dstPos, b.length);
            dstPos += b.length;
        }
        return result;
    }

    public static byte[] as256BitLe(BigInteger bigInteger)
    {
        byte[] in = bigInteger.toByteArray();
        byte[] out = new byte[32];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[(in.length - 1 - i)];
        }
        return out;
    }

    public static BigInteger leToPositiveBigInt(byte[] bytes)
    {
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = bytes[(bytes.length - 1 - i)];
        }
        return new BigInteger(1, out);
    }
    public static byte[] sc_reduce32(byte[] in) {
        byte [] copy = Arrays.copyOf(in,in.length);
        Utils.reverse(copy);
        BigInteger x = new BigInteger(1, copy).mod(Ed25519Constants.L);
        return Utils.as256BitLe(x);
    }

    public static boolean isEven(BigInteger value)
    {
        return !isOdd(value);
    }

    public static boolean isOdd(BigInteger value)
    {
        return value.testBit(0);
    }

    public static int negative(int b)
    {
        return b >> 8 & 0x1;
    }

    public static int bit(byte[] h, int i)
    {
        return h[(i >> 3)] >> (i & 0x7) & 0x1;
    }

    public static BigInteger randomBigInt(Random random, BigInteger upperVal)
    {
        BigInteger r;
        do
        {
            r = new BigInteger(upperVal.bitLength(), random);
        } while (r.compareTo(upperVal) >= 0);
        return r;
    }

    public static BigInteger digestModulo(byte[] digest, BigInteger modulo)
    {
        Utils.reverse(digest);
        return new BigInteger(1, digest).mod(modulo);
    }

}
