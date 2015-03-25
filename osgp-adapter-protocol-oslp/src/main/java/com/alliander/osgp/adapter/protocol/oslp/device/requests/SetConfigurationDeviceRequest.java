package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.Configuration;

public class SetConfigurationDeviceRequest extends DeviceRequest {

    private Configuration configuration;

    public SetConfigurationDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Configuration configuration) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
