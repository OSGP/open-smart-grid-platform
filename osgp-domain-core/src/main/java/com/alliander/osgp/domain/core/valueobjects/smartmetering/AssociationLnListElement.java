/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class AssociationLnListElement implements Serializable {

    private static final long serialVersionUID = 2432320129309477392L;

    private long classId;

    private int version;

    private CosemObisCode logicalName;

    public AssociationLnListElement(final long classId, final int version, final CosemObisCode logicalName) {
        this.classId = classId;
        this.version = version;
        this.logicalName = logicalName;
    }

    public long getClassId() {
        return this.classId;
    }

    public int getVersion() {
        return this.version;
    }

    public CosemObisCode getLogicalName() {
        return this.logicalName;
    }
}
