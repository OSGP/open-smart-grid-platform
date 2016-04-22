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

    private byte a;
    private byte b;
    private byte c;
    private byte d;
    private byte e;
    private byte f;
    
    public ObisCodeValuesDto(byte a, byte b, byte c, byte d, byte e, byte f) {
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
}
