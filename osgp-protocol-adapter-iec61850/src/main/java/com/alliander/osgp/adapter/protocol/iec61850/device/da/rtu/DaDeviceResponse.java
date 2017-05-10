/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;

import java.io.Serializable;

public class DaDeviceResponse extends EmptyDeviceResponse {

    private Serializable dataResponse;

    public DaDeviceResponse(final String organisation, final String device, final String correlationUid,
                            final DeviceMessageStatus status, final Serializable dataResponse) {
        super(organisation, device, correlationUid, status);
        this.dataResponse = dataResponse;
    }

    public Serializable getDataResponse() {
        return this.dataResponse;
    }
}
