package com.alliander.osgp.domain.core.valueobjects;

import com.alliander.osgp.domain.core.entities.DeviceMessageStatus;

public class ConfigurationResponse {
    private final DeviceMessageStatus deviceMessageStatus;
    private final Configuration configuration;

    public ConfigurationResponse(final DeviceMessageStatus deviceMessageStatus, final Configuration configuration) {
        this.deviceMessageStatus = deviceMessageStatus;
        this.configuration = configuration;
    }

    public DeviceMessageStatus getDeviceMessageStatus() {
        return this.deviceMessageStatus;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }
}
