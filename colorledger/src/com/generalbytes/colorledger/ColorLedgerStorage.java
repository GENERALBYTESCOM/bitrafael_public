/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.colorledger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ColorLedgerStorage implements IColorLedgerStorage {
    private File dir;
    private File txDir;
    private File colorsDir;
    private Serializer serializer = new Serializer();

    public ColorLedgerStorage(File dir) {
        this.dir = dir;
        this.txDir = new File(dir,"tx");
        this.colorsDir = new File(dir,"colors");
    }

    @Override
    public boolean addEmitter(String color, String address) {
        final File colorDir = new File(colorsDir, color);
        if (!colorDir.exists()) {
            colorDir.mkdirs();
        }
        try {
            (new File(colorDir,address)).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removeEmitter(String color, String address) {
        final File colorDir = new File(colorsDir, color);
        if (colorDir.exists()) {
            final File colorFile = new File(colorDir, address);
            if (colorFile.exists()) {
                return colorFile.delete();
            }
        }
        return false;
    }

    @Override
    public List<String> getEmitters(String color) {
        List<String> result = new ArrayList<String>();
        final File colorDir = new File(colorsDir, color);
        if (colorDir.exists()) {
            final String[] list = colorDir.list();
            for (int i = 0; i < list.length; i++) {
                String emitter = list[i];
                result.add(emitter);
            }
        }
        return result;
    }

    @Override
    public boolean addTransaction(Transaction tx) {
        if (!txDir.exists()){
            txDir.mkdirs();
        }

        final String hash = tx.getHash();
        File txFile = new File(txDir, hash);
        writeFile(txFile,serializer.serializeTransaction(tx));

        File fromDir = new File(dir,tx.getFromAddress());
        if (!fromDir.exists()) {
            fromDir.mkdirs();
        }
        File fromFile = new File(fromDir, hash);
        try {
            fromFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File toDir = new File(dir,tx.getToAddress());
        if (!toDir.exists()) {
            toDir.mkdirs();
        }
        File toFile = new File(toDir, hash);
        try {
            toFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Transaction getTransaction(String txHash) {
        File txFile = new File(txDir, txHash);
        if (txFile.exists()) {
            return serializer.deserializeTransaction(readFile(txFile.getAbsolutePath()));
        }
        return null;
    }

    @Override
    public boolean hasTransaction(String txHash) {
        return new File(txDir,txHash).exists();
    }

    @Override
    public List<Transaction> getTransactions(final String address, String coinColor) {
        List<Transaction> result = new ArrayList<Transaction>();
        File ad = new File(dir,address);
        if (ad.exists()) {
            final String[] list = ad.list();
            for (int i = 0; i < list.length; i++) {
                String txHash = list[i];
                final Transaction d = getTransaction(txHash);
                if (d.getCoinColor().equals(coinColor)) {
                    result.add(d);
                }
            }
        }
        return result;
    }

    private static byte[] readFile(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeFile(File file, byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
