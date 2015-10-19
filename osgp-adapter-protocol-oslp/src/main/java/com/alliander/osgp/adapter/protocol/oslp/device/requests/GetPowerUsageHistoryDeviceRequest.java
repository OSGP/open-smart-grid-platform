/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.HistoryTermType;
import com.alliander.osgp.dto.valueobjects.TimePeriod;

public class GetPowerUsageHistoryDeviceRequest extends DeviceRequest {

    private TimePeriod timePeriod;
    private HistoryTermType historyTermType;

    public GetPowerUsageHistoryDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final TimePeriod timePeriod,
            final HistoryTermType historyTermType) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.timePeriod = timePeriod;
        this.historyTermType = historyTermType;
    }

    public GetPowerUsageHistoryDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final TimePeriod timePeriod,
            final HistoryTermType historyTermType, final String domain, final String domainVersion,
            final String messageType, final String ipAddress, final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);
        this.timePeriod = timePeriod;
        this.historyTermType = historyTermType;
    }

    public TimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    public HistoryTermType getHistoryTermType() {
        return this.historyTermType;
    }
}
