package com.generalbytes.bitrafael.api.wallet.xmr;

import com.generalbytes.bitrafael.api.wallet.xmr.core.Utils;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PrivateKey;
import com.generalbytes.bitrafael.api.wallet.xmr.core.key.PublicKey;

public class Account{
    private String seedInMnemonic;
    private byte[] seedInBytes;
    private PrivateKey privateSpendKey;
    private PrivateKey privateViewKey;
    private PublicKey publicSpendKey;
    private PublicKey publicViewKey;
    private Address address;
    private int index;

    public Account(String seedInMnemonic, byte[] seedInBytes, PrivateKey privateSpendKey, PrivateKey privateViewKey, PublicKey publicSpendKey, PublicKey publicViewKey, int index) {
        this.seedInMnemonic = seedInMnemonic;
        this.seedInBytes = seedInBytes;
        this.privateSpendKey = privateSpendKey;
        this.privateViewKey = privateViewKey;
        this.publicSpendKey = publicSpendKey;
        this.publicViewKey = publicViewKey;
        this.index = index;

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

    public PrivateKey getPrivateViewKey() {
        return privateViewKey;
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
                ", privateViewKey=" + privateViewKey +
                ", publicSpendKey=" + publicSpendKey +
                ", publicViewKey=" + publicViewKey +
                ", index=" + index +
                ", address='" + address + '\'' +
                '}';
    }
}
