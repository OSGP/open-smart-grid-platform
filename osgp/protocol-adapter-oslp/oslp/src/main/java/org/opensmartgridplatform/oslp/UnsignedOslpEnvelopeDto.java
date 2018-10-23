/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import java.io.Serializable;

import org.opensmartgridplatform.oslp.Oslp.Message;

public class UnsignedOslpEnvelopeDto implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4175402017349718991L;

    public static final String OSLP_REQUEST_TYPE = "OSLP_REQUEST";
    public static final String OSLP_RESPONSE_TYPE = "OSLP_RESPONSE";

    /**
     * Buffer for sequence number bytes.
     */
    private byte[] sequenceNumber = new byte[OslpEnvelope.SEQUENCE_NUMBER_LENGTH];

    /**
     * Buffer for deviceid bytes.
     */
    private byte[] deviceId = new byte[OslpEnvelope.DEVICE_ID_LENGTH + OslpEnvelope.MANUFACTURER_ID_LENGTH];

    /**
     * Buffer for OSLP payload.
     */
    private final Message payloadMessage;

    /**
     * The IP address of the device.
     */
    private String ipAddress;

    // Other values which need to be passed back to protocol adapter.

    private String domain;
    private String domainVersion;
    private String messageType;
    private int messagePriority;
    private int retryCount;
    private boolean isScheduled;
    private final String organisationIdentification;
    private final String correlationUid;
    private Serializable extraData;

    private final String type;

    /**
     * Constructor for OSLP device responses.
     */
    public UnsignedOslpEnvelopeDto(final byte[] sequenceNumber, final byte[] deviceId, final Message payloadMessage,
            final String correlationUid) {
        this.sequenceNumber = sequenceNumber;
        this.deviceId = deviceId;
        this.payloadMessage = payloadMessage;

        this.organisationIdentification = "organisationIdentification";
        this.correlationUid = correlationUid;

        this.type = OSLP_RESPONSE_TYPE;
    }

    /**
     * Constructor for OSLP device requests, with extra data field which can
     * contain any serializable object.
     */
    public UnsignedOslpEnvelopeDto(final byte[] sequenceNumber, final byte[] deviceId, final Message payloadMessage,
            final String ipAddress, final String domain, final String domainVersion, final String messageType,
            final int messagePriority, final int retryCount, final boolean isScheduled,
            final String organisationIdentification, final String correlationUid, final Serializable extraData) {
        this.sequenceNumber = sequenceNumber;
        this.deviceId = deviceId;
        this.payloadMessage = payloadMessage;
        this.ipAddress = ipAddress;

        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.messagePriority = messagePriority;
        this.retryCount = retryCount;
        this.isScheduled = isScheduled;
        this.organisationIdentification = organisationIdentification;
        this.correlationUid = correlationUid;
        this.extraData = extraData;

        this.type = OSLP_REQUEST_TYPE;
    }

    public byte[] getSequenceNumber() {
        return this.sequenceNumber;
    }

    public byte[] getDeviceId() {
        return this.deviceId;
    }

    public Message getPayloadMessage() {
        return this.payloadMessage;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public boolean isScheduled() {
        return this.isScheduled;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public Serializable getExtraData() {
        return this.extraData;
    }

    public String getType() {
        return this.type;
    }
}
