/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class ActualMeterReadsGasData implements Serializable, ActionValueObject {

    private static final long serialVersionUID = 2901630229011386951L;
    private String deviceIdentification;

    public ActualMeterReadsGasData(final String deviceIdentification2) {
        this.deviceIdentification = deviceIdentification2;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public void validate() throws FunctionalException {
        // no validation needed

    }

}
