package com.generalbytes.bitrafael.api.wallet.bch;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Bech32Test {
    @Test
    public void test() {
        assertEquals("11DstTXVTvo8DfczCQws6CHV718u74VV2",
                LegacyAddress.fromPubKeyHash(MainNetParams.get(), Bech32.decodeCashAddress("qqqq4077ywes2cmda80crheea4m7zznszvk5lqedw9")).toString());
        assertEquals("1P3GQYtcWgZHrrJhUa4ctoQ3QoCU2F65nz",
                LegacyAddress.fromPubKeyHash(MainNetParams.get(), Bech32.decodeCashAddress("bitcoincash:qrcuqadqrzp2uztjl9wn5sthepkg22majyxw4gmv6p")).toString());
        assertEquals("17WXKozdmX7zfjPRLZtJz5Avth4E3kq3xn",
                LegacyAddress.fromPubKeyHash(MainNetParams.get(), Bech32.decodeCashAddress("qprkvgvvlvn856mkhuuh0kxtu7ngnu2sr5xhsvwye4")).toString());
    }
}