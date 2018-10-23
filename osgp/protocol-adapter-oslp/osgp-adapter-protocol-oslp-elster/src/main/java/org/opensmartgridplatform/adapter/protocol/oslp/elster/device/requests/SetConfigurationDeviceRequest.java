/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;

public class SetConfigurationDeviceRequest extends DeviceRequest {

    private final ConfigurationDto configuration;

    public SetConfigurationDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ConfigurationDto configuration, final int messagePriority) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

        this.configuration = configuration;
    }

    public SetConfigurationDeviceRequest(final Builder deviceRequestBuilder, final ConfigurationDto configuration) {
        super(deviceRequestBuilder);
        this.configuration = configuration;
    }

    public ConfigurationDto getConfiguration() {
        return this.configuration;
    }
}
