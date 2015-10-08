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
import java.util.List;

public class SpecialDaysRequest implements Serializable {

    private static final long serialVersionUID = 2863312762786033679L;

    private String deviceIdentification;

    private List<SpecialDaysRequestData> specialDaysRequestData;

    public String getDeviceIdentification() {
        return deviceIdentification;
    }

    public void setDeviceIdentification(String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<SpecialDaysRequestData> getSpecialDaysRequestData() {
        return specialDaysRequestData;
    }

    public void setSpecialDaysRequestData(List<SpecialDaysRequestData> specialDaysRequestData) {
        this.specialDaysRequestData = specialDaysRequestData;
    }
}