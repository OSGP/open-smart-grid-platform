/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;

public class SwitchConfigurationBankRequest extends DeviceRequest {

    private String configurationBank;

    public SwitchConfigurationBankRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String configurationBank) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.configurationBank = configurationBank;
    }

    public SwitchConfigurationBankRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String configurationBank, final String domain,
            final String domainVersion, final String messageType, final String ipAddress, final int retryCount,
            final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.configurationBank = configurationBank;
    }

    public String getConfigurationBank() {
        return this.configurationBank;
    }
}
