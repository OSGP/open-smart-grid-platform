/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class SwitchConfigurationBankRequest extends DeviceRequest {

    private final String configurationBank;

    public SwitchConfigurationBankRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority, final String configurationBank) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

        this.configurationBank = configurationBank;
    }

    public SwitchConfigurationBankRequest(final Builder deviceRequestBuilder, final String configurationBank) {
        super(deviceRequestBuilder);
        this.configurationBank = configurationBank;
    }

    public String getConfigurationBank() {
        return this.configurationBank;
    }
}
