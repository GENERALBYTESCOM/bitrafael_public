package com.generalbytes.bitrafael.api.wallet;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by b00lean on 23.1.16.
 */
public class BitRafaelWalletTools implements IWalletTools{

    public MasterPrivateKey getMasterPrivateKey(String seedMnemonicSeparatedBySpaces, String password){
        List<String> split = ImmutableList.copyOf(Splitter.on(" ").omitEmptyStrings().split(seedMnemonicSeparatedBySpaces));
        DeterministicSeed seed = new DeterministicSeed(split,null,password, MnemonicCode.BIP39_STANDARDISATION_TIME_SECS);
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        final String xprv = masterKey.serializePrivB58(MainNetParams.get());
        return new MasterPrivateKey(xprv);
    }

    @Override
    public String getWalletAddress(MasterPrivateKey master, int subsystemId, int accountId, int purposeId, int childId) {
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(master.getSeedBytes());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
        DeterministicKey subsystemKey = hierarchy.get(ImmutableList.of(new ChildNumber(subsystemId)), false, true);
        DeterministicKey accountKey = hierarchy.get(HDUtils.append(subsystemKey.getPath(), new ChildNumber(accountId, false)), false, true);
        DeterministicKey purposeKey = hierarchy.get(HDUtils.append(accountKey.getPath(), new ChildNumber(purposeId, false)), false, true);
        DeterministicKey walletKey = hierarchy.get(HDUtils.append(purposeKey.getPath(), new ChildNumber(childId, false)), false, true);
        return new Address(MainNetParams.get(), walletKey.getPubKeyHash()).toBase58();
    }

    @Override
    public String getAccountXPUB(MasterPrivateKey master, int subsystemId, int accountId) {
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(master.getSeedBytes());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
        DeterministicKey subsystemKey = hierarchy.get(ImmutableList.of(new ChildNumber(subsystemId)), false, true);
        DeterministicKey accountKey = hierarchy.get(HDUtils.append(subsystemKey.getPath(), new ChildNumber(accountId, false)), false, true);
        return accountKey.serializePubB58(MainNetParams.get());
    }


    @Override
    public String getWalletPrivateKey(MasterPrivateKey master, int subsystemId, int accountId, int purposeId, int childId) {
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(master.getSeedBytes());
        masterKey.setCreationTimeSeconds(master.getCreationTimeSeconds());
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(masterKey);
        DeterministicKey subsystemKey = hierarchy.get(ImmutableList.of(new ChildNumber(subsystemId)), false, true);
        DeterministicKey accountKey = hierarchy.get(HDUtils.append(subsystemKey.getPath(), new ChildNumber(accountId, false)), false, true);
        DeterministicKey purposeKey = hierarchy.get(HDUtils.append(accountKey.getPath(), new ChildNumber(purposeId, false)), false, true);
        DeterministicKey walletKey = hierarchy.get(HDUtils.append(purposeKey.getPath(), new ChildNumber(childId, false)), false, true);
        return walletKey.getPrivateKeyAsWiF(MainNetParams.get());
    }

    @Override
    public String getAddressFromPrivateKey(String privateKey) {
        DumpedPrivateKey dp = new DumpedPrivateKey(MainNetParams.get(),privateKey);
        return (new Address(MainNetParams.get(),dp.getKey().getPubKeyHash())).toBase58();
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
}
