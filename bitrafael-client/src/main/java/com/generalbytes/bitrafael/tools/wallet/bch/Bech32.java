/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.tools.wallet.bch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Bech32 {
    public static final String SEPARATOR = ":";
    public static final String MAIN_NET_PREFIX = "bitcoincash";
    public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    private static final BigInteger[] POLYMOD_GENERATORS = new BigInteger[] {
        new BigInteger("98f2bc8e61", 16),
        new BigInteger("79b76d99e2", 16),
        new BigInteger("f33e5fb3c4", 16),
        new BigInteger("ae2eabe2a8", 16),
        new BigInteger("1e4f43e470", 16)};

    private static final BigInteger POLYMOD_AND_CONSTANT = new BigInteger("07ffffffff", 16);
    private static final char[] CHARS = CHARSET.toCharArray();

    private static Map<Character, Integer> charPositionMap;

    static {
        charPositionMap = new HashMap<>();
        for (int i = 0; i < CHARS.length; i++) {
            charPositionMap.put(CHARS[i], i);
        }
        if (charPositionMap.size() != 32) {
            throw new RuntimeException("The charset must contain 32 unique characters.");
        }
    }

    public static byte[] calculateChecksumBytesPolymod(byte[] checksumInput) {
        BigInteger c = BigInteger.ONE;

        for (int i = 0; i < checksumInput.length; i++) {
            byte c0 = c.shiftRight(35).byteValue();
            c = c.and(POLYMOD_AND_CONSTANT).shiftLeft(5)
                    .xor(new BigInteger(String.format("%02x", checksumInput[i]), 16));

            if ((c0 & 0x01) != 0)
                c = c.xor(POLYMOD_GENERATORS[0]);
            if ((c0 & 0x02) != 0)
                c = c.xor(POLYMOD_GENERATORS[1]);
            if ((c0 & 0x04) != 0)
                c = c.xor(POLYMOD_GENERATORS[2]);
            if ((c0 & 0x08) != 0)
                c = c.xor(POLYMOD_GENERATORS[3]);
            if ((c0 & 0x10) != 0)
                c = c.xor(POLYMOD_GENERATORS[4]);
        }

        byte[] checksum = c.xor(BigInteger.ONE).toByteArray();
        if (checksum.length == 5) {
            return checksum;
        } else {
            byte[] newChecksumArray = new byte[5];

            System.arraycopy(checksum, Math.max(0, checksum.length - 5), newChecksumArray,
                    Math.max(0, 5 - checksum.length), Math.min(5, checksum.length));

            return newChecksumArray;
        }
    }


    public static byte[] decodeFromCharset(String base32String) {
        byte[] bytes = new byte[base32String.length()];

        char[] charArray = base32String.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            Integer position = charPositionMap.get(charArray[i]);
            if (position == null) {
                throw new RuntimeException("There seems to be an invalid char: " + charArray[i]);
            }
            bytes[i] = (byte) ((int) position);
        }

        return bytes;
    }

    public static String encodeToCharset(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (int i=0;i<data.length;i++) {
            sb.append(CHARSET.charAt((int)data[i]));
        }
        return sb.toString();
    }

    public static String encodeHashToBech32Address(String humanPart, int version, byte[] pubKeyHash) {
        // Check arguments
        Objects.requireNonNull(humanPart);
        Objects.requireNonNull(pubKeyHash);

        byte[] prefixData = concatenateByteArrays(encodePrefixToUInt5(humanPart), new byte[1]);
        byte versionByte = (byte)version;

        byte[] payloadData =  encodeToUInt5(concatenateByteArrays(new byte[]{versionByte}, pubKeyHash));
        byte[] checksumData =  concatenateByteArrays(prefixData, payloadData, new byte[8]);
        byte[] checksum = calculateChecksumBytesPolymod(checksumData);
        payloadData = concatenateByteArrays(payloadData, convertBits(checksum,8,5,true));
        return encodeToCharset(payloadData);
    }


    public static byte[] decodeCashAddress(String bitcoinCashAddress) {
        if (!isValidCashAddress(bitcoinCashAddress)) {
            throw new RuntimeException("Address wasn't valid: " + bitcoinCashAddress);
        }

        String[] addressParts = bitcoinCashAddress.split(SEPARATOR);
        int datapos = 0;
        if (addressParts.length == 2) {
            String prefix = addressParts[0];
            datapos++;
        }

        byte[] addressData = Bech32.decodeFromCharset(addressParts[datapos]);
        addressData = Arrays.copyOfRange(addressData, 0, addressData.length - 8);
        addressData = convertBits(addressData, 5, 8, true);
        byte versionByte = addressData[0];
        return Arrays.copyOfRange(addressData, 1, addressData.length);
    }

    public static boolean isValidCashAddress(String bitcoinCashAddress) {
        try {
            if (bitcoinCashAddress == null || bitcoinCashAddress.length() == 0) {
                return false;
            }
            String prefix;
            if (bitcoinCashAddress.contains(SEPARATOR)) {
                String[] split = bitcoinCashAddress.split(SEPARATOR);
                if (split.length != 2) {
                    return false;
                }
                prefix = split[0];
                bitcoinCashAddress = split[1];
            } else {
                prefix = MAIN_NET_PREFIX;
            }
            if (!isSingleCase(bitcoinCashAddress))
                return false;
            bitcoinCashAddress = bitcoinCashAddress.toLowerCase();
            byte[] checksumData = concatenateByteArrays(
                    concatenateByteArrays(encodePrefixToUInt5(prefix), new byte[]{0x00}),
                    Bech32.decodeFromCharset(bitcoinCashAddress));
            byte[] calculateChecksumBytesPolymod = calculateChecksumBytesPolymod(checksumData);
            return new BigInteger(calculateChecksumBytesPolymod).compareTo(BigInteger.ZERO) == 0;
        } catch (RuntimeException re) {
            return false;
        }
    }

    private static boolean isSingleCase(String s) {
        return s.equals(s.toLowerCase()) || s.equals(s.toUpperCase());
    }

    private static byte[] convertBits(byte[] bytes8Bits, int from, int to, boolean strictMode) {
        int length = (int) (strictMode ? Math.floor((double) bytes8Bits.length * from / to)
                : Math.ceil((double) bytes8Bits.length * from / to));
        int mask = ((1 << to) - 1) & 0xff;
        byte[] result = new byte[length];
        int index = 0;
        int accumulator = 0;
        int bits = 0;
        for (int i = 0; i < bytes8Bits.length; i++) {
            byte value = bytes8Bits[i];
            accumulator = (((accumulator & 0xff) << from) | (value & 0xff));
            bits += from;
            while (bits >= to) {
                bits -= to;
                result[index] = (byte) ((accumulator >> bits) & mask);
                ++index;
            }
        }
        if (!strictMode) {
            if (bits > 0) {
                result[index] = (byte) ((accumulator << (to - bits)) & mask);
                ++index;
            }
        } else {
            if (!(bits < from && ((accumulator << (to - bits)) & mask) == 0)) {
                throw new RuntimeException("Strict mode was used but input couldn't be converted without padding");
            }
        }

        return result;
    }

    private static byte[] encodeToUInt5(byte[] input) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();  // Every element is uint5
        // Variables/constants for bit processing
        final int IN_BITS = 8;
        final int OUT_BITS = 5;
        int inputIndex = 0;
        int bitBuffer = 0;  // Topmost bitBufferLen bits are valid; remaining lower bits are zero
        int bitBufferLen = 0;  // Always in the range [0, 12]

        // Repack all 8-bit bytes into 5-bit groups, adding padding
        while (inputIndex < input.length || bitBufferLen > 0) {
            assert 0 <= bitBufferLen && bitBufferLen <= IN_BITS + OUT_BITS - 1;
            assert (bitBuffer << bitBufferLen) == 0;

            if (bitBufferLen < OUT_BITS) {
                if (inputIndex < input.length) {  // Read a byte
                    bitBuffer |= (input[inputIndex] & 0xFF) << (32 - IN_BITS - bitBufferLen);
                    inputIndex++;
                    bitBufferLen += IN_BITS;
                } else  // Create final padding
                    bitBufferLen = OUT_BITS;
            }
            assert bitBufferLen >= 5;

            // Write a 5-bit group
            data.write(bitBuffer >>> (32 - OUT_BITS));  // uint5
            bitBuffer <<= OUT_BITS;
            bitBufferLen -= OUT_BITS;
        }
        return data.toByteArray();
    }


    public static byte[] concatenateByteArrays(byte[] ... arrays ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int i = 0; i < arrays.length; i++) {
            try {
                bos.write(arrays[i]);
            } catch (IOException e) {
            }
        }
        return bos.toByteArray();
    }


    public static byte[] encodePrefixToUInt5(String prefixString) {
        byte[] prefixBytes = new byte[prefixString.length()];
        char[] charArray = prefixString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            prefixBytes[i] = (byte) (charArray[i] & 0x1f);
        }

        return prefixBytes;
    }

    public static long bytes2Long(byte[] bytes) {
        long result = 0;
        for (byte b : bytes) {
            result <<= bytes.length;
            result |= (b & 0xFF);
        }
        return result;
    }
}
