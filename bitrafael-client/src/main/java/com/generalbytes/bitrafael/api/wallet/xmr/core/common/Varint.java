/*************************************************************************************
 * Copyright (C) 2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.bitrafael.api.wallet.xmr.core.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class Varint
{
    private final long value;
    public static final Varint ZERO = new Varint(0L);
    public static final Varint MAX_VALUE = new Varint(Long.MAX_VALUE);

    public Varint(long value)
    {
        if (value < 0L) {
            throw new IllegalArgumentException("[varint] negative number initialization: " + value);
        }
        this.value = value;
    }

    public static Varint valueOf(long longValue)
    {
        return new Varint(longValue);
    }

    public Varint increment()
    {
        return add(1L);
    }

    public Varint add(long add)
    {
        return valueOf(this.value + add);
    }

    public long getValue()
    {
        return this.value;
    }

    public int getIntValue()
    {
        int intValue = (int)this.value;
        if (intValue != this.value) {
            throw new IllegalStateException(toString() + " cannot be transformed to integer type without information loss.");
        }
        return intValue;
    }

    public static Varint read(InputStream is)
            throws IOException
    {
        long result = 0L;
        for (int off = 0;; off += 7)
        {
            if (off > 57) {
                throw new IOException("[varint] value overflow.");
            }
            int b = is.read();
            if (b == -1) {
                throw new IOException("Cannot read [varint]. Premature end of input stream.");
            }
            result |= (b & 0x7F) << off;
            if ((b & 0x80) == 0)
            {
                if ((b != 0) || (off == 0)) {
                    break;
                }
                throw new IOException("Invalid [varint] encoding.");
            }
        }
        return new Varint(result);
    }

    public void writeTo(OutputStream out)
            throws IOException
    {
        if (this.value == 0L)
        {
            out.write(0);
            return;
        }
        byte[] buf = new byte[9];
        int off = 0;
        for (int i = 0; off < 57; i++)
        {
            byte b = (byte)(int)(0x7F & this.value >>> off);
            buf[i] = b;off += 7;
        }
        int last = buf.length - 1;
        for (int j = buf.length - 1; j >= 0; j--)
        {
            if (buf[j] != 0) {
                break;
            }
            last--;
        }
        for (int i = 0; i < last; i++) {
            buf[i] = ((byte)(buf[i] | 0x80));
        }
        out.write(buf, 0, last + 1);
    }

    public String toString()
    {
        return "Varint{value=" + this.value + '}';
    }

    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Varint varint = (Varint)o;

        return this.value == varint.value;
    }

    public int hashCode()
    {
        return (int)(this.value ^ this.value >>> 32);
    }

    public byte[] toByteArray()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            writeTo(out);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot happen.");
        }
        return out.toByteArray();
    }
}
