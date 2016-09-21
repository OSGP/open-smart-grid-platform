package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;

public class SystemIdentifierDto implements Serializable {

    private static final long serialVersionUID = -8592667499461927077L;

    private int id;
    private String systemType;

    public SystemIdentifierDto(final int id, final String systemType) {
        this.id = id;
        this.systemType = systemType;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getSystemType() {
        return this.systemType;
    }

    public void setSystemType(final String systemType) {
        this.systemType = systemType;
    }

}
