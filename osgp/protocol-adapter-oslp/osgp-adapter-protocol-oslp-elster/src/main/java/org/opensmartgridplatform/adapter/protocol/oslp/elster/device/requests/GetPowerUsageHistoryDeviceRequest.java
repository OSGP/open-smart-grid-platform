/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;

public class GetPowerUsageHistoryDeviceRequest extends DeviceRequest {

    private final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryContainer;

    public GetPowerUsageHistoryDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority,
            final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
        this.powerUsageHistoryContainer = powerUsageHistoryContainer;
    }

    public GetPowerUsageHistoryDeviceRequest(final Builder deviceRequestBuilder,
            final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryContainer) {
        super(deviceRequestBuilder);
        this.powerUsageHistoryContainer = powerUsageHistoryContainer;
    }

    public PowerUsageHistoryMessageDataContainerDto getPowerUsageHistoryContainer() {
        return this.powerUsageHistoryContainer;
    }
}
