/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import org.apache.commons.lang3.StringUtils;

public class Iec60870LogItemRequestMessage {

    private static final int MAX_MESSAGE_LENGTH = 8000;

    private boolean incoming;

    private String encodedMessage;

    private String decodedMessage;

    private String deviceIdentification;

    private String organisationIdentification;

    private boolean valid;

    private int payloadMessageSerializedSize;

    public Iec60870LogItemRequestMessage(final String deviceIdentification, final String organisationIdentification,
            final boolean incoming, final boolean valid, final String message, final int payloadMessageSerializedSize) {
        this.deviceIdentification = deviceIdentification;
        this.organisationIdentification = organisationIdentification;
        this.incoming = incoming;
        this.valid = valid;
        this.payloadMessageSerializedSize = payloadMessageSerializedSize;

        // Truncate the log-items to max length.
        this.encodedMessage = null;
        this.decodedMessage = StringUtils.substring(message, 0, MAX_MESSAGE_LENGTH);
    }

    public Boolean isIncoming() {
        return this.incoming;
    }

    public String getEncodedMessage() {
        return this.encodedMessage;
    }

    public String getDecodedMessage() {
        return this.decodedMessage;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public Boolean isValid() {
        return this.valid;
    }

    public int getPayloadMessageSerializedSize() {
        return this.payloadMessageSerializedSize;
    }
}
