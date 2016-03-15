/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

    private static final long serialVersionUID = 8978488633831083383L;

    private final SmartMeteringRequestMessageType messageType;
    private final Integer messagePriority;

    /**
     * Use builder instead
     *
     * @deprecated
     */
    @Deprecated
    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable request) {
        this(messageType, correlationUid, organisationIdentification, deviceIdentification, null, request);
    }

    /**
     * Use builder instead
     *
     * @deprecated
     */
    @Deprecated
    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String ipAddress,
            final Serializable request) {
        this(messageType, correlationUid, organisationIdentification, deviceIdentification, ipAddress, request, null);
    }

    /**
     * Use builder instead
     *
     * @deprecated
     */
    @Deprecated
    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType setEncryptionKeyExchangeOnGMeter,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification) {
        this(setEncryptionKeyExchangeOnGMeter, correlationUid, organisationIdentification, deviceIdentification, null);
    }

    private SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String ipAddress,
            final Serializable request, final Integer messagePriority) {
        super(correlationUid, organisationIdentification, deviceIdentification, ipAddress, request);
        this.messageType = messageType;
        this.messagePriority = messagePriority;
    }

    public static class Builder {
        private SmartMeteringRequestMessageType messageType;
        private String correlationUid;
        private String organisationIdentification;
        private String deviceIdentification;
        private String ipAddress;
        private Serializable request;
        private Integer messagePriority;

        public Builder() {
            // empty constructor
        }

        public Builder messageType(final SmartMeteringRequestMessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder correlationUid(final String correlationUid) {
            this.correlationUid = correlationUid;
            return this;
        }

        public Builder organisationIdentification(final String organisationIdentification) {
            this.organisationIdentification = organisationIdentification;
            return this;
        }

        public Builder deviceIdentification(final String deviceIdentification) {
            this.deviceIdentification = deviceIdentification;
            return this;
        }

        public Builder ipAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder request(final Serializable request) {
            this.request = request;
            return this;
        }

        public Builder messagePriority(final Integer messagePriority) {
            this.messagePriority = messagePriority;
            return this;
        }

        public SmartMeteringRequestMessage build() {
            return new SmartMeteringRequestMessage(this.messageType, this.correlationUid,
                    this.organisationIdentification, this.deviceIdentification, this.ipAddress, this.request,
                    this.messagePriority);
        }
    }

    public Integer getMessagePriority() {
        return this.messagePriority;
    }

    public SmartMeteringRequestMessageType getMessageType() {
        return this.messageType;
    }
}
