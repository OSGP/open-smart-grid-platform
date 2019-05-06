/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

/**
 * A value object, containing the relevant information for sending responses to
 * the platform:
 * <ul>
 * <li>Correlation Uid</li>
 * <li>Device Identification</li>
 * <li>Organisation Identification</li>
 * <li>Message Type</li>
 * <li>{@link DomainInfo}</li>
 * </ul>
 *
 */
public class ResponseMetadata {

    private final String correlationUid;
    private final String deviceIdentification;
    private final String organisationIdentification;
    private final String messageType;
    private final DomainInfo domainInfo;

    private ResponseMetadata(final Builder builder) {
        this.correlationUid = builder.correlationUid;
        this.deviceIdentification = builder.deviceIdentification;
        this.organisationIdentification = builder.organisationIdentification;
        this.messageType = builder.messageType;
        this.domainInfo = builder.domainInfo;
    }

    public static ResponseMetadata from(final RequestMetadata requestMetadata) {
        return new Builder().withCorrelationUid(requestMetadata.correlationUid)
                .withDeviceIdentification(requestMetadata.getDeviceIdentification())
                .withOrganisationIdentification(requestMetadata.getOrganisationIdentification())
                .withMessageType(requestMetadata.getMessageType()).withDomainInfo(requestMetadata.getDomainInfo())
                .build();
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public DomainInfo getDomainInfo() {
        return this.domainInfo;
    }

    public static class Builder {

        private String correlationUid;
        private String deviceIdentification;
        private String organisationIdentification;
        private String messageType;
        private DomainInfo domainInfo;

        public Builder() {
        }

        public Builder(final ResponseMetadata responseMetadata) {
            this.correlationUid = responseMetadata.getCorrelationUid();
            this.deviceIdentification = responseMetadata.getDeviceIdentification();
            this.organisationIdentification = responseMetadata.getOrganisationIdentification();
            this.messageType = responseMetadata.getMessageType();
            this.domainInfo = responseMetadata.getDomainInfo();
        }

        public Builder withCorrelationUid(final String correlationUid) {
            this.correlationUid = correlationUid;
            return this;
        }

        public Builder withDeviceIdentification(final String deviceIdentification) {
            this.deviceIdentification = deviceIdentification;
            return this;
        }

        public Builder withOrganisationIdentification(final String organisationIdentification) {
            this.organisationIdentification = organisationIdentification;
            return this;
        }

        public Builder withMessageType(final String messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder withDomainInfo(final DomainInfo domainInfo) {
            this.domainInfo = domainInfo;
            return this;
        }

        public ResponseMetadata build() {
            return new ResponseMetadata(this);
        }
    }

}
