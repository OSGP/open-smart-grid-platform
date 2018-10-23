/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

import org.opensmartgridplatform.domain.core.entities.DeviceMessageStatus;

public class ActualPowerUsageResponse implements Serializable {

    /**
     * Serial Version UID.
     */
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
