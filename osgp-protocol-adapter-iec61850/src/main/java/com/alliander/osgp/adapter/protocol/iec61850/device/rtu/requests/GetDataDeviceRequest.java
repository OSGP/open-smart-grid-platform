/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataRequestDto;

public class GetDataDeviceRequest extends DeviceRequest {

    private GetDataRequestDto dataRequest;

    public GetDataDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final GetDataRequestDto dataRequest) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.dataRequest = dataRequest;
    }

    public GetDataDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final GetDataRequestDto dataRequest, final String domain,
            final String domainVersion, final String messageType, final String ipAddress, final int retryCount,
            final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.dataRequest = dataRequest;
    }

    public GetDataRequestDto getDataRequest() {
        return this.dataRequest;
    }
}
