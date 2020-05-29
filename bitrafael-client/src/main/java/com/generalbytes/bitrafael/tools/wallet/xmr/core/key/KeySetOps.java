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
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Varint;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Constants;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.Ed25519Ops;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.ed25519.ReferenceEd25519Ops;

import java.math.BigInteger;
import java.security.InvalidKeyException;

public class KeySetOps {

    private final PrivateKey a;
    private final PrivateKey b;
    private final PublicKey pA;
    private final PublicKey pB;
    final Keccak256 keccak = new Keccak256();
    final Ed25519Ops ed25519Ops;
    final KeyFactory keyFactory;

    public KeySetOps(PrivateKey a, PrivateKey b, PublicKey pA, PublicKey pB)
    {
        this(a, b, pA, pB, new ReferenceEd25519Ops());
    }

    public KeySetOps(PrivateKey a, PrivateKey b, PublicKey pA, PublicKey pB, Ed25519Ops ed25519Ops)
    {
        this.a = a;
        this.b = b;
        this.pA = pA;
        this.pB = pB;
        this.ed25519Ops = ed25519Ops;
        this.keyFactory = new KeyFactory(ed25519Ops);
    }

    public PublicKey calculateFromViewKey(long nonce, PublicKey pR)
    {
        if ((this.a == null) || (this.pB == null)) {
            throw new IllegalStateException("Cannot calculate view key. At least one of the tuple (a, B) is null.");
        }
        byte[] nonceBytes = Varint.valueOf(nonce).toByteArray();
        byte[] aR = this.ed25519Ops.scalarMultPoint(this.a.getEncoded(), pR.getEncoded());
        byte[] aHash = aHash(Utils.concat(new byte[][] { aR, nonceBytes }));
        try
        {
            return this.keyFactory.decodePublicKey(this.ed25519Ops.add(this.ed25519Ops.scalarMultG(aHash), this.pB.getEncoded()));
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalStateException("Cannot happen.");
        }
    }

    public PrivateKey calculateFromSpendKey(long nonce, PublicKey pR)
    {
        if ((this.a == null) || (this.b == null)) {
            throw new IllegalStateException("Cannot calculate spend key. At leas one of the tuple (a, b) is null.");
        }
        byte[] nonceBytes = Varint.valueOf(nonce).toByteArray();
        byte[] aR = this.ed25519Ops.scalarMultPoint(this.a.getEncoded(), pR.getEncoded());
        byte[] aHash = aHash(Utils.concat(new byte[][] { aR, nonceBytes }));
        BigInteger mod = Utils.leToPositiveBigInt(aHash).add(Utils.leToPositiveBigInt(this.b.getEncoded())).mod(Ed25519Constants.L);
        try
        {
            return this.keyFactory.decodePrivateKey(Utils.as256BitLe(mod));
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalStateException("Cannot happen.", e);
        }
    }

    public PublicKey calculateToAddressKeys(KeyPair rR, long nonce)
    {
        PrivateKey r = rR.getPrivateKey();
        if ((this.pA == null) || (this.pB == null)) {
            throw new IllegalStateException("This operation is not supported as at least one of (A, B) is missing");
        }
        byte[] nonceBytes = Varint.valueOf(nonce).toByteArray();
        byte[] rA = this.ed25519Ops.scalarMultPoint(r.getEncoded(), this.pA.getEncoded());
        byte[] aHash = aHash(Utils.concat(new byte[][] { rA, nonceBytes }));
        byte[] pEncoded = this.ed25519Ops.add(this.ed25519Ops.scalarMultG(aHash), this.pB.getEncoded());
        try
        {
            return this.keyFactory.decodePublicKey(pEncoded);
        }
        catch (InvalidKeyException e)
        {
            throw new IllegalStateException("Cannot happen.", e);
        }
    }

    public PrivateKey getSecretA()
    {
        return this.a;
    }

    public PrivateKey getSecretB()
    {
        return this.b;
    }

    public PublicKey getPublicA()
    {
        return this.pA;
    }

    public PublicKey getPublicB()
    {
        return this.pB;
    }

    private byte[] aHash(byte[] bytes)
    {
        this.keccak.reset();
        this.keccak.update(bytes);
        byte[] digest = this.keccak.digest().array();

        return Utils.as256BitLe(Utils.leToPositiveBigInt(digest).mod(Ed25519Constants.L));
    }
}
