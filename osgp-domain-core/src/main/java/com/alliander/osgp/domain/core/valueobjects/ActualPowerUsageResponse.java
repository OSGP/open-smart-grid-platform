package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import com.alliander.osgp.domain.core.entities.DeviceMessageStatus;

public class ActualPowerUsageResponse implements Serializable {

    private static final long serialVersionUID = 3873494432836212279L;

    private DeviceMessageStatus status;
    private final PowerUsageData powerUsageData;

    public ActualPowerUsageResponse(final DeviceMessageStatus status, final PowerUsageData newPowerUsageData) {
        if (status == null) {
            throw new IllegalArgumentException("Status is null.");
        } else {
            this.status = status;
        }

        this.powerUsageData = newPowerUsageData;
    }

    public DeviceMessageStatus getStatus() {
        return this.status;
    }

    public PowerUsageData getPowerUsageData() {
        return this.powerUsageData;
    }
}
