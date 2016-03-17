/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class SmartMeteringRequestMessage extends RequestMessage {

    private static final long serialVersionUID = 8978488633831083383L;

    private final String messageType;
    private final Integer messagePriority;

    /**
     * Use builder instead
     *
     * @deprecated
     */
    @Deprecated
    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable request) {
        this(messageType.toString(), correlationUid, organisationIdentification, deviceIdentification, null, request,
                MessagePriorityEnum.DEFAULT.getPriority());
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
        this(messageType.toString(), correlationUid, organisationIdentification, deviceIdentification, ipAddress,
                request, MessagePriorityEnum.DEFAULT.getPriority());
    }

    /**
     * Use builder instead
     *
     * @deprecated
     */
    @Deprecated
    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification) {
        this(messageType.toString(), correlationUid, organisationIdentification, deviceIdentification, null, null,
                MessagePriorityEnum.DEFAULT.getPriority());
    }

    private SmartMeteringRequestMessage(final String messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String ipAddress,
            final Serializable request, final Integer messagePriority) {
        super(correlationUid, organisationIdentification, deviceIdentification, ipAddress, request);
        this.messageType = messageType;
        this.messagePriority = messagePriority;
    }

    private SmartMeteringRequestMessage(final DeviceMessageMetadata deviceMessageMetadata, final String ipAddress,
            final Serializable request) {
        super(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(), ipAddress, request);
        this.messageType = deviceMessageMetadata.getMessageType();
        this.messagePriority = deviceMessageMetadata.getMessagePriority();
    }

    public static class Builder {
        private DeviceMessageMetadata deviceMessageMetadata;
        private String ipAddress;
        private Serializable request;

        public Builder() {
            // empty constructor
        }

        public Builder deviceMessageMetadata(final DeviceMessageMetadata deviceMessageMetadata) {
            this.deviceMessageMetadata = deviceMessageMetadata;
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

        public SmartMeteringRequestMessage build() {
            return new SmartMeteringRequestMessage(this.deviceMessageMetadata, this.ipAddress, this.request);
        }
    }

    public Integer getMessagePriority() {
        return this.messagePriority;
    }

    public String getMessageType() {
        return this.messageType;
    }
}
