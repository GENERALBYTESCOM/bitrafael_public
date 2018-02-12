package com.generalbytes.colorledger.example;

import com.generalbytes.colorledger.*;

import java.io.File;

public class ColorExample {

    public static void main(String[] args) {
        final String color = "CAFE";
        Key miner = new Key("KyrsDtNbgYwh7CFkgoLEpkoMdkeUrdif7GvmwSiYcJXwpstbBcEa");//18Nf3yXaxkWi5jdRv22e4MbjNmeneUb4d3
        Key emitter = new Key("KyktRUebZXrBS3cUPwWULo561M6qMZDxZNjvU4tBUHnPVDgpNmiV");//1Hi8ha843GpHSGFgVWRRyRgY8mh3QbvxV3
        Key alice = new Key("KwWgWtBsE1ZRwjAmMP7Ppaih9UzoqpchgLGKYyg6z4GPEt2qQd5Y");//1FmZANKJLb9ukcu5ET7KzQiu6rKFE7Xwgj
        Key bob = new Key("KywXnYbGKFYBuqqtkGfcKEYpbG4123rsGDveh6XekVVvG5h2kBoj");//1D6uTG5xCmZ1jije5X84tqzEqcMstGpi4S


        IColorLedger ledger = new ColorLedger(new ColorLedgerStorage(new File("./colordata")),miner);
        ledger.addEmitter(color,emitter.getAddress());


        boolean result = ledger.transferCoins(emitter.createTransaction(alice.getAddress(), color, 1));
        System.out.println("emit result = " + result);
        result = ledger.transferCoins(alice.createTransaction(bob.getAddress(), color, 1));
        System.out.println("transfer result = " + result);


        System.out.println("emit = " + ledger.getBalance(emitter.getAddress(),color));
        System.out.println("alice = " + ledger.getBalance(alice.getAddress(),color));
        System.out.println("bob = " + ledger.getBalance(bob.getAddress(),color));
        ledger.removeEmitter(color,emitter.getAddress());

        final Page newPage = ledger.createNewPage(miner, System.currentTimeMillis());
        System.out.println("page = " + newPage);

    }

}
