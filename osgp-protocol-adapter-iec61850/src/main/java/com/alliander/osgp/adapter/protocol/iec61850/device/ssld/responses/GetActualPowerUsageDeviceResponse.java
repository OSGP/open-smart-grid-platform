/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.PowerUsageDataDto;

public class GetActualPowerUsageDeviceResponse extends EmptyDeviceResponse {

    private final PowerUsageDataDto actualPowerUsageData;

    public GetActualPowerUsageDeviceResponse(final DeviceRequest deviceRequest, final DeviceMessageStatus status,
            final PowerUsageDataDto actualPowerUsageData) {
        super(deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), deviceRequest.getMessagePriority(), status);
        this.actualPowerUsageData = actualPowerUsageData;
    }

    public PowerUsageDataDto getActualPowerUsageData() {
        return this.actualPowerUsageData;
    }
}
