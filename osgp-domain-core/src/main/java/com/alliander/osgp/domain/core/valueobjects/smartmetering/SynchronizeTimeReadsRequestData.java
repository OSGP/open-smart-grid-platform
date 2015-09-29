package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class SynchronizeTimeReadsRequestData implements Serializable {

    private static final long serialVersionUID = -1483665562035897062L;

    private String deviceIdentification;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }
}