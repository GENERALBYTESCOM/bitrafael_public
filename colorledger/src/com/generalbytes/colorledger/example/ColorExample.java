package com.generalbytes.colorledger.example;

import com.generalbytes.colorledger.ColorLedger;
import com.generalbytes.colorledger.ColorLedgerStorage;
import com.generalbytes.colorledger.ColorWallet;
import com.generalbytes.colorledger.IColorLedger;

import java.io.File;

public class ColorExample {

    public static void main(String[] args) {
        final String color = "CAFE";
        ColorWallet emitter = new ColorWallet("KyktRUebZXrBS3cUPwWULo561M6qMZDxZNjvU4tBUHnPVDgpNmiV");//1Hi8ha843GpHSGFgVWRRyRgY8mh3QbvxV3
        ColorWallet alice = new ColorWallet("KwWgWtBsE1ZRwjAmMP7Ppaih9UzoqpchgLGKYyg6z4GPEt2qQd5Y");//1FmZANKJLb9ukcu5ET7KzQiu6rKFE7Xwgj
        ColorWallet bob = new ColorWallet("KywXnYbGKFYBuqqtkGfcKEYpbG4123rsGDveh6XekVVvG5h2kBoj");//1D6uTG5xCmZ1jije5X84tqzEqcMstGpi4S


        IColorLedger ledger = new ColorLedger(new ColorLedgerStorage(new File("./colordata")));
        ledger.addEmitter(color,emitter.getAddress());


        boolean result = ledger.transferCoins(emitter.createTransaction(alice.getAddress(), color, 1));
        System.out.println("emit result = " + result);
        result = ledger.transferCoins(alice.createTransaction(bob.getAddress(), color, 1));
        System.out.println("transfer result = " + result);


        System.out.println("emit = " + ledger.getBalance(emitter.getAddress(),color));
        System.out.println("alice = " + ledger.getBalance(alice.getAddress(),color));
        System.out.println("bob = " + ledger.getBalance(bob.getAddress(),color));
        ledger.removeEmitter(color,emitter.getAddress());


    }

//

}
