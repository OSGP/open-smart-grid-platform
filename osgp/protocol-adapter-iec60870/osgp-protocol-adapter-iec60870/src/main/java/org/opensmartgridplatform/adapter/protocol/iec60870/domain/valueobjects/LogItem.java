/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import org.apache.commons.lang3.StringUtils;

public class LogItem {

    private static final int MAX_MESSAGE_LENGTH = 8000;

    private boolean incoming;

    private String message;

    private String deviceIdentification;

    private String organisationIdentification;

    public LogItem(final String deviceIdentification, final String organisationIdentification, final boolean incoming,
            final String message) {
        this.deviceIdentification = deviceIdentification;
        this.organisationIdentification = organisationIdentification;
        this.incoming = incoming;

        // Truncate the log-items to max length.
        this.message = StringUtils.substring(message, 0, MAX_MESSAGE_LENGTH);
    }

    public Boolean isIncoming() {
        return this.incoming;
    }

    public String getMessage() {
        return this.message;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }
}
