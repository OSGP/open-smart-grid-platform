package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ObjectListElement implements Serializable {

    private static final long serialVersionUID = 2432320129309477392L;

    private long classId;

    private int version;

    private String logicalName;

    public ObjectListElement(final long classId, final int version, final String logicalName) {
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

    public String getLogicalName() {
        return this.logicalName;
    }
}
