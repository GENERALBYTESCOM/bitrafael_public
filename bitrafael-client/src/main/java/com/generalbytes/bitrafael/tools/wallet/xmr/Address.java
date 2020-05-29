package com.generalbytes.bitrafael.tools.wallet.xmr;

import com.generalbytes.bitrafael.tools.wallet.xmr.core.Utils;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Base58;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Keccak256;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.common.Varint;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.key.KeyFactory;
import com.generalbytes.bitrafael.tools.wallet.xmr.core.key.PublicKey;

import java.security.InvalidKeyException;
import java.util.Arrays;

public class Address {
    private PublicKey publicSpendKey;
    private PublicKey publicViewKey;
    private byte[] paymentId;
    private boolean isSubAddress;

    public Address(PublicKey publicSpendKey, PublicKey publicViewKey) {
        this.publicSpendKey = publicSpendKey;
        this.publicViewKey = publicViewKey;
    }

    public Address(PublicKey publicSpendKey, PublicKey publicViewKey, boolean isSubAddress) {
        this.publicSpendKey = publicSpendKey;
        this.publicViewKey = publicViewKey;
        this.isSubAddress = isSubAddress;
    }

    public Address(PublicKey publicSpendKey, PublicKey publicViewKey, byte[] paymentId) {
        this(publicSpendKey,publicViewKey);
        this.paymentId = paymentId;
    }

    public Address(PublicKey publicSpendKey, PublicKey publicViewKey, byte[] paymentId, boolean isSubAddress) {
        this.publicSpendKey = publicSpendKey;
        this.publicViewKey = publicViewKey;
        this.paymentId = paymentId;
        this.isSubAddress = isSubAddress;
    }

    public Address(PublicKey publicSpendKey, PublicKey publicViewKey, long paymentId) {
        this(publicSpendKey,publicViewKey);
        setPaymentId(paymentId);
    }


    public boolean isStandardAddress() {
        return paymentId == null;
    }

    public boolean isIntegratedAddress() {
        return paymentId != null;
    }


    public String toString() {
        byte[] prefix = Varint.valueOf(0x12).toByteArray();
        if (isSubAddress) {
            prefix = Varint.valueOf(0x2A).toByteArray();
        }

        if (isIntegratedAddress()) {
            prefix = Varint.valueOf(0x13).toByteArray();
        }
        byte[] contains = Utils.concat(new byte[][] { prefix, publicSpendKey.getEncoded(), publicViewKey.getEncoded()});
        if (isIntegratedAddress()) {
            contains = Utils.concat(new byte[][] { prefix, publicSpendKey.getEncoded(), publicViewKey.getEncoded(), paymentId});
        }
        final Keccak256 keccak = new Keccak256();
        keccak.reset();
        keccak.update(contains);
        byte[] checksum = Arrays.copyOfRange(keccak.digest().array(), 0, 4);

        byte[] addressBytes = Base58.encode(Utils.concat(new byte[][] { contains, checksum }));
        return new String(addressBytes);
    }

    public static Address parse(String address){
        try {
            byte[] decoded = Base58.decode(address.getBytes());
            byte[] prefix = Arrays.copyOfRange(decoded, 0, 1);
            boolean isStandard = prefix[0] == 0x12;
            boolean isIntegrated = prefix[0] == 0x13;
            boolean isSubAddress = prefix[0] == 0x2A;

            int prefixLen = 1;
            byte[] publicSpendKeyEncoded = null;
            byte[] publicViewKeyEncoded = null;
            byte[] checksum = null;
            byte[] paymentId = null;

            if (isStandard || isSubAddress) {
                publicSpendKeyEncoded = Arrays.copyOfRange(decoded, prefixLen + 0, prefixLen + 32);
                publicViewKeyEncoded = Arrays.copyOfRange(decoded, prefixLen + 32, prefixLen + 32 + 32);
                checksum = Arrays.copyOfRange(decoded, prefixLen + 32 + 32, prefixLen + 32 + 32 + 4);
            } else if (isIntegrated) {
                publicSpendKeyEncoded = Arrays.copyOfRange(decoded, prefixLen + 0, prefixLen + 32);
                publicViewKeyEncoded = Arrays.copyOfRange(decoded, prefixLen + 32, prefixLen + 32 + 32);
                paymentId = Arrays.copyOfRange(decoded, prefixLen + 32 + 32, prefixLen + 32 + 32 + 8);
                checksum = Arrays.copyOfRange(decoded, prefixLen + 32 + 32 + 8, prefixLen + 32 + 32 + 8 + 4);
            }

            byte[] contains = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
            final Keccak256 keccak = new Keccak256();
            keccak.reset();
            keccak.update(contains);
            byte[] checksum2 = Arrays.copyOfRange(keccak.digest().array(), 0, 4);
            if (!Arrays.equals(checksum, checksum2)) {
                return null;//checksum failed
            }


            try {
                KeyFactory kf = new KeyFactory();
                Address result = new Address(kf.decodePublicKey(publicSpendKeyEncoded), kf.decodePublicKey(publicViewKeyEncoded), paymentId, isSubAddress);
                return result;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
        return null;

    }

    public PublicKey getPublicSpendKey() {
        return publicSpendKey;
    }

    public PublicKey getPublicViewKey() {
        return publicViewKey;
    }

    public byte[] getPaymentId() {
        return paymentId;
    }

    public void setPublicSpendKey(PublicKey publicSpendKey) {
        this.publicSpendKey = publicSpendKey;
    }

    public void setPublicViewKey(PublicKey publicViewKey) {
        this.publicViewKey = publicViewKey;
    }

    public void setPaymentId(byte[] paymentId) {
        this.paymentId = paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = new byte[8];
        this.paymentId[7] = (byte) (paymentId & 0xFFL);
        this.paymentId[6] = (byte) ((paymentId >> 8)  & 0xFFL);
        this.paymentId[5] = (byte) ((paymentId >> 16) & 0xFFL);
        this.paymentId[4] = (byte) ((paymentId >> 24) & 0xFFL);

        this.paymentId[3] = (byte) ((paymentId >> 32) & 0xFFL);
        this.paymentId[2] = (byte) ((paymentId >> 40) & 0xFFL);
        this.paymentId[1] = (byte) ((paymentId >> 48) & 0xFFL);
        this.paymentId[0] = (byte) ((paymentId >> 56) & 0xFFL);
    }

    public long getPaymentIdAsLong() {
        if (paymentId == null) {
            return 0;
        }
        return    (byteToLong(paymentId[0]) & 0xFFL) << 56
                | (byteToLong(paymentId[1]) & 0xFFL) << 48
                | (byteToLong(paymentId[2]) & 0xFFL) << 40
                | (byteToLong(paymentId[3]) & 0xFFL) << 32

                | (byteToLong(paymentId[4]) & 0xFFL) << 24
                | (byteToLong(paymentId[5]) & 0xFFL) << 16
                | (byteToLong(paymentId[6]) & 0xFFL) << 8
                |  byteToLong(paymentId[7]) & 0xFFL;
    }

    private static long byteToLong(byte v) {
        if (v < 0) {
            return 256+v;
        }else{
            return v;
        }

    }



}
