/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SetConfigurationObjectRequest implements Serializable {

    private static final long serialVersionUID = -8295596279285780413L;

    private String deviceIdentification;

    private SetConfigurationObjectRequestData setConfigurationObjectRequestData;

    public String getDeviceIdentification() {
        return deviceIdentification;
    }

    public void setDeviceIdentification(String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public SetConfigurationObjectRequestData getSetConfigurationObjectRequestData() {
        return setConfigurationObjectRequestData;
    }

    public void setSetConfigurationObjectRequestData(SetConfigurationObjectRequestData setConfigurationObjectRequestData) {
        this.setConfigurationObjectRequestData = setConfigurationObjectRequestData;
    }
}