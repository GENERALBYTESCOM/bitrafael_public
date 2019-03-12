package com.generalbytes.bitrafael.api.wallet.smart;

import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import org.smartcashj.crypto.DeterministicKey;
import org.smartcashj.crypto.MnemonicCode;
import org.smartcashj.params.MainNetParams;


public class MasterPrivateKeySMART implements IMasterPrivateKey {
    private DeterministicKey key;
    private int standard;

    public MasterPrivateKeySMART(String xprv, int standard) {
        key = DeterministicKey.deserializeB58(xprv, MainNetParams.get());
        this.standard = standard;
    }

    public long getCreationTimeSeconds() {
        return MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    }

    @Override
    public String toString() {
        return getPUB();
    }

    public String getPRV() {
        return key.serializePrivB58(MainNetParams.get());
    }
    public String getPUB() {
        return key.serializePubB58(MainNetParams.get());
    }

    public boolean hasPrv() {
        return key.hasPrivKey();
    }

    @Override
    public int getStandard() {
        return standard;
    }
}
