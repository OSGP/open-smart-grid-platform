/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.CertificationDto;

public class UpdateDeviceSslCertificationDeviceRequest extends DeviceRequest {

    private final CertificationDto certification;

    public UpdateDeviceSslCertificationDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final int messagePriority,
            final CertificationDto certification) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

        this.certification = certification;
    }

    public UpdateDeviceSslCertificationDeviceRequest(final Builder deviceRequestBuilder,
            final CertificationDto certification) {
        super(deviceRequestBuilder);
        this.certification = certification;
    }

    public CertificationDto getCertification() {
        return this.certification;
    }
}
