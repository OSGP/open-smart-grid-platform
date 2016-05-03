/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.ConfigurationDto;

public class GetConfigurationDeviceResponse extends EmptyDeviceResponse {

    ConfigurationDto configuration;

    public GetConfigurationDeviceResponse(final String organisation, final String device, final String correlationUid,
            final DeviceMessageStatus status, final ConfigurationDto configuration) {
        super(organisation, device, correlationUid, status);
        this.configuration = configuration;
    }

    public ConfigurationDto getConfiguration() {
        return this.configuration;
    }

}
