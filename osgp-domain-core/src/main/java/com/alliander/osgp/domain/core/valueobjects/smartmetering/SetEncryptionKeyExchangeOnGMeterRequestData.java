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

public class SetEncryptionKeyExchangeOnGMeterRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = 3965412208032103531L;

    private String deviceIdentification;

    public SetEncryptionKeyExchangeOnGMeterRequestData(final String deviceIdentification) {
        super();
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed
    }

}
