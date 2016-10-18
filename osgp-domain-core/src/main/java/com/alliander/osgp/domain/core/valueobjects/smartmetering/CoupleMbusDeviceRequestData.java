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
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

/**
 * this class holds the information needed beside the metadata of a request to
 * couple a device and a m-bus device
 */
public class CoupleMbusDeviceRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = 8993111326494612489L;

    private final String mbusDeviceIdentification;
    private static final short MIN_CHANNEL = 1;
    private static final short MAX_CHANNEL = 4;

    private final short channel;

    /**
     * @param mbusDeviceIdentification
     *            the mbus device that needs to be coupled to the device in the
     *            metadata information of the request
     * @param channel
     *            the channel on which the mbus device needs to be coupled
     */
    public CoupleMbusDeviceRequestData(final String mbusDeviceIdentification, final short channel) {
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.channel = channel;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public short getChannel() {
        return this.channel;
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
        if ((this.channel < MIN_CHANNEL) || (this.channel > MAX_CHANNEL)) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new IllegalArgumentException("channel not in range [1,4]: "
                            + this.channel));
        }

    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.COUPLE_MBUS_DEVICE;
    }

}