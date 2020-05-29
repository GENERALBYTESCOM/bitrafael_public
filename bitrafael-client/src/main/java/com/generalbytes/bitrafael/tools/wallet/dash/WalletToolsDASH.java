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

package com.generalbytes.bitrafael.tools.wallet.dash;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.Classification;
import com.generalbytes.bitrafael.tools.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.tools.api.wallet.ISignature;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.dashj.core.*;
import org.dashj.crypto.*;
import org.dashj.params.MainNetParams;
import org.dashj.wallet.DeterministicSeed;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WalletToolsDASH implements IWalletTools {

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

    public MasterPrivateKeyDASH getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard){
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String xprv = masterKey.serializePrivB58(MainNetParams.get());
        return getMasterPrivateKey(xprv,cryptoCurrency, standard);
    }

    @Override
    public MasterPrivateKeyDASH getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return new MasterPrivateKeyDASH(prv, standard);
    }

    @Override
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
    }

    @Override
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        if (!accountPUB.startsWith("drkp")) {
            return null;
        }
        byte[] serializedKey = Base58.decodeChecked(accountPUB);
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        if(header != MainNetParams.get().getBip32HeaderPriv() && header != MainNetParams.get().getBip32HeaderPub()) {
            throw new IllegalArgumentException("Unknown header bytes in xpub: " + accountPUB);
        } else {
            boolean pub = header == MainNetParams.get().getBip32HeaderPub();
            if (pub) {
                int depth = buffer.get() & 255;
                DeterministicKey accountKey = DeterministicKey.deserializeB58(accountPUB, MainNetParams.get());
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

    @Override
    public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return walletKey.getPrivateKeyAsWiF(MainNetParams.get());
    }

    @Override
    public String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        return accountKey.serializePubB58(MainNetParams.get());
    }


    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
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

    public boolean isAddressValid(String address, String cryptoCurrency) {
        if (address == null) {
            return false;
        }else{
            if (!(address.startsWith("X"))){
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

    @Override
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        final ECKey key = dp.getKey();
        return new Signature(key.getPubKey(),key.sign(Sha256Hash.wrap(hashToSign)).encodeToDER());
    }

    public class Signature implements ISignature {
        private byte[] publicKey;
        private byte[] signature;

        public Signature(byte[] publicKey, byte[] signature) {
            this.publicKey = publicKey;
            this.signature = signature;
        }

        @Override
        public byte[] getPublicKey() {
            return publicKey;
        }

        @Override
        public byte[] getSignature() {
            return signature;
        }
    }

    @Override
    public Classification classify(String input, String cryptoCurrencyHint) {
        return classify(input);
    }

    @Override
    public Classification classify(String input) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n","");
        if (input.contains(":")) {
            //remove leading protocol
            input = input.substring(input.indexOf(":") + 1);
        }

        //remove leading slashes
        if (input.startsWith("//")) {
            input = input.substring("//".length());
        }

        //remove things after
        if (input.contains("?")) {
            input = input.substring(0,input.indexOf("?"));
        }

        if ((input.startsWith("X")) &&  input.length() <= 34) {
            //most likely address lets check it
            try {
                if (isAddressValidInternal(input)) {
                    return new Classification(Classification.TYPE_ADDRESS, IClient.DASH,input);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }else if ((input.startsWith("7") || input.startsWith("X")) && input.length() >= 51) {
            try {
                DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), input);
                return new Classification(Classification.TYPE_PRIVATE_KEY_IN_WIF, IClient.DASH,input);
            } catch (AddressFormatException e) {
                //e.printStackTrace();
            }
        }else if (input.startsWith("drkp")) {
            return new Classification(Classification.TYPE_PUB, IClient.DASH,input);
        }else if (input.startsWith("drkv")) {
            return new Classification(Classification.TYPE_PRV, IClient.DASH,input);
        }

        return new Classification(Classification.TYPE_UNKNOWN);

    }

    private static boolean isAddressValidInternal(String address) {
        try {
            Base58.decodeChecked(address);
        } catch (AddressFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Set<String> supportedCryptoCurrencies() {
        final HashSet<String> result = new HashSet<String>();
        result.add(IClient.DASH);
        return result;
    }

    public String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency) {
        String result = null;
        NetworkParameters params = MainNetParams.get();
        do {
            ECKey key = new ECKey();
            Address address = new Address(params, key.getPubKeyHash());
            if (prefix == null || prefix.isEmpty() || address.toString().startsWith(prefix)) {
                DumpedPrivateKey privateKeyEncoded = key.getPrivateKeyEncoded(params);
                result = privateKeyEncoded.toString();
            }
        }while (result == null);
        return result;
    }
}
