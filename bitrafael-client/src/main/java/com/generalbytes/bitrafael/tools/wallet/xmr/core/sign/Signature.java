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
import com.generalbytes.bitrafael.tools.wallet.xmr.core.key.*;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Keccak256;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Constants;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Ops;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.ReferenceEd25519Ops;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Signature
{
    private final Ed25519Ops ed25519Ops;
    private final Keccak256 keccak256 = new Keccak256();
    private final Random random;
    private final KeyHashProvider keyHashProvider;
    private final KeyImageProvider keyImageProvider;

    public Signature()
    {
        this(new ReferenceEd25519Ops());
    }

    public Signature(Ed25519Ops ed25519Ops)
    {
        this(ed25519Ops, new SecureRandom());
    }

    public Signature(Ed25519Ops ed25519Ops, Random random)
    {
        this(ed25519Ops, random, new KeyHashProviderImpl(ed25519Ops));
    }

    public Signature(Ed25519Ops ed25519Ops, Random random, KeyHashProvider keyHashProvider)
    {
        this.ed25519Ops = ed25519Ops;
        this.random = random;
        this.keyHashProvider = keyHashProvider;
        this.keyImageProvider = new KeyImageProviderImpl(ed25519Ops, keyHashProvider);
    }

    public byte[] generateSignature(byte[] m, PublicKey[] publicKeys, int i, PrivateKey privateKey)
    {
        int n = publicKeys.length;
        BigInteger[] c = new BigInteger[n];
        BigInteger[] r = new BigInteger[n];
        BigInteger k = Utils.randomBigInt(this.random, Ed25519Constants.L);
        for (int j = 0; j < n; j++) {
            if (j != i)
            {
                c[j] = Utils.randomBigInt(this.random, Ed25519Constants.L);
                r[j] = Utils.randomBigInt(this.random, Ed25519Constants.L);
            }
        }
        byte[][] vX = new byte[n][];
        byte[][] vY = new byte[n][];

        byte[] vI = this.keyImageProvider.getKeyImage(privateKey, publicKeys[i]);
        for (int j = 0; j < n; j++) {
            if (j != i)
            {
                byte[] vAj = publicKeys[j].getEncoded();
                byte[] vHAj = this.keyHashProvider.hash(publicKeys[j]);
                byte[] cj = SignatureUtils.toLeByteArray(c[j]);
                byte[] rj = SignatureUtils.toLeByteArray(r[j]);
                vX[j] = this.ed25519Ops.add(this.ed25519Ops.scalarMultPoint(cj, vAj), this.ed25519Ops.scalarMultG(rj));

                vY[j] = this.ed25519Ops.add(this.ed25519Ops.scalarMultPoint(cj, vI), this.ed25519Ops.scalarMultPoint(rj, vHAj));
            }
        }
        byte[] vHAi = this.keyHashProvider.hash(publicKeys[i]);
        byte[] kBytes = SignatureUtils.toLeByteArray(k);
        vX[i] = this.ed25519Ops.scalarMultG(kBytes);
        vY[i] = this.ed25519Ops.scalarMultPoint(kBytes, vHAi);

        this.keccak256.reset();
        this.keccak256.update(m);
        byte[] mDigest = this.keccak256.digest().array();

        this.keccak256.reset();
        this.keccak256.update(concatArrays(mDigest, vX, vY));
        byte[] digest = this.keccak256.digest().array();

        BigInteger digestVal = Utils.digestModulo(digest, Ed25519Constants.L);

        BigInteger sum = BigInteger.ZERO;
        for (int j = 0; j < n; j++) {
            if (j != i) {
                sum = sum.add(c[j]).mod(Ed25519Constants.L);
            }
        }
        c[i] = digestVal.subtract(sum).mod(Ed25519Constants.L);
        byte[] encoded = privateKey.getEncoded();
        Utils.reverse(encoded);
        BigInteger pk = new BigInteger(1, encoded);
        r[i] = k.subtract(pk.multiply(c[i]).mod(Ed25519Constants.L)).mod(Ed25519Constants.L);

        return SignatureUtils.packSignature(c, r);
    }

    public boolean verifySignature(byte[] m, PublicKey[] publicKeys, byte[] keyImage, byte[] signature)
    {
        this.keccak256.reset();
        this.keccak256.update(m);
        return verifySignatureHash(this.keccak256.digest().array(), publicKeys, keyImage, signature);
    }

    boolean verifySignatureHash(byte[] messageHash, PublicKey[] publicKeys, byte[] keyImage, byte[] signature)
    {
        int n = publicKeys.length;

        BigInteger[][] cr = SignatureUtils.unpackSignature(signature);
        BigInteger[] c = cr[0];
        BigInteger[] r = cr[1];

        byte[][] vX = new byte[n][];
        byte[][] vY = new byte[n][];
        for (int i = 0; i < n; i++)
        {
            byte[] ci = SignatureUtils.toLeByteArray(c[i]);
            byte[] ri = SignatureUtils.toLeByteArray(r[i]);

            vX[i] = this.ed25519Ops.add(this.ed25519Ops.scalarMultPoint(ci, publicKeys[i].getEncoded()), this.ed25519Ops.scalarMultG(ri));

            vY[i] = this.ed25519Ops.add(this.ed25519Ops.scalarMultPoint(ci, keyImage), this.ed25519Ops.scalarMultPoint(ri, this.keyHashProvider.hash(publicKeys[i])));
        }
        this.keccak256.reset();
        this.keccak256.update(concatArrays(messageHash, vX, vY));
        byte[] digest = this.keccak256.digest().array();

        BigInteger sumDigest = Utils.digestModulo(digest, Ed25519Constants.L);

        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < n; i++) {
            sum = sum.add(c[i]).mod(Ed25519Constants.L);
        }
        return sumDigest.equals(sum);
    }

    private static byte[] concatArrays(byte[] prefix, byte[][] vX, byte[][] vY)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            baos.write(prefix);
            for (int i = 0; i < vX.length; i++)
            {
                baos.write(vX[i]);
                baos.write(vY[i]);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
        return baos.toByteArray();
    }
}
