package com.generalbytes.bitrafael.api.wallet.xmr;

import com.generalbytes.bitrafael.api.wallet.xmr.core.Utils;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PrivateKey;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PublicKey;

public class Account{
    private String seedInMnemonic;
    private byte[] seedInBytes;
    private PrivateKey privateSpendKey;
    private PrivateKey privateVidewKey;
    private PublicKey publicSpendKey;
    private PublicKey publicViewKey;
    private Address address;

    public Account(String seedInMnemonic, byte[] seedInBytes, PrivateKey privateSpendKey, PrivateKey privateVidewKey, PublicKey publicSpendKey, PublicKey publicViewKey) {
        this.seedInMnemonic = seedInMnemonic;
        this.seedInBytes = seedInBytes;
        this.privateSpendKey = privateSpendKey;
        this.privateVidewKey = privateVidewKey;
        this.publicSpendKey = publicSpendKey;
        this.publicViewKey = publicViewKey;

        address = new Address(publicSpendKey,publicViewKey);
    }

    public String getSeedInMnemonic() {
        return seedInMnemonic;
    }

    public byte[] getSeedInBytes() {
        return seedInBytes;
    }

    public PrivateKey getPrivateSpendKey() {
        return privateSpendKey;
    }

    public PrivateKey getPrivateVidewKey() {
        return privateVidewKey;
    }

    public PublicKey getPublicSpendKey() {
        return publicSpendKey;
    }

    public PublicKey getPublicViewKey() {
        return publicViewKey;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Account{" +
                "seedInMnemonic='" + seedInMnemonic + '\'' +
                ", seedInBytes=" + Utils.bytesToHex(seedInBytes)+
                ", privateSpendKey=" + privateSpendKey +
                ", privateVidewKey=" + privateVidewKey +
                ", publicSpendKey=" + publicSpendKey +
                ", publicViewKey=" + publicViewKey +
                ", address='" + address + '\'' +
                '}';
    }
}
