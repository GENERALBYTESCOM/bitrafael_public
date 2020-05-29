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

package com.generalbytes.bitrafael.tools.wallet.btc;

import com.generalbytes.bitrafael.client.api.IClient;
import com.generalbytes.bitrafael.tools.api.wallet.IMasterPrivateKey;
import com.generalbytes.bitrafael.tools.api.wallet.IWalletTools;
import com.generalbytes.bitrafael.tools.wallet.WalletTools;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.MonetaryFormat;

import java.nio.ByteBuffer;

import static com.google.common.base.Preconditions.checkState;

public class MasterPrivateKeyBTC implements IMasterPrivateKey {
    public static final int XPUB = 0x0488b21e;
    public static final int XPRV = 0x0488ade4;
    public static final int YPUB = 0x049d7cb2;
    public static final int YPRV = 0x049d7878;
    public static final int ZPUB = 0x04b24746;
    public static final int ZPRV = 0x04b2430c;

    private DeterministicKey key;
    private int standard;

    public MasterPrivateKeyBTC(String prv, int standard) {
        key = getDeterministicKey(prv, standard);
        this.standard = standard;
    }

    public static DeterministicKey getDeterministicKey(String prv, int standard) {
        int header = 0;
        switch (standard) {
            case IWalletTools.STANDARD_BIP44:
                header = XPRV;//xprv
                break;
            case IWalletTools.STANDARD_BIP49:
                header = YPRV; //yprv
                break;
            case IWalletTools.STANDARD_BIP84:
                header = ZPRV; //zprv
                break;
        }
        final int finalHeader = header;
        return DeterministicKey.deserializeB58(prv, new NetworkParameters() {
            @Override
            public int getBip32HeaderP2PKHpub() {
                return 0;
            }

            @Override
            public int getBip32HeaderP2PKHpriv() {
                return finalHeader;
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

    public long getCreationTimeSeconds() {
        return MnemonicCode.BIP39_STANDARDISATION_TIME_SECS;
    }

    @Override
    public String toString() {
        return getPUB();
    }

    public String getPRV() {
        return serializePRV(key,standard);
    }

    public String getPUB() {
        return serializePUB(key,standard,0, IClient.BTC);
    }

    public boolean hasPrv() {
        return key.hasPrivKey();
    }

    @Override
    public int getStandard() {
        return standard;
    }

    public static String serializePRV(DeterministicKey masterKey, int standard) {
        int header = XPRV;

        switch (standard) {
            case IWalletTools.STANDARD_BIP44:
                header = XPRV;//xprv
                break;
            case IWalletTools.STANDARD_BIP49:
                header = YPRV; //yprv
                break;
            case IWalletTools.STANDARD_BIP84:
                header = ZPRV; //zprv
                break;
        }
        ByteBuffer ser = ByteBuffer.allocate(78);
        ser.putInt(header);
        ser.put((byte) masterKey.getDepth());
        ser.putInt(masterKey.getParentFingerprint());
        ser.putInt(masterKey.getChildNumber().i());
        ser.put(masterKey.getChainCode());
        ser.put(masterKey.getPrivKeyBytes33());
        checkState(ser.position() == 78);
        byte[] x = ser.array();
        return Base58.encode(addChecksum(x));
    }


    public static String serializePUB(DeterministicKey masterKey, int standard, int accountIndex, String cryptoCurrency) {
        final DeterministicKey purposeKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(standard, true));
        final DeterministicKey coinKey = HDKeyDerivation.deriveChildKey(purposeKey, new ChildNumber(WalletTools.getCoinTypeByCryptoCurrency(cryptoCurrency), true));
        final DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(coinKey, new ChildNumber(accountIndex, true));

        int header = 0x0488b21e;
        switch (standard) {
            case IWalletTools.STANDARD_BIP44:
                header= XPUB;//xpub
                break;
            case IWalletTools.STANDARD_BIP49:
                header = YPUB; //ypub
                break;
            case IWalletTools.STANDARD_BIP84:
                header = ZPUB; //zpub
                break;
        }

        ByteBuffer ser = ByteBuffer.allocate(78);
        ser.putInt(header);
        ser.put((byte) accountKey.getDepth());
        ser.putInt(accountKey.getParentFingerprint());
        ser.putInt(accountKey.getChildNumber().i());
        ser.put(accountKey.getChainCode());
        ser.put(accountKey.getPubKey());
        checkState(ser.position() == 78);
        return Base58.encode(addChecksum(ser.array()));
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
