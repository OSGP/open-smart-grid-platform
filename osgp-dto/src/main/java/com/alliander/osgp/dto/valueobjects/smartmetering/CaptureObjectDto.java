/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class CaptureObjectDto implements Serializable {

    private static final long serialVersionUID = 2123390296585369208L;

    private final long classId;
    private final ObisCodeValuesDto logicalName;
    private final int attribute;
    private final int version;

    public CaptureObjectDto(long classId, ObisCodeValuesDto logicalName, int attribute, int version) {
        super();
        this.classId = classId;
        this.logicalName = logicalName;
        this.attribute = attribute;
        this.version = version;
    }

    public long getClassId() {
        return this.classId;
    }

    public ObisCodeValuesDto getLogicalName() {
        return this.logicalName;
    }

    public int getAttribute() {
        return this.attribute;
    }

    public int getVersion() {
        return this.version;
    }

}
