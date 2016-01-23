package com.generalbytes.bitrafael.api.wallet;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;

/**
 * Created by b00lean on 23.1.16.
 */
public class MasterPrivateKey {
    private DeterministicKey key;

    public MasterPrivateKey(String xprv) {
        key = DeterministicKey.deserializeB58(xprv,MainNetParams.get());
    }

    public byte[] getSeedBytes() {
        return key.getSecretBytes();
    }

    public long getCreationTimeSeconds() {
        return MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    }

    @Override
    public String toString() {
        return key.serializePubB58(MainNetParams.get());
    }
}
