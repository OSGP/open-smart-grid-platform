/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

/**
 * this class holds the information needed beside the metadata of a request to
 * decouple a device and a m-bus device
 */
public class DeCoupleMbusDeviceRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = -1152385333807906318L;
    private final String mbusDeviceIdentification;

    /**
     * @param mbusDeviceIdentification
     *            the mbus device that needs to be decoupled to the device in
     *            the metadata information of the request
     */
    public DeCoupleMbusDeviceRequestData(final String mbusDeviceIdentification) {
        this.mbusDeviceIdentification = mbusDeviceIdentification;

    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionRequest#
     * validate()
     */
    @Override
    public void validate() throws FunctionalException {
        // no validation needed
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.DE_COUPLE_MBUS_DEVICE;
    }
}
