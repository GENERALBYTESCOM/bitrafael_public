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

package com.generalbytes.bitrafael.api.wallet.dash;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.Classification;
import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.api.wallet.ISignature;
import com.generalbytes.bitrafael.api.wallet.IWalletTools;
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

    public MasterPrivateKeyDASH getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency){
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String xprv = masterKey.serializePrivB58(MainNetParams.get());
        return getMasterPrivateKey(xprv,cryptoCurrency);
    }

    @Override
    public MasterPrivateKeyDASH getMasterPrivateKey(String xprv, String cryptoCurrency) {
        return new MasterPrivateKeyDASH(xprv);
    }

    @Override
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
    }

    @Override
    public String getWalletAddressFromAccountXPUB(String accountXPUB, String cryptoCurrency, int chainIndex, int index) {
        if (!accountXPUB.startsWith("drkp")) {
            return null;
        }
        byte[] serializedKey = Base58.decodeChecked(accountXPUB);
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

    @Override
    public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return walletKey.getPrivateKeyAsWiF(MainNetParams.get());
    }

    @Override
    public String getAccountXPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getXPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(PURPOSE_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        return accountKey.serializePubB58(MainNetParams.get());
    }

    private int getCoinTypeByCryptoCurrency(String cryptoCurrency) {
        if (IClient.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BITCOIN;
        }else if (IClient.LTC.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_LITECOIN;
        }else if (IClient.DASH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_DASH;
        }else if (IClient.BCH.equalsIgnoreCase(cryptoCurrency)) {
            return COIN_TYPE_BITCOIN_CASH;
        }
        return COIN_TYPE_DASH;
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
            return new Classification(Classification.TYPE_UNKNOWN, false, null);
        }
        input = input.trim().replace("\n","");
        boolean containsPrefix = false;
        String prefix = null;
        if (input.contains(":")) {
            prefix = input.substring(0, input.indexOf(":"));
            //remove leading protocol
            input = input.substring(input.indexOf(":") + 1);
            containsPrefix = true;
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
                    return new Classification(Classification.TYPE_ADDRESS,IClient.DASH,input,containsPrefix,prefix);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }else if ((input.startsWith("7") || input.startsWith("X")) && input.length() >= 51) {
            try {
                DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), input);
                return new Classification(Classification.TYPE_PRIVATE_KEY_IN_WIF,IClient.DASH,input,containsPrefix,prefix);
            } catch (AddressFormatException e) {
                //e.printStackTrace();
            }
        }else if (input.startsWith("drkp")) {
            return new Classification(Classification.TYPE_XPUB,IClient.DASH,input,containsPrefix,prefix);
        }

        return new Classification(Classification.TYPE_UNKNOWN,containsPrefix,prefix);

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
}
