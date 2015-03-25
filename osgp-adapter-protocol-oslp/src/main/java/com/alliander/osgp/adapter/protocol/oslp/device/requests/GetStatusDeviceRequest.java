package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.DomainType;

public class GetStatusDeviceRequest extends DeviceRequest {

    private DomainType domainType;

    public GetStatusDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DomainType domainType) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.domainType = domainType;
    }

    public DomainType getDomainType() {
        return this.domainType;
    }
}
