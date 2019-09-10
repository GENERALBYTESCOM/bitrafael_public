package com.generalbytes.bitrafael.api.wallet.smart;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import cc.smartcash.smartcashj.core.*;
import cc.smartcash.smartcashj.crypto.*;
import cc.smartcash.smartcashj.params.MainNetParams;
import cc.smartcash.smartcashj.wallet.DeterministicSeed;


import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WalletToolsSMART implements IWalletTools {
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

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard) {
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split, null, password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String xprv = masterKey.serializePrivB58(MainNetParams.get());
        return getMasterPrivateKey(xprv, cryptoCurrency, standard);
    }

    @Override
    public IMasterPrivateKey getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return new MasterPrivateKeySMART(prv, standard);
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
    public String getWalletAddress(IMasterPrivateKey master, String cryptoCurrency, int accountIndex, int chainIndex, int index) {
        DeterministicKey masterKey = DeterministicKey.deserializeB58(master.getPRV(), MainNetParams.get());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return LegacyAddress.fromKey(MainNetParams.get(), walletKey.getPrivateKeyEncoded(MainNetParams.get()).getKey()).toString();
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
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        if (!accountPUB.startsWith("xpub")) {
            return null;
        }
        byte[] serializedKey = Base58.decodeChecked(accountPUB);
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        if (header != MainNetParams.get().getBip32HeaderPriv() && header != MainNetParams.get().getBip32HeaderPub()) {
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
                return LegacyAddress.fromKey(MainNetParams.get(), walletKey.decompress()).toString();
                //return Address.fromString(MainNetParams.get(), walletKey.getPubKeyHash().toString()).toString();
            } else {
                return null;
            }
        }
    }

    @Override
    public String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency) {
        String result;
        result = null;
        NetworkParameters params = MainNetParams.get();
        //do {
        DumpedPrivateKey privateKeyEncoded = new ECKey().getPrivateKeyEncoded(params);
        result = privateKeyEncoded.toString();

        //DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), new ECKey().getPrivateKeyAsWiF(MainNetParams.get()));
        //result = dp.getKey().getPrivateKeyAsWiF(MainNetParams.get()).toString();

        //}while (result == null);
        return result;
    }

    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey);

        return LegacyAddress.fromKey(MainNetParams.get(), dp.getKey()).toString();
    }

    @Override
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey) ;// fromBase58(MainNetParams.get(), privateKey);
        final ECKey key = dp.getKey();
        return new WalletToolsSMART.Signature(key.getPubKey(), key.sign(Sha256Hash.wrap(hashToSign)).encodeToDER());
    }

    public class Signature implements ISignature {
        private byte[] publicKey;
        private byte[] signature;

        Signature(byte[] publicKey, byte[] signature) {
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
    public boolean isAddressValid(String address, String cryptoCurrency) {
        String optionalPrefix = "smartcash:";

        if (address == null) {
            return false;
        }

        if (address.startsWith(optionalPrefix)) {
            address = address.substring(optionalPrefix.length(), address.length());
        }

        if (!(address.startsWith("S"))) {
            return false;
        }

        try {
            Base58.decodeToBigInteger(address);
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
        result.add(IClient.SMART);
        return result;
    }

    @Override
    public Classification classify(String input) {
        if (input == null) {
            return new Classification(Classification.TYPE_UNKNOWN);
        }
        input = input.trim().replace("\n", "");
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
            input = input.substring(0, input.indexOf("?"));
        }

        if ((input.startsWith("X")) && input.length() <= 34) {
            //most likely address lets check it
            try {
                if (isAddressValidInternal(input)) {
                    return new Classification(Classification.TYPE_ADDRESS, IClient.SMART, input);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        } else if ((input.startsWith("7") || input.startsWith("X")) && input.length() >= 51) {
            try {
                DumpedPrivateKey dp = DumpedPrivateKey.fromBase58(MainNetParams.get(), input);
                return new Classification(Classification.TYPE_PRIVATE_KEY_IN_WIF, IClient.SMART, input);
            } catch (AddressFormatException e) {
                //e.printStackTrace();
            }
        } else if (input.startsWith("drkp")) {
            return new Classification(Classification.TYPE_PUB, IClient.SMART, input);
        } else if (input.startsWith("drkv")) {
            return new Classification(Classification.TYPE_PRV, IClient.SMART, input);
        }

        return new Classification(Classification.TYPE_UNKNOWN);
    }

    @Override
    public Classification classify(String input, String cryptoCurrencyHint) {
        return classify(input);
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
}