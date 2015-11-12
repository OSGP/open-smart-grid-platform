package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SetAdministration implements Serializable {

    private static final long serialVersionUID = 5653168808776680422L;

    private boolean enabled;

    private String deviceIdentification;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }
}
