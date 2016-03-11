/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.Certification;

public class UpdateDeviceSslCertificationDeviceRequest extends DeviceRequest {

    private Certification certification;

    public UpdateDeviceSslCertificationDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Certification certification) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.certification = certification;
    }

    public UpdateDeviceSslCertificationDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Certification certification, final String domain, final String domainVersion,
            final String messageType, final String ipAddress, final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.certification = certification;
    }

    public Certification getCertification() {
        return this.certification;
    }
}
