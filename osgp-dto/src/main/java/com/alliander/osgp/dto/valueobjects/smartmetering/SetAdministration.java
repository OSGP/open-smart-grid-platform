package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetAdministration implements Serializable {

    private static final long serialVersionUID = 5653168808776680422L;

    private final boolean enabled;

    private final String deviceIdentification;

    public SetAdministration(final boolean enabled, final String deviceIdentification) {
        this.enabled = enabled;
        this.deviceIdentification = deviceIdentification;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

}