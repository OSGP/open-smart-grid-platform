/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetAdministrationState implements Serializable {

    private static final long serialVersionUID = -1399391398920839144L;

    private final String deviceIdentification;

    private final AdministrationStateType status;

    public GetAdministrationState(final String deviceIdentification, final AdministrationStateType status) {
        this.deviceIdentification = deviceIdentification;
        this.status = status;
    }

    public AdministrationStateType getStatus() {
        return this.status;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }
}