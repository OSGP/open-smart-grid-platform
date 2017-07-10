/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class GetMBusDeviceOnChannelRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = -1330890838864967863L;

    private final String gatewayDeviceIdentification;
    private final short channel;

    public GetMBusDeviceOnChannelRequestData(final String gatewayDeviceIdentification, final short channel) {
        this.gatewayDeviceIdentification = gatewayDeviceIdentification;
        this.channel = channel;
    }

    public String getGatewayDeviceIdentification() {
        return this.gatewayDeviceIdentification;
    }

    public short getChannel() {
        return this.channel;
    }

    @Override
    public void validate() throws FunctionalException {
        // nothing to validate
    }

    @Override
    public DeviceFunction getDeviceFunction() {
        return DeviceFunction.GET_M_BUS_DEVICE_ON_CHANNEL;
    }

}
