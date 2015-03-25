package com.alliander.osgp.adapter.protocol.oslp.device;

public class DeviceResponse {

    private String organisationIdentification;
    private String deviceIdentification;
    private String correlationUid;

    public DeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }
}
