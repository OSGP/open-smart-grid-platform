/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.io.Serializable;

/**
 * A value object, containing all data of an incoming ObjectMessage.
 */
public class RequestMessageData {

    final Serializable messageData;
    final String domain;
    final String domainVersion;
    final String messageType;
    final int retryCount;
    final boolean isScheduled;
    final String correlationUid;
    final String organisationIdentification;
    final String deviceIdentification;
    final String ipAddress;
    final int messagePriority;
    final Long scheduleTime;

    public RequestMessageData(final Builder builder) {
        this.messageData = builder.messageData;
        this.domain = builder.domain;
        this.domainVersion = builder.domainVersion;
        this.messageType = builder.messageType;
        this.retryCount = builder.retryCount;
        this.isScheduled = builder.isScheduled;
        this.correlationUid = builder.correlationUid;
        this.organisationIdentification = builder.organisationIdentification;
        this.deviceIdentification = builder.deviceIdentification;
        this.ipAddress = builder.ipAddress;
        this.messagePriority = builder.messagePriority;
        this.scheduleTime = builder.scheduleTime;
    }

    public static class Builder {
        private Serializable messageData = null;
        private String domain = null;
        private String domainVersion = null;
        private String messageType = null;
        private int retryCount = 0;
        private boolean isScheduled = false;
        private String correlationUid = null;
        private String organisationIdentification = null;
        private String deviceIdentification = null;
        private String ipAddress = null;
        private int messagePriority = 0;
        private Long scheduleTime = 0L;

        public Builder withMessageData(final Serializable messageData) {
            this.messageData = messageData;
            return this;
        }

        public Builder withDomain(final String domain) {
            this.domain = domain;
            return this;
        }

        public Builder withDomainVersion(final String domainVersion) {
            this.domainVersion = domainVersion;
            return this;
        }

        public Builder withMessageType(final String messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder withRetryCount(final int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder withIsScheduled(final boolean isScheduled) {
            this.isScheduled = isScheduled;
            return this;
        }

        public Builder withCorrelationUid(final String correlationUid) {
            this.correlationUid = correlationUid;
            return this;
        }

        public Builder withOrganisationIdentification(final String organisationIdentification) {
            this.organisationIdentification = organisationIdentification;
            return this;
        }

        public Builder withDeviceIdentification(final String deviceIdentification) {
            this.deviceIdentification = deviceIdentification;
            return this;
        }

        public Builder withIpAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder withMessagePriority(final int messagePriority) {
            this.messagePriority = messagePriority;
            return this;
        }

        public Builder withScheduleTime(final Long scheduleTime) {
            this.scheduleTime = scheduleTime;
            return this;
        }

        public RequestMessageData build() {
            return new RequestMessageData(this);
        }

    }

    public static Builder newRequestMessageDataBuilder() {
        return new Builder();
    }

    public Serializable getMessageData() {
        return this.messageData;
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

    public int getRetryCount() {
        return this.retryCount;
    }

    public boolean isScheduled() {
        return this.isScheduled;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

    public Long getScheduleTime() {
        return this.scheduleTime;
    }
}
