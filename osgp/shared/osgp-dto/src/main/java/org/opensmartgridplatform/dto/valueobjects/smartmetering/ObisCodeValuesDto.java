/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class ObisCodeValuesDto implements Serializable {

    private static final long serialVersionUID = 6725616781596815362L;

    private final byte a;
    private final byte b;
    private final byte c;
    private final byte d;
    private final byte e;
    private final byte f;

    public ObisCodeValuesDto(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public ObisCodeValuesDto(String obisCode) {

        try {
            int[] values = new int[6];

            int i = 0;
            for (String s : obisCode.split("\\.")) {
                values[i++] = Integer.parseInt(s);
            }

            this.a = (byte) values[0];
            this.b = (byte) values[1];
            this.c = (byte) values[2];
            this.d = (byte) values[3];
            this.e = (byte) values[4];
            this.f = (byte) values[5];

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse String to ObisCode", ex);
        }
    }

    public byte getA() {
        return this.a;
    }

    public byte getB() {
        return this.b;
    }

    public byte getC() {
        return this.c;
    }

    public byte getD() {
        return this.d;
    }

    public byte getE() {
        return this.e;
    }

    public byte getF() {
        return this.f;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.a, this.b, this.c, this.d, this.e, this.f);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ObisCodeValuesDto)) {
            return false;
        }
        final ObisCodeValuesDto other = (ObisCodeValuesDto) obj;
        return this.a == other.a && this.b == other.b && this.c == other.c && this.d == other.d && this.e == other.e
                && this.f == other.f;
    }

    @Override
    public String toString() {
        return String
                .format("%d.%d.%d.%d.%d.%d", this.a & 0xFF, this.b & 0xFF, this.c & 0xFF, this.d & 0xFF, this.e & 0xFF,
                        this.f & 0xFF);
    }

    public byte[] toByteArray() {
        return new byte[] { this.a, this.b, this.c, this.d, this.e, this.f };
    }
}
