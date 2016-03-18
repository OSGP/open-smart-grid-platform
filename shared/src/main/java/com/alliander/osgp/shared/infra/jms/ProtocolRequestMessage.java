/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class ProtocolRequestMessage extends RequestMessage {

    private static final long serialVersionUID = -6951175556510738951L;

    private final String domain;
    private final String domainVersion;
    private final String messageType;
    private final Serializable messageData;
    private final boolean scheduled;
    private final int retryCount;
    private int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();

    /**
     * Constructor with no scheduled flag and no messagePriority
     *
     * @deprecated use the Builder in stead. Too many arguments in constructor
     */
    @Deprecated
    public ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                ipAddress, request, false, retryCount);
    }

    /**
     * Constructor with scheduled flag and no messagePriority
     *
     * @deprecated use the Builder in stead. Too many arguments in constructor
     */
    @Deprecated
    public ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final boolean scheduled, final int retryCount) {
        super(correlationUid, organisationIdentification, deviceIdentification, ipAddress, request);
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.messageData = request;
        this.scheduled = scheduled;
        this.retryCount = retryCount;
    }

    /**
     * Constructor with both a scheduled flag and a messagePriority. Only called
     * from Builder
     */
    private ProtocolRequestMessage(final DeviceMessageMetadata deviceMessageMetadata, final String domain,
            final String domainVersion, final String ipAddress, final Serializable request, final boolean scheduled,
            final int retryCount) {
        super(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(), ipAddress, request);

        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageData = request;
        this.scheduled = scheduled;
        this.retryCount = retryCount;

        this.messageType = deviceMessageMetadata.getMessageType();
        this.messagePriority = deviceMessageMetadata.getMessagePriority();
    }

    public static class Builder {
        private String domain;
        private String domainVersion;
        private String ipAddress;
        private Serializable request;
        private boolean scheduled;
        private int retryCount;

        private DeviceMessageMetadata deviceMessageMetadata;

        public Builder deviceMessageMetadata(final DeviceMessageMetadata deviceMessageMetadata) {
            this.deviceMessageMetadata = deviceMessageMetadata;
            return this;
        }

        public Builder domain(final String domain) {
            this.domain = domain;
            return this;
        }

        public Builder domainVersion(final String domainVersion) {
            this.domainVersion = domainVersion;
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

        public Builder scheduled(final boolean scheduled) {
            this.scheduled = scheduled;
            return this;
        }

        public Builder retryCount(final int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public ProtocolRequestMessage build() {
            return new ProtocolRequestMessage(this.deviceMessageMetadata, this.domain, this.domainVersion,
                    this.ipAddress, this.request, this.scheduled, this.retryCount);
        }
    }

    public String getDomain() {
        return this.domain;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public Serializable getMessageData() {
        return this.messageData;
    }

    public boolean isScheduled() {
        return this.scheduled;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

}
