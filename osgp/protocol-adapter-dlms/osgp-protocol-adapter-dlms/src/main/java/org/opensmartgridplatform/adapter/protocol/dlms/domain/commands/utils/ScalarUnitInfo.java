/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import org.openmuc.jdlms.datatypes.DataObject;

public class ScalarUnitInfo {

    private final String logicalName;
    private final int classId;
    private final DataObject scalarUnit;

    public ScalarUnitInfo(final String logicalName, final int classId, final DataObject scalarUnit) {
        super();
        this.logicalName = logicalName;
        this.classId = classId;
        this.scalarUnit = scalarUnit;
    }

    @Override
    public String toString() {
        return "ScalarUnitInfo [logicalName=" + this.logicalName + ", classId=" + this.classId + ", scalarUnit="
                + this.scalarUnit + "]";
    }

    public String getLogicalName() {
        return this.logicalName;
    }

    public int getClassId() {
        return this.classId;
    }

    public DataObject getScalarUnit() {
        return this.scalarUnit;
    }

}
