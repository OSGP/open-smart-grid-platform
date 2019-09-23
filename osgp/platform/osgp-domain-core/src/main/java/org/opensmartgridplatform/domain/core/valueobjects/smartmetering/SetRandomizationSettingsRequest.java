package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class SetRandomizationSettingsRequest implements Serializable {

    private static final long serialVersionUID = -8295596279285700413L;
    
    private final String deviceIdentification;

    private SetRandomizationSettingsRequestData setRandomizationSettingsRequestData;

    public SetRandomizationSettingsRequest(String deviceIdentification,
            SetRandomizationSettingsRequestData setRandomizationSettingsRequestData) {
        this.deviceIdentification = deviceIdentification;
        this.setRandomizationSettingsRequestData = setRandomizationSettingsRequestData;
    }

    public String getDeviceIdentification() {
        return deviceIdentification;
    }

    public SetRandomizationSettingsRequestData getSetRandomizationSettingsRequestData() {
        return setRandomizationSettingsRequestData;
    }
}
