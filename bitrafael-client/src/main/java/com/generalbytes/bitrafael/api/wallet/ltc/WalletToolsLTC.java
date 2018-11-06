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

package com.generalbytes.bitrafael.api.wallet.ltc;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.litecoinj.core.*;
import org.litecoinj.crypto.*;
import org.litecoinj.params.MainNetParams;
import org.litecoinj.store.BlockStore;
import org.litecoinj.store.BlockStoreException;
import org.litecoinj.utils.MonetaryFormat;
import org.litecoinj.wallet.DeterministicSeed;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WalletToolsLTC implements IWalletTools {

    public static final int XPUB = 0x019da462;//Ltub
    public static final int XPRV = 0x019d9cfe;//Ltpv
    public static final int YPUB = 0x01b26ef6;//Mtub
    public static final int YPRV = 0x01b26792;//Mtpv


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

    public MasterPrivateKeyLTC getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard){
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String prv = MasterPrivateKeyLTC.serializePRV(masterKey,standard);
        return getMasterPrivateKey(prv,cryptoCurrency,standard);
    }

    @Override
    public MasterPrivateKeyLTC getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return new MasterPrivateKeyLTC(prv,standard);
    }

    @Override
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        byte[] serializedKey = Base58.decodeChecked(master.getPRV());
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        int standard = -1;
        switch (header) {
            case XPRV:
                standard = STANDARD_BIP44;//xprv
                break;
            case YPRV:
                standard = STANDARD_BIP49; //yprv
                break;
        }
        if (standard == -1) {
            return null;
        }
        DeterministicKey masterKey = MasterPrivateKeyLTC.getDeterministicKey(master.getPRV(),standard);
        if (standard == STANDARD_BIP44) {
            final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
            final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
            final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
            final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
            final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

            return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
        } else if (standard == STANDARD_BIP49) {
            final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP49, true));
            final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
            final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
            final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
            final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

            ByteBuffer bb = ByteBuffer.allocate(2+walletKey.getPubKeyHash().length);
            bb.put(new byte[]{0x00,0x14});
            bb.put(walletKey.getPubKeyHash());
            byte[] scriptSig = bb.array();
            byte[] addressBytes =  Utils.sha256hash160(scriptSig);
            return new Address(MainNetParams.get(),5,addressBytes).toBase58();
        } else if (standard == STANDARD_BIP84) {
            final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP84, true));
            final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
            final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
            final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
            final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

            return "bechadress";
        }
        return null;
    }

    @Override
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        if (!accountPUB.startsWith("Ltub") && !accountPUB.startsWith("Mtub")) {
            return null;
        }
        byte[] serializedKey = org.litecoinj.core.Base58.decodeChecked(accountPUB);
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        int standard = -1;

        boolean isPub = true;

        switch (header) {
            case XPUB:
                standard = STANDARD_BIP44;//Lpub
                isPub = true;
                break;
            case XPRV:
                standard = STANDARD_BIP44;//Ltpv
                isPub = false;
                break;
            case YPUB:
                standard = STANDARD_BIP49; //Mtub
                isPub = true;
                break;
            case YPRV:
                standard = STANDARD_BIP49; //Mtpv
                isPub = false;
                break;
        }

        if(standard == -1) {
            throw new IllegalArgumentException("Unknown header bytes in pub: " + accountPUB);
        } else {
            if (isPub) {
                int depth = buffer.get() & 255;
                DeterministicKey accountKey = deserializePub(accountPUB, standard);
                DeterministicKey walletKey = accountKey;
                if (depth != 2) {
                    //bip44
                    final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
                    walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));
                }
                if (standard == STANDARD_BIP44) {
                    return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
                } else if (standard == STANDARD_BIP49) {
                    ByteBuffer bb = ByteBuffer.allocate(2+walletKey.getPubKeyHash().length);
                    bb.put(new byte[]{0x00,0x14});
                    bb.put(walletKey.getPubKeyHash());
                    byte[] scriptSig = bb.array();
                    byte[] addressBytes =  Utils.sha256hash160(scriptSig);
                    return new Address(MainNetParams.get(),5,addressBytes).toBase58();
                }else  if (standard == STANDARD_BIP84) {
                    return null; //TODO
                }
            }else {
                return null;
            }
        }
        return null;
    }

    private DeterministicKey deserializePub(String accountPUB, int standard) {
        int header = 0;
        switch (standard) {
            case STANDARD_BIP44:
                header = XPUB;//xpub
                break;
            case STANDARD_BIP49:
                header = YPUB; //ypub
                break;
        }
        final int finalHeader = header;
        return DeterministicKey.deserializeB58(accountPUB, new NetworkParameters() {
            @Override
            public int getBip32HeaderPub() {
                return finalHeader;
            }

            @Override
            public int getBip32HeaderPriv() {
                return -1;
            }

            @Override
            public String getPaymentProtocolId() {
                return null;
            }

            @Override
            public void checkDifficultyTransitions(StoredBlock storedPrev, Block next, BlockStore blockStore) throws VerificationException, BlockStoreException {

            }

            @Override
            public Coin getMaxMoney() {
                return null;
            }

            @Override
            public Coin getMinNonDustOutput() {
                return null;
            }

            @Override
            public MonetaryFormat getMonetaryFormat() {
                return null;
            }

            @Override
            public String getUriScheme() {
                return null;
            }

            @Override
            public boolean hasMaxMoney() {
                return false;
            }

            @Override
            public BitcoinSerializer getSerializer(boolean parseRetain) {
                return null;
            }

            @Override
            public int getProtocolVersionNum(ProtocolVersion version) {
                return 0;
            }
        });
    }

    @Override
    public String getWalletPrivateKey(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = MasterPrivateKeyLTC.getDeterministicKey(master.getPRV(),master.getStandard());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(master.getStandard(), true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return walletKey.getPrivateKeyAsWiF(MainNetParams.get());
    }

    @Override
    public String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        DeterministicKey masterKey = MasterPrivateKeyLTC.getDeterministicKey(master.getPRV(),master.getStandard());
        return MasterPrivateKeyLTC.serializePUB(masterKey,master.getStandard(), accountIndex, cryptoCurrency);
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        return (new Address(MainNetParams.get(),dp.getKey().getPubKeyHash())) +"";
    }

    public boolean isAddressValid(String address, String cryptoCurrency) {
        if (address == null) {
            return false;
        }else{
            if (!(address.startsWith("L") || address.startsWith("3") || address.startsWith("M"))){
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

        if ((input.startsWith("L") || input.startsWith("3") || input.startsWith("M")) &&  input.length() <= 34) {
            //most likely address lets check it
            try {
                if (isAddressValidInternal(input)) {
                    return new Classification(Classification.TYPE_ADDRESS,IClient.LTC,input);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }else if (((input.startsWith("6")) && input.length() >= 51) || ((input.startsWith("T")) && input.length() >= 51))  {
            //most likely private key
            try {
                DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), input);
                return new Classification(Classification.TYPE_PRIVATE_KEY_IN_WIF,IClient.LTC,input);
            } catch (AddressFormatException e) {
                //e.printStackTrace();
            }
        }else if (input.startsWith("Ltub")) {
            return new Classification(Classification.TYPE_PUB,IClient.LTC,input);
        }else if (input.startsWith("Mtub")) {
            return new Classification(Classification.TYPE_PUB,IClient.LTC,input);
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
        result.add(IClient.LTC);
        return result;
    }


    public static byte[] addChecksum(byte[] input) {
        int inputLength = input.length;
        byte[] checksummed = new byte[inputLength + 4];
        System.arraycopy(input, 0, checksummed, 0, inputLength);
        byte[] checksum = Sha256Hash.hashTwice(input);
        System.arraycopy(checksum, 0, checksummed, inputLength, 4);
        return checksummed;
    }
}
