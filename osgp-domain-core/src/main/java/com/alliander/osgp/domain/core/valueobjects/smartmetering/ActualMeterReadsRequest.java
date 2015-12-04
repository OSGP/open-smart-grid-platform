/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * request actual reads for E or GAS meters
 * 
 * @author dev
 */
public class ActualMeterReadsRequest implements Serializable {
    private static final long serialVersionUID = 3751586818507193990L;

    private final String deviceIdentification;
    private final boolean gas;

    public ActualMeterReadsRequest(final String deviceIdentification) {
        this(deviceIdentification, false);
    }

    public ActualMeterReadsRequest(final String deviceIdentification, final boolean gas) {
        this.deviceIdentification = deviceIdentification;
        this.gas = gas;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public boolean isGas() {
        return gas;
    }

}
