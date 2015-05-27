/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;

public class GetPowerUsageHistoryDeviceResponse extends EmptyDeviceResponse {

    private List<PowerUsageData> powerUsageHistoryData;

    public GetPowerUsageHistoryDeviceResponse(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final DeviceMessageStatus status,
            final List<PowerUsageData> powerUsageHistoryData) {
        super(organisationIdentification, deviceIdentification, correlationUid, status);
        this.powerUsageHistoryData = powerUsageHistoryData;
    }

    public List<PowerUsageData> getPowerUsageHistoryData() {
        return this.powerUsageHistoryData;
    }
}
