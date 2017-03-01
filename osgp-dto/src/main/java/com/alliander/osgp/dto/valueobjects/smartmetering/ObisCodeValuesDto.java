/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ObisCodeValuesDto implements Serializable {

    private static final long serialVersionUID = 6725616781596815362L;

    private final byte a;
    private final byte b;
    private final byte c;
    private final byte d;
    private final byte e;
    private final byte f;

    public ObisCodeValuesDto(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
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
        final int prime = 31;
        int result = 1;
        result = prime * result + this.a;
        result = prime * result + this.b;
        result = prime * result + this.c;
        result = prime * result + this.d;
        result = prime * result + this.e;
        result = prime * result + this.f;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final ObisCodeValuesDto other = (ObisCodeValuesDto) obj;
        if (this.a != other.a)
            return false;
        if (this.b != other.b)
            return false;
        if (this.c != other.c)
            return false;
        if (this.d != other.d)
            return false;
        if (this.e != other.e)
            return false;
        if (this.f != other.f)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d.%d.%d", this.a, this.b, this.c, this.d, this.e, this.f);
    }
}
