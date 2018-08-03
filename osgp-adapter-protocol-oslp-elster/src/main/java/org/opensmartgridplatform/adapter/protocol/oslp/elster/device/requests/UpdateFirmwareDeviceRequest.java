/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class UpdateFirmwareDeviceRequest extends DeviceRequest {

    private final String firmwareDomain;
    private final String firmwareUrl;

    public UpdateFirmwareDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority, final String firmwareDomain,
            final String firmwareUrl) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
        this.firmwareDomain = firmwareDomain;
        this.firmwareUrl = firmwareUrl;
    }

    public UpdateFirmwareDeviceRequest(final Builder deviceRequestBuilder, final String firmwareDomain,
            final String firmwareUrl) {
        super(deviceRequestBuilder);
        this.firmwareDomain = firmwareDomain;
        this.firmwareUrl = firmwareUrl;
    }

    public String getFirmwareDomain() {
        return this.firmwareDomain;
    }

    public String getFirmwareUrl() {
        return this.firmwareUrl;
    }
}
