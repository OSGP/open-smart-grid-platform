/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.LightValueMessageDataContainerDto;

public class SetLightDeviceRequest extends DeviceRequest {

    private final LightValueMessageDataContainerDto lightValuesContainer;

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority,
            final LightValueMessageDataContainerDto lightValuesContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

        this.lightValuesContainer = lightValuesContainer;
    }

    public SetLightDeviceRequest(final Builder deviceRequestBuilder,
            final LightValueMessageDataContainerDto lightValuesContainer) {
        super(deviceRequestBuilder);
        this.lightValuesContainer = lightValuesContainer;
    }

    public LightValueMessageDataContainerDto getLightValuesContainer() {
        return this.lightValuesContainer;
    }
}
