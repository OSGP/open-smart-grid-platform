package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
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
