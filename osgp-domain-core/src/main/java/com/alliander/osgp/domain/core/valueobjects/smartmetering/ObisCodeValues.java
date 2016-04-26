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
    
    public ObisCodeValues(byte a, byte b, byte c, byte d, byte e, byte f) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public byte getA() {
        return a;
    }

    public byte getB() {
        return b;
    }

    public byte getC() {
        return c;
    }

    public byte getD() {
        return d;
    }

    public byte getE() {
        return e;
    }

    public byte getF() {
        return f;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + a;
        result = prime * result + b;
        result = prime * result + c;
        result = prime * result + d;
        result = prime * result + e;
        result = prime * result + f;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ObisCodeValues other = (ObisCodeValues) obj;
        if (a != other.a)
            return false;
        if (b != other.b)
            return false;
        if (c != other.c)
            return false;
        if (d != other.d)
            return false;
        if (e != other.e)
            return false;
        if (f != other.f)
            return false;
        return true;
    }
}
