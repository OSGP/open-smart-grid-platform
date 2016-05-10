/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ObisCodeValues implements Serializable {

    private static final long serialVersionUID = 7600691210538237856L;

    private byte a;
    private byte b;
    private byte c;
    private byte d;
    private byte e;
    private byte f;

    public ObisCodeValues(final byte a, final byte b, final byte c, final byte d, final byte e, final byte f) {
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ObisCodeValues other = (ObisCodeValues) obj;
        if (this.a != other.a) {
            return false;
        }
        if (this.b != other.b) {
            return false;
        }
        if (this.c != other.c) {
            return false;
        }
        if (this.d != other.d) {
            return false;
        }
        if (this.e != other.e) {
            return false;
        }
        if (this.f != other.f) {
            return false;
        }
        return true;
    }
}
