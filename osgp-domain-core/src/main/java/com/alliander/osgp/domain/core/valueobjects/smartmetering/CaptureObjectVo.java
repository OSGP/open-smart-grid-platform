/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class CaptureObjectVo implements Serializable {

    private static final long serialVersionUID = 991045734132231909L;

    private final long classId;
    private final ObisCodeValues logicalName;
    private final int attribute;
    private final int version;

    public CaptureObjectVo(long classId, ObisCodeValues logicalName, int attribute, int version) {
        this.classId = classId;
        this.logicalName = logicalName;
        this.attribute = attribute;
        this.version = version;
    }

    public long getClassId() {
        return this.classId;
    }

    public ObisCodeValues getLogicalName() {
        return this.logicalName;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public int getVersion() {
        return this.version;
    }
}
