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
package com.generalbytes.bitrafael.tools.wallet.xmr.core.common;

import com.generalbytes.bitrafael.tools.wallet.xmr.core.Utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Vector;

//Monero is using different type of Base58 then Bitcoin

public class Base58
{
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final int[] ENCODED_BLOCK_SIZES = { 0, 2, 3, 5, 6, 7, 9, 10, 11 };
    private static final int FULL_BLOCK_SIZE = ENCODED_BLOCK_SIZES.length - 1;
    private static final int FULL_ENCODED_BLOCK_SIZE = ENCODED_BLOCK_SIZES[FULL_BLOCK_SIZE];
    public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
    private static final BigInteger ALPHABET_LENGTH_BIG_INT = BigInteger.valueOf(ALPHABET.length);
    private static final long P_DIV_ALPHABET = new BigInteger(1, new byte[] { Byte.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0 }).divide(ALPHABET_LENGTH_BIG_INT).longValue();
    private static final long P_MOD_ALPHABET = new BigInteger(1, new byte[] { Byte.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0 }).mod(ALPHABET_LENGTH_BIG_INT).longValue();

    private static class ReverseAlphabet
    {
        private Vector<Byte> data;

        private ReverseAlphabet()
        {
            this.data = new Vector();
            this.data.setSize(Base58.ALPHABET[(Base58.ALPHABET.length - 1)] - Base58.ALPHABET[0] + 1);
            for (int i = 0; i < Base58.ALPHABET.length; i++)
            {
                int idx = Base58.ALPHABET[i] - Base58.ALPHABET[0];
                this.data.set(idx, Byte.valueOf((byte)i));
            }
        }

        public int get(char letter)
        {
            int idx = letter - Base58.ALPHABET[0];
            return (idx >= 0) && (idx < this.data.size()) ? ((Byte)this.data.get(idx)).byteValue() : -1;
        }

        static ReverseAlphabet INSTANCE = new ReverseAlphabet();
    }

    private static class DecodedBlockSizes
    {
        private Vector<Integer> data;

        private DecodedBlockSizes()
        {
            this.data = new Vector();
            this.data.setSize(Base58.ENCODED_BLOCK_SIZES[Base58.FULL_BLOCK_SIZE] + 1);
            for (int i = 0; i <= Base58.FULL_BLOCK_SIZE; i++) {
                this.data.set(Base58.ENCODED_BLOCK_SIZES[i], Integer.valueOf(i));
            }
        }

        int get(int encodedBlockSize)
        {
            if (encodedBlockSize > Base58.FULL_ENCODED_BLOCK_SIZE) {
                throw new IllegalArgumentException();
            }
            Integer integer = (Integer)this.data.get(encodedBlockSize);
            return integer == null ? -1 : integer.intValue();
        }

        static DecodedBlockSizes INSTANCE = new DecodedBlockSizes();
    }

    private static byte[] longToBytesBe(long num, int size)
    {
        byte[] res = new byte[8];
        Utils.encodeBELong(num, res, 0);
        return Arrays.copyOfRange(res, res.length - size, res.length);
    }

    private static long asBeLong(byte[] data, int off, int size)
    {
        long res = 0L;
        int i = 0;
        switch (9 - size)
        {
            case 1:
                res |= 0xFF & data[(off + i++)];
            case 2:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 3:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 4:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 5:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 6:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 7:
                res <<= 8;res |= 0xFF & data[(off + i++)];
            case 8:
                res <<= 8;res |= 0xFF & data[(off + i)];
                break;
            default:
                throw new IllegalArgumentException("Invalid size");
        }
        return res;
    }

    private static void encodeBlock(byte[] data, int dataOffset, int size, byte[] res, int resOffset)
    {
        if ((size < 1) || (size > FULL_BLOCK_SIZE)) {
            throw new IllegalArgumentException();
        }
        long num = asBeLong(data, dataOffset, size);
        int i = ENCODED_BLOCK_SIZES[size] - 1;
        while (num != 0L)
        {
            long rem;
            if (num < 0L)
            {
                rem = ((num & 0x7FFFFFFFFFFFFFFFL) % ALPHABET.length + P_MOD_ALPHABET) % ALPHABET.length;
                num = P_DIV_ALPHABET + (num & 0x7FFFFFFFFFFFFFFFL) / ALPHABET.length + ((num & 0x7FFFFFFFFFFFFFFFL) % ALPHABET.length + P_MOD_ALPHABET) / ALPHABET.length;
            }
            else
            {
                rem = num % ALPHABET.length;
                num /= ALPHABET.length;
            }
            res[(resOffset + i)] = ((byte)ALPHABET[((int)rem)]);
            i--;
        }
    }

    private static boolean decodeBlock(byte[] block, int offset, int size, byte[] result, int resOffset)
    {
        if ((size < 1) || (size > FULL_ENCODED_BLOCK_SIZE)) {
            throw new IllegalArgumentException();
        }
        int resSize = DecodedBlockSizes.INSTANCE.get(size);
        if (size <= 0) {
            return false;
        }
        long resNum = 0L;
        long order = 1L;
        for (int i = size - 1; (i < size) && (i >= 0); i--)
        {
            int digit = ReverseAlphabet.INSTANCE.get((char)block[(offset + i)]);
            if (digit < 0) {
                return false;
            }
            resNum += order * digit;
            order *= ALPHABET.length;
        }
        byte[] bytes = longToBytesBe(resNum, resSize);
        System.arraycopy(bytes, 0, result, resOffset, bytes.length);

        return true;
    }

    public static String encode(String dataString)
    {
        return new String(encode(dataString.getBytes(DEFAULT_CHARSET)));
    }

    public static byte[] decode(String encodedString)
    {
        return decode(encodedString.getBytes(DEFAULT_CHARSET));
    }

    public static byte[] encode(byte[] data)
    {
        if (data.length == 0) {
            return new byte[0];
        }
        int fullBlockCount = data.length / FULL_BLOCK_SIZE;
        int lastBlockSize = data.length % FULL_BLOCK_SIZE;

        int resSize = fullBlockCount * FULL_ENCODED_BLOCK_SIZE + ENCODED_BLOCK_SIZES[lastBlockSize];
        byte[] res = new byte[resSize];
        for (int i = 0; i < res.length; i++) {
            res[i] = ((byte)ALPHABET[0]);
        }
        for (int i = 0; i < fullBlockCount; i++) {
            encodeBlock(data, i * FULL_BLOCK_SIZE, FULL_BLOCK_SIZE, res, i * FULL_ENCODED_BLOCK_SIZE);
        }
        if (lastBlockSize > 0) {
            encodeBlock(data, fullBlockCount * FULL_BLOCK_SIZE, lastBlockSize, res, fullBlockCount * FULL_ENCODED_BLOCK_SIZE);
        }
        return res;
    }

    public static byte[] decode(byte[] encoded)
    {
        if (encoded.length == 0) {
            return new byte[0];
        }
        int fullBlockCount = encoded.length / FULL_ENCODED_BLOCK_SIZE;
        int lastBlockSize = encoded.length % FULL_ENCODED_BLOCK_SIZE;
        int lastBlockDecodedSize = DecodedBlockSizes.INSTANCE.get(lastBlockSize);
        if (lastBlockDecodedSize < 0) {
            return null;
        }
        int dataSize = fullBlockCount * FULL_BLOCK_SIZE + lastBlockDecodedSize;
        byte[] data = new byte[dataSize];
        for (int i = 0; i < fullBlockCount; i++) {
            if (!decodeBlock(encoded, i * FULL_ENCODED_BLOCK_SIZE, FULL_ENCODED_BLOCK_SIZE, data, i * FULL_BLOCK_SIZE)) {
                return null;
            }
        }
        if ((lastBlockDecodedSize > 0) &&
                (!decodeBlock(encoded, fullBlockCount * FULL_ENCODED_BLOCK_SIZE, lastBlockSize, data, fullBlockCount * FULL_BLOCK_SIZE))) {
            return null;
        }
        return data;
    }
}
