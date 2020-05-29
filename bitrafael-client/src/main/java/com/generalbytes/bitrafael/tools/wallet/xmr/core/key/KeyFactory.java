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
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Constants;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Ops;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.ReferenceEd25519Ops;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.fast.ge_p3;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.fast.ge_scalarmult_base;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import static com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.fast.ge_p3_tobytes.ge_p3_tobytes;


public class KeyFactory {
    private Ed25519Ops ed25519Ops;

    public KeyFactory()
    {
        this(new ReferenceEd25519Ops());
    }

    public KeyFactory(Ed25519Ops ed25519Ops)
    {
        this.ed25519Ops = ed25519Ops;
    }

    public PrivateKey generatePrivateKey()
    {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger r = Utils.randomBigInt(secureRandom, Ed25519Constants.L);
        return new PrivateKey(Utils.as256BitLe(r));
    }

    public PrivateKey decodePrivateKey(byte[] encoded)
            throws InvalidKeyException
    {
        validateKeySize(encoded);
        byte[] buf = new byte[encoded.length];
        System.arraycopy(encoded, 0, buf, 0, buf.length);
        Utils.reverse(buf);
        if (new BigInteger(1, buf).compareTo(Ed25519Constants.L) > 0) {
            throw new InvalidKeyException("Scalar is out of range.");
        }
        return new PrivateKey(encoded);
    }

    public PublicKey generatePublicKey(PrivateKey privateKey)
    {
        byte[] encoded = privateKey.getEncoded();
//        byte[] bytes = this.ed25519Ops.scalarMultG(encoded); (this method was replaced with faster implementation down bellow)
        byte[] bytes = new byte[32];
        ge_p3 ge_p3 = new ge_p3();
        ge_scalarmult_base.ge_scalarmult_base(ge_p3,encoded);
        ge_p3_tobytes(bytes,ge_p3);

        return new PublicKey(bytes);
    }

    public PublicKey decodePublicKey(byte[] encoded) throws InvalidKeyException
    {
        validateKeySize(encoded);
        return new PublicKey(encoded);
    }

    public KeyPair generateKeyPair()
    {
        return generateKeyPair(generatePrivateKey());
    }

    public KeyPair generateKeyPair(PrivateKey privateKey)
    {
        PublicKey publicKey = generatePublicKey(privateKey);
        return new KeyPair(privateKey, publicKey);
    }

    private void validateKeySize(byte[] key) throws InvalidKeyException
    {
        if (key == null) {
            throw new InvalidKeyException("Null array cannot be a valid key.");
        }
        if (key.length != 32) {
            throw new InvalidKeyException("Invalid key size specified: " + key.length);
        }
    }
}
