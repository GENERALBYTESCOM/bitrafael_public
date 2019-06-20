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

package com.generalbytes.bitrafael.api.wallet.eth;

import com.generalbytes.bitrafael.api.client.IClient;
import com.generalbytes.bitrafael.api.wallet.*;
import com.generalbytes.bitrafael.api.wallet.xmr.core.common.Keccak256;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.utils.MonetaryFormat;
import org.bitcoinj.wallet.DeterministicSeed;
import org.spongycastle.crypto.digests.SHA3Digest;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class WalletToolsETH implements IWalletTools {

    public static final int XPUB = 0x0488b21e;
    public static final int XPRV = 0x0488ade4;
    private static final String HEX_CHARS = "0123456789abcdef";


    @Override
    public String generateSeedMnemonicSeparatedBySpaces() {
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            List<String> words = MnemonicCode.INSTANCE.toMnemonic(Sha256Hash.of(prng.generateSeed(32)).getBytes());
            return Joiner.on(" ").join(words);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MasterPrivateKeyETH getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password, String cryptoCurrency, int standard){
        if (password == null) {
            password = "";
        }
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String prv = MasterPrivateKeyETH.serializePRV(masterKey,standard);
        return getMasterPrivateKey(prv,cryptoCurrency,standard);
    }

    @Override
    public MasterPrivateKeyETH getMasterPrivateKey(String prv, String cryptoCurrency, int standard) {
        return new MasterPrivateKeyETH(prv,standard);
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
        }
        if (standard == -1) {
            return null;
        }
        DeterministicKey masterKey = MasterPrivateKeyETH.getDeterministicKey(master.getPRV(),standard);
        if (standard == STANDARD_BIP44) {
            final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(STANDARD_BIP44, true));
            final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
            final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
            final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
            final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

            Keccak256 hash = new Keccak256();
            hash.update(walletKey.getPubKeyPoint().getAffineXCoord().getEncoded());
            hash.update(walletKey.getPubKeyPoint().getAffineYCoord().getEncoded());
            byte[] result = hash.digestArray();
            byte[] address = new byte[20];
            System.arraycopy(result,result.length - address.length, address,0, address.length);
            return encodeAddressToChecksummedAddress(address);
        }
        return null;
    }

    @Override
    public String getWalletAddressFromAccountPUB(String accountPUB, String cryptoCurrency, int chainIndex, int index) {
        if (!accountPUB.startsWith("xpub")) {
            return null;
        }
        byte[] serializedKey = Base58.decodeChecked(accountPUB);
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        int standard = -1;

        boolean isPub = true;

        switch (header) {
            case XPUB:
                standard = STANDARD_BIP44;//xpub
                isPub = true;
                break;
            case XPRV:
                standard = STANDARD_BIP44;//xprv
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
                    return Address.fromKey(MainNetParams.get(), walletKey, Script.ScriptType.P2PKH).toString();
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
        }
        final int finalHeader = header;
        return DeterministicKey.deserializeB58(accountPUB, new NetworkParameters() {
            @Override
            public int getBip32HeaderP2PKHpub() {
                return finalHeader;
            }

            @Override
            public int getBip32HeaderP2PKHpriv() {
                return -1;
            }

            @Override
            public String getPaymentProtocolId() {
                return null;
            }

            @Override
            public void checkDifficultyTransitions(StoredBlock storedPrev, Block next, BlockStore blockStore) throws VerificationException {

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
        DeterministicKey masterKey = MasterPrivateKeyETH.getDeterministicKey(master.getPRV(),master.getStandard());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());

        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(master.getStandard(), true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));
        final DeterministicKey chainKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(chainIndex, false));
        final DeterministicKey walletKey = HDKeyDerivation.deriveChildKey(chainKey, new ChildNumber(index, false));

        return walletKey.getPrivateKeyAsHex();
    }

    @Override
    public String getAccountPUB(IMasterPrivateKey master, String cryptoCurrency, int accountIndex) {
        DeterministicKey masterKey = MasterPrivateKeyETH.getDeterministicKey(master.getPRV(),master.getStandard());
        return MasterPrivateKeyETH.serializePUB(masterKey,master.getStandard(), accountIndex, cryptoCurrency);
    }


    @Override
    public String getWalletAddressFromPrivateKey(String privateKey, String cryptoCurrency) {
        return null; //not supported yet
    }

    @Override
    public ISignature sign(String privateKey, byte[] hashToSign, String cryptoCurrency) {
        return null;//not supported yet
    }


    public boolean isAddressValid(String address, String cryptoCurrency) {
        if (address == null) {
            return false;
        }else{
            if (!address.startsWith("0x") && !address.startsWith("XE")){
                return false;
            }
        }

        return isAddressValidInternal(address);
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

        if (input.startsWith("0x")) {
            //most likely address lets check it
            try {
                if (isAddressValidInternal(input)) {
                    return new Classification(Classification.TYPE_ADDRESS,IClient.ETH,input);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }else if (input.startsWith("XE")) {
            try {
                if (isAddressValidInternal(input)) {
                    return new Classification(Classification.TYPE_ADDRESS,IClient.ETH,input);
                }
            } catch (AddressFormatException e) {
                e.printStackTrace();
            }
        }else if (input.startsWith("xpub")) {
            return new Classification(Classification.TYPE_PUB,IClient.ETH,input);
        }

        return new Classification(Classification.TYPE_UNKNOWN);

    }

    private static boolean isAddressValidInternal(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        address = address.trim();

        byte[] addrBytes = decodeAddressAsBytes(address);
        if (addrBytes != null) {
            if (address.equals(address.toLowerCase()) || address.equals(address.toUpperCase())) {
                //address doesn't contain checksum
                return true;
            }else{
                //if address contains checksum, we should check that too
                final String encodedAddress = encodeAddressToChecksummedAddress(addrBytes);
                if (address.equals(encodedAddress)){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> supportedCryptoCurrencies() {
        final HashSet<String> result = new HashSet<String>();
        result.add(IClient.ETH);
        result.add(IClient.ETC);
        return result;
    }


    private static byte[] decodeAddressAsBytes(String address) {
        if (address == null) {
            return null;
        }
        address = address.trim();

        if (address.startsWith("XE") || address.toLowerCase().startsWith("iban:")) {
            address = decodeAddressFromIBAN(address);
            if (address == null) {
                return null;
            }
        }

        if (address.toLowerCase().startsWith("0x")) {
            address = address.substring(2);
        }

        if (address.length() == 42) {
            //probably this format 0xf8b483DbA2c3B7176a3Da549ad41A48BB3121069
            if (address.toLowerCase().startsWith("0x")) {
                address = address.substring(2);
            }
        }

        if (address.length() == 40) {
            //probably this format f8b483DbA2c3B7176a3Da549ad41A48BB3121069
            if (isAllLowerCaseHex(address.toLowerCase())) {
                return hexStringToByteArray(address);
            }
        }
        return null;
    }

    public static byte[] sha3(byte[] message) {
        SHA3Digest digest = new SHA3Digest(256);

        byte[] hash = new byte[digest.getDigestSize()];
        if (message.length != 0) {
            digest.update(message, 0, message.length);
        }

        digest.doFinal(hash, 0);
        return hash;
    }

    public static String encodeAddressToIBAN(byte[] addrBytes) {
        BigInteger asBn = new BigInteger(1,addrBytes);
        String base36 = asBn.toString(36);
        String padded = padLeft(base36, 15);

        return fromBBan(padded.toUpperCase());
    }

    private static String decodeAddressFromIBAN(String iban) {
        if (iban == null || iban.isEmpty()) {
            return null;
        }
        if (iban.toLowerCase().startsWith("iban:")) {
            iban = iban.substring("iban:".length());
        }
        if ((iban.length() == 34 || iban.length() == 35) && iban.startsWith("XE")) {
            String base36 = iban.substring(4);
            BigInteger asBn = new BigInteger(base36, 36);
            return encodeAddressToChecksummedAddress(padLeft(asBn.toString(16), 20));
        }else{
            return null;
        }
    }

    static class CheckDigitException extends Exception {
        public CheckDigitException(String s) {
            super(s);
        }
    }

    private static String fromBBan(String bban) {
        String countryCode = "XE";
        try {
            final String checkDigit = calculate(countryCode + "00" + bban);
            return countryCode + checkDigit + bban;
        } catch (CheckDigitException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String calculate(String code) throws CheckDigitException {
        if (code != null && code.length() >= 5) {
            code = code.substring(0, 2) + "00" + code.substring(4);
            int modulusResult = calculateModulus(code);
            int charValue = 98 - modulusResult;
            String checkDigit = Integer.toString(charValue);
            return charValue > 9 ? checkDigit : "0" + checkDigit;
        } else {
            throw new CheckDigitException("Invalid Code length=" + (code == null ? 0 : code.length()));
        }
    }

    private static int calculateModulus(String code) throws CheckDigitException {
        String reformattedCode = code.substring(4) + code.substring(0, 4);
        long total = 0L;

        for(int i = 0; i < reformattedCode.length(); ++i) {
            int charValue = Character.getNumericValue(reformattedCode.charAt(i));
            if (charValue < 0 || charValue > 35) {
                throw new CheckDigitException("Invalid Character[" + i + "] = '" + charValue + "'");
            }

            total = (charValue > 9 ? total * 100L : total * 10L) + (long)charValue;
            if (total > 999999999L) {
                total %= 97L;
            }
        }

        return (int)(total % 97L);
    }


    private static String padLeft(String number, int bytes) {
        String result = number;
        while (result.length() < bytes * 2) {
            result = "00" + result;
        }
        return result;
    }

    private static String encodeAddressToChecksummedAddress(byte[] addrBytes) {
        String address = bytesToHexString(addrBytes);
        return encodeAddressToChecksummedAddress(address);
    }

    private static String encodeAddressToChecksummedAddress(String address) {
        if (address == null) {
            return null;
        }
        address = address.trim();
        if (address.isEmpty()) {
            return null;
        }
        Keccak256 sha3 = new Keccak256();
        sha3.update(address.toLowerCase().getBytes());
        final String addressHash = bytesToHexString(sha3.digest().array());

        String checksumAddress ="";

        final char[] addrChars = address.toCharArray();
        final char[] addrHashChars = addressHash.toCharArray();

        for (int i = 0; i < addrChars.length; i++ ) {
            // If ith character is 9 to f then make it uppercase
            if (Integer.parseInt((addrHashChars[i]+""), 16) > 7) {
                checksumAddress += (addrChars[i] +"").toUpperCase();
            } else {
                checksumAddress += (addrChars[i] +"").toLowerCase();
            }
        }
        return "0x"+checksumAddress;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static boolean isAllLowerCaseHex(String string) {
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (HEX_CHARS.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    public String generateWalletPrivateKeyWithPrefix(String prefix, String cryptoCurrency) {
        if (prefix == null) {
            prefix = "0x";
        }

        prefix = prefix.toLowerCase();

        ECKey ecKey = null;
        String testAddress = null;
        do {
            ecKey = new ECKey();
            testAddress = "0x" + getAddress(ecKey);
        }while (testAddress.indexOf(prefix) != 0);
        return bytesToHexString(ecKey.getPrivKeyBytes());
    }

    private String getAddress(ECKey ecKey) {
        Keccak256 hash = new Keccak256();
        hash.update(ecKey.getPubKeyPoint().getAffineXCoord().getEncoded());
        hash.update(ecKey.getPubKeyPoint().getAffineYCoord().getEncoded());
        byte[] result = hash.digestArray();
        byte[] address = new byte[20];
        System.arraycopy(result,result.length - address.length, address,0, address.length);
        return bytesToHexString(address);
    }

}
