/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.device.responses;

import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceResponse;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;

public class GetHealthStatusDeviceResponse extends DeviceResponse {

    private final GetHealthStatusResponseDto deviceHealthStatus;

    public GetHealthStatusDeviceResponse(final DeviceRequest deviceRequest,
            final GetHealthStatusResponseDto deviceHealthStatus) {
        super(deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), deviceRequest.getMessagePriority());
        this.deviceHealthStatus = deviceHealthStatus;
    }

    public GetHealthStatusResponseDto getDeviceHealthStatus() {
        return this.deviceHealthStatus;
    }
}
