/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.messaging;

import org.apache.commons.lang3.StringUtils;

public class CoreLogItemRequestMessage {

    private static final int MAX_MESSAGE_LENGTH = 8000;

    private final String decodedMessage;

    private final String deviceIdentification;

    private final String organisationIdentification;

    private final boolean valid;

    private final String retryCount;

    public CoreLogItemRequestMessage(final String deviceIdentification, final String organisationIdentification,
            final String decodedMessage) {
        this.deviceIdentification = deviceIdentification;
        this.organisationIdentification = organisationIdentification;
        this.valid = true;
        this.decodedMessage = StringUtils.substring(decodedMessage, 0, MAX_MESSAGE_LENGTH);
        this.retryCount = null;
    }

    public String getDecodedMessage() {
        return this.decodedMessage;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public boolean hasOrganisationIdentification() {
        return this.organisationIdentification != null;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public Boolean isValid() {
        return this.valid;
    }

    public String getRetryCount() {
        return this.retryCount;
    }
}
