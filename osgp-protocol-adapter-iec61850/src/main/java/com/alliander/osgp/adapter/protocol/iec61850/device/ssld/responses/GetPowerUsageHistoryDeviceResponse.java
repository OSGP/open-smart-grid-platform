/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses;

import java.util.List;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.PowerUsageDataDto;

public class GetPowerUsageHistoryDeviceResponse extends EmptyDeviceResponse {

    private final List<PowerUsageDataDto> powerUsageHistoryData;

    public GetPowerUsageHistoryDeviceResponse(final DeviceRequest deviceRequest, final DeviceMessageStatus status,
            final List<PowerUsageDataDto> powerUsageHistoryData) {
        super(deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                deviceRequest.getCorrelationUid(), deviceRequest.getMessagePriority(), status);
        this.powerUsageHistoryData = powerUsageHistoryData;
    }

    public List<PowerUsageDataDto> getPowerUsageHistoryData() {
        return this.powerUsageHistoryData;
    }
}
