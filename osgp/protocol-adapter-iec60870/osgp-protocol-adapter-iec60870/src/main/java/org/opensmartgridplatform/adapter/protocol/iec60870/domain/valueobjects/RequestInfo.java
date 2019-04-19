/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import java.io.Serializable;

import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * A value object, containing the relevant information of an incoming request:
 * <ul>
 * <li>Correlation Uid</li>
 * <li>Organisation Identification</li>
 * <li>Device Identification</li>
 * <li>Message Type</li>
 * <li>IP Address</li>
 * <li>Domain</li>
 * <li>Domain Version</li>
 * </ul>
 *
 */
public class RequestInfo {

    final Serializable messageData;
    final String domain;
    final String domainVersion;
    final String messageType;
    final String correlationUid;
    final String organisationIdentification;
    final String deviceIdentification;
    final String ipAddress;

    public RequestInfo(final Builder builder) {
        this.messageData = builder.messageData;
        this.domain = builder.domain;
        this.domainVersion = builder.domainVersion;
        this.messageType = builder.messageType;
        this.correlationUid = builder.correlationUid;
        this.organisationIdentification = builder.organisationIdentification;
        this.deviceIdentification = builder.deviceIdentification;
        this.ipAddress = builder.ipAddress;
    }

    public static class Builder {
        private Serializable messageData = null;
        private String domain = null;
        private String domainVersion = null;
        private String messageType = null;
        private String correlationUid = null;
        private String organisationIdentification = null;
        private String deviceIdentification = null;
        private String ipAddress = null;

        public Builder messageData(final Serializable messageData) {
            this.messageData = messageData;
            return this;
        }

        public Builder messageMetadata(final MessageMetadata messageMetadata) {
            this.domain = messageMetadata.getDomain();
            this.domainVersion = messageMetadata.getDomainVersion();
            this.messageType = messageMetadata.getMessageType();
            this.correlationUid = messageMetadata.getCorrelationUid();
            this.organisationIdentification = messageMetadata.getOrganisationIdentification();
            this.deviceIdentification = messageMetadata.getDeviceIdentification();
            this.ipAddress = messageMetadata.getIpAddress();
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

        public RequestInfo build() {
            return new RequestInfo(this);
        }

    }

    public static Builder newBuilder() {
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
}
