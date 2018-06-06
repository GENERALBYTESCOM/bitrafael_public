package com.generalbytes.bitrafael.api.wallet.bch;

import com.generalbytes.bitrafael.api.wallet.IMasterPrivateKey;
import org.bitcoincashj.crypto.DeterministicKey;
import org.bitcoincashj.crypto.MnemonicCode;

public class MasterPrivateKeyBCH implements IMasterPrivateKey {
	private DeterministicKey key;

	public MasterPrivateKeyBCH() {

	}

    public MasterPrivateKeyBCH(String xprv) {

    }

    @Override
	public long getCreationTimeSeconds() {
        return MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    }

	@Override
	public String getXPRV() {
		//Not implemented
    	return null;
    }

	@Override
	public String getXPUB() {
		//Not implemented
		return null;
	}

	@Override
	public boolean hasPrv() {
		return key.hasPrivKey();
	}
	
	@Override
    public String toString() {
        return String.format("MasterPrivateKeyBCH");
    }

}
