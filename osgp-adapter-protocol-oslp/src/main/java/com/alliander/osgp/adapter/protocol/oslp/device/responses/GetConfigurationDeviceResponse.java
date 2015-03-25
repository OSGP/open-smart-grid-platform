package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.Configuration;

public class GetConfigurationDeviceResponse extends EmptyDeviceResponse {

    Configuration configuration;

    public GetConfigurationDeviceResponse(final String organisation, final String device, final String correlationUid,
            final DeviceMessageStatus status, final Configuration configuration) {
        super(organisation, device, correlationUid, status);
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

}
