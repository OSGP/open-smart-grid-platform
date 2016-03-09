/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriority;

public class ProtocolRequestMessage extends RequestMessage {

    private static final long serialVersionUID = -6951175556510738951L;

    private final String domain;
    private final String domainVersion;
    private final String messageType;
    private final String ipAddress;
    private final Serializable messageData;
    private final boolean scheduled;
    private final int retryCount;
    private final int messagePriority;

    /**
     * Constructor with no scheduled flag and no messagePriority
     *
     * @deprecated use the Builder in stead. Constructors have too a lot of
     *             arguments of which some are optional
     */
    @Deprecated
    public ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                ipAddress, request, false, retryCount);
    }

    /**
     * Constructor with no scheduled flag and no messagePriority
     *
     * @deprecated use the Builder in stead. Constructors have too a lot of
     *             arguments of which some are optional
     */
    @Deprecated
    public ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final boolean scheduled, final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                ipAddress, request, scheduled, retryCount, MessagePriority.DEFAULT.getPriority());
    }

    /**
     * Constructor with both a scheduled flag and a messagePriority. Only called
     * from builder
     */
    private ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final boolean scheduled, final int retryCount,
            final int messagePriority) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);

        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.ipAddress = ipAddress;

        this.messageData = request;
        this.scheduled = scheduled;
        this.retryCount = retryCount;

        this.messagePriority = messagePriority;
    }

    public static class Builder {
        private String domain;
        private String domainVersion;
        private String messageType;

        private String correlationUid;
        private String organisationIdentification;
        private String deviceIdentification;

        private String ipAddress;
        private Serializable request;
        private boolean scheduled;
        private int retryCount;

        private int messagePriority;

        public Builder domain(final String domain) {
            this.domain = domain;
            return this;
        }

        public Builder domainVersion(final String domainVersion) {
            this.domainVersion = domainVersion;
            return this;
        }

        public Builder messageType(final String messageType) {
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

        public Builder scheduled(final boolean scheduled) {
            this.scheduled = scheduled;
            return this;
        }

        public Builder retryCount(final int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder messagePriority(final int messagePriority) {
            this.messagePriority = messagePriority;
            return this;
        }

        public ProtocolRequestMessage build() {
            return new ProtocolRequestMessage(this.domain, this.domainVersion, this.messageType, this.correlationUid,
                    this.organisationIdentification, this.deviceIdentification, this.ipAddress, this.request,
                    this.scheduled, this.retryCount, this.messagePriority);
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

    @Override
    public String getIpAddress() {
        return this.ipAddress;
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
