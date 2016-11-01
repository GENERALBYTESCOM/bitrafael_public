/*************************************************************************************
 * Copyright (C) 2016 GENERAL BYTES s.r.o. All rights reserved.
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

package com.generalbytes.bitrafael.api.wallet;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class WalletTools implements IWalletTools{


    @Override
    public String generateSeedMnemonicSeparatedBySpaces() {
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            List<String> words = MnemonicCode.INSTANCE.toMnemonic(Sha256Hash.create(prng.generateSeed(32)).getBytes());
            return Joiner.on(" ").join(words);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password){
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String xprv = masterKey.serializePrivB58(MainNetParams.get());
        return getMasterPrivateKey(xprv);
    }

    @Override
    public MasterPrivateKey getMasterPrivateKey(String xprv) {
        return new MasterPrivateKey(xprv);
    }

    @Override
    public String getWalletAddress(MasterPrivateKey master, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(COIN_TYPE_BITCOIN, true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
    }

    @Override
    public String getWalletAddressFromAccountXPUB(String accountXPUB, int chainIndex, int index) {
        if (!accountXPUB.startsWith("xpub")) {
            return null;
        }
        byte[] serializedKey = org.bitcoinj.core.Base58.decodeChecked(accountXPUB);
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        if(header != MainNetParams.get().getBip32HeaderPriv() && header != MainNetParams.get().getBip32HeaderPub()) {
            throw new IllegalArgumentException("Unknown header bytes in xpub: " + accountXPUB);
        } else {
            boolean pub = header == MainNetParams.get().getBip32HeaderPub();
            if (pub) {
                int depth = buffer.get() & 255;
                DeterministicKey accountKey = DeterministicKey.deserializeB58(accountXPUB, MainNetParams.get());
                DeterministicKey walletKey = accountKey;
                if (depth != 2) {
                    //bip44
                    final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
                    walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));
                }
                return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
            }else {
                return null;
            }
        }
    }

    public String getWalletPrivateKey(MasterPrivateKey master, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(COIN_TYPE_BITCOIN, true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return walletKey.getPrivateKeyAsWiF(MainNetParams.get());
    }

    @Override
    public String getAccountXPUB(MasterPrivateKey master, int accountIndex) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(COIN_TYPE_BITCOIN, true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        return accountKey.serializePubB58(MainNetParams.get());
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        return (new Address(MainNetParams.get(),dp.getKey().getPubKeyHash())) +"";
    }

    public static DeterministicKey createMasterPubKeyFromPubB58(String xpubstr) throws AddressFormatException
    {
        byte[] data = Base58.decodeChecked(xpubstr);
        ByteBuffer ser = ByteBuffer.wrap(data);
        if (ser.getInt() != 0x0488B21E)
            throw new AddressFormatException("bad xpub version");
        ser.get();		// depth
        ser.getInt();	// parent fingerprint
        ser.getInt();	// child number
        byte[] chainCode = new byte[32];
        ser.get(chainCode);
        byte[] pubBytes = new byte[33];
        ser.get(pubBytes);


        final DeterministicKey masterPubKeyFromBytes = HDKeyDerivation.createMasterPubKeyFromBytes(pubBytes, chainCode);
        return masterPubKeyFromBytes;
    }

    @Override
    public boolean isAddressValid(String address) {
        if (address == null) {
            return false;
        }else{
            if (!(address.startsWith("1") || address.startsWith("3"))){
                return false;
            }
        }

        try {
            Base58.decodeChecked(address);
        } catch (AddressFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
