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
 *
 */
public class ResponseInfo {

    private final String correlationUid;
    private final String deviceIdentification;
    private final String organisationIdentification;
    private final String messageType;
    private final DomainInfo domainInfo;

    private ResponseInfo(final Builder builder) {
        this.correlationUid = builder.correlationUid;
        this.deviceIdentification = builder.deviceIdentification;
        this.organisationIdentification = builder.organisationIdentification;
        this.messageType = builder.messageType;
        this.domainInfo = builder.domainInfo;
    }

    public static ResponseInfo from(final RequestInfo requestInfo) {
        return new Builder().withCorrelationUid(requestInfo.correlationUid)
                .withDeviceIdentification(requestInfo.getDeviceIdentification())
                .withOrganisationIdentification(requestInfo.getOrganisationIdentification())
                .withMessageType(requestInfo.getMessageType())
                .withDomainInfo(new DomainInfo(requestInfo.getDomain(), requestInfo.getDomainVersion())).build();
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

        public Builder(final ResponseInfo responseInfo) {
            this.correlationUid = responseInfo.getCorrelationUid();
            this.deviceIdentification = responseInfo.getDeviceIdentification();
            this.organisationIdentification = responseInfo.getOrganisationIdentification();
            this.messageType = responseInfo.getMessageType();
            this.domainInfo = responseInfo.getDomainInfo();
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

        public ResponseInfo build() {
            return new ResponseInfo(this);
        }
    }

}
