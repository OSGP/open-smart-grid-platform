/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Arrays;

public class CosemObisCode implements Serializable {

    private static final long serialVersionUID = -4096303352316355492L;

    private final int a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;
    private final int f;

    public CosemObisCode(final int[] code) {
        if (code.length != 6) {
            throw new IllegalArgumentException("ObisCode must have 6 values: " + code.length);
        }
        this.a = code[0];
        this.b = code[1];
        this.c = code[2];
        this.d = code[3];
        this.e = code[4];
        this.f = code[5];
        this.checkValues();
    }

    public CosemObisCode(final byte[] code) {
        if (code.length != 6) {
            throw new IllegalArgumentException("ObisCode must have 6 values: " + code.length);
        }
        this.a = code[0] & 0xFF;
        this.b = code[1] & 0xFF;
        this.c = code[2] & 0xFF;
        this.d = code[3] & 0xFF;
        this.e = code[4] & 0xFF;
        this.f = code[5] & 0xFF;
    }

    public CosemObisCode(final String code) {
        this(parseCode(code));
    }

    private void checkValues() {
        this.checkValue("a", this.a);
        this.checkValue("b", this.b);
        this.checkValue("c", this.c);
        this.checkValue("d", this.d);
        this.checkValue("e", this.e);
        this.checkValue("f", this.f);
    }

    private void checkValue(final String letter, final int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(letter + " not in [0..0xFF]");
        }
    }

    private static int[] parseCode(final String code) {
        final String[] parts = code.split("\\.|:|-");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Unable to parse code into 6 integer parts: " + Arrays.toString(parts));
        }
        final int[] values = new int[6];
        for (int i = 0; i < 6; i++) {
            try {
                values[i] = Integer.parseInt(parts[i]);
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Unable to parse code into 6 integer parts: " + Arrays.toString(parts), e);
            }
        }
        return values;
    }

    public int getA() {
        return this.a;
    }

    public int getB() {
        return this.b;
    }

    public int getC() {
        return this.c;
    }

    public int getD() {
        return this.d;
    }

    public int getE() {
        return this.e;
    }

    public int getF() {
        return this.f;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d.%d.%d", this.a, this.b, this.c, this.d, this.e, this.f);
    }

    public String toDsmrString() {
        return String.format("%d-%d:%d.%d.%d.%d", this.a, this.b, this.c, this.d, this.e, this.f);
    }

    public String toHexString() {
        return String.format("%02X%02X%02X%02X%02X%02X", this.a, this.b, this.c, this.d, this.e, this.f);
    }

    public byte[] toByteArray() {
        return new byte[] { (byte) this.a, (byte) this.b, (byte) this.c, (byte) this.d, (byte) this.e, (byte) this.f };
    }

    public int[] toIntArray() {
        return new int[] { this.a, this.b, this.c, this.d, this.e, this.f };
    }
}
