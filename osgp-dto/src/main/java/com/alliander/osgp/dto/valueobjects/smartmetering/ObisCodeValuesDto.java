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

    private short a;
    private short b;
    private short c;
    private short d;
    private short e;
    private short f;

    public ObisCodeValuesDto(final short a, final short b, final short c, final short d, final short e, final short f) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public short getA() {
        return this.a;
    }

    public short getB() {
        return this.b;
    }

    public short getC() {
        return this.c;
    }

    public short getD() {
        return this.d;
    }

    public short getE() {
        return this.e;
    }

    public short getF() {
        return this.f;
    }
}
