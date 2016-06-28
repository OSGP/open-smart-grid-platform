/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

public class FirmwareModuleData implements Serializable {

    private static final long serialVersionUID = 3479817852183883103L;

    private final String comm;
    private final String func;
    private final String ma;
    private final String mbus;
    private final String sec;

    public FirmwareModuleData(final String comm, final String func, final String ma, final String mbus,
            final String sec) {
        this.comm = comm;
        this.func = func;
        this.ma = ma;
        this.mbus = mbus;
        this.sec = sec;
    }

    public String getComm() {
        return this.comm;
    }

    public String getFunc() {
        return this.func;
    }

    public String getMa() {
        return this.ma;
    }

    public String getMbus() {
        return this.mbus;
    }

    public String getSec() {
        return this.sec;
    }

}
