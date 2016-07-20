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

    private final String moduleVersionComm;
    private final String moduleVersionFunc;
    private final String moduleVersionMa;
    private final String moduleVersionMbus;
    private final String moduleVersionSec;

    public FirmwareModuleData(final String moduleVersionComm, final String moduleVersionFunc,
            final String moduleVersionMa, final String moduleVersionMbus, final String moduleVersionSec) {
        this.moduleVersionComm = moduleVersionComm;
        this.moduleVersionFunc = moduleVersionFunc;
        this.moduleVersionMa = moduleVersionMa;
        this.moduleVersionMbus = moduleVersionMbus;
        this.moduleVersionSec = moduleVersionSec;
    }

    public String getModuleVersionComm() {
        return this.moduleVersionComm;
    }

    public String getModuleVersionFunc() {
        return this.moduleVersionFunc;
    }

    public String getModuleVersionMa() {
        return this.moduleVersionMa;
    }

    public String getModuleVersionMbus() {
        return this.moduleVersionMbus;
    }

    public String getModuleVersionSec() {
        return this.moduleVersionSec;
    }

}
