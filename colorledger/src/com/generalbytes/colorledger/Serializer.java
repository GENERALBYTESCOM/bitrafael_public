package com.generalbytes.colorledger;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by b00lean on 18.11.16.
 */
public class Serializer {

    public byte[] serializeTransaction(Transaction transaction) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(bos);
        try {
            ho.writeObject(transaction);
            ho.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public Transaction deserializeTransaction(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        Hessian2Input input = new Hessian2Input(bis);
        Object o = null;
        try {
            o = input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Transaction)o;
    }

}
