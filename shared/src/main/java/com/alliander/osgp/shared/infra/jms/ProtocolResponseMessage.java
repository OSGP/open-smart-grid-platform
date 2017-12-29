/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.exceptionhandling.OsgpException;

public class ProtocolResponseMessage extends ResponseMessage {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7720502773704936266L;

    private final String domain;
    private final String domainVersion;
    private final String messageType;
    private final boolean scheduled;

    private final int retryCount;
    private final RetryHeader retryHeader;

    private ProtocolResponseMessage(final Builder builder) {
        super(builder.superBuilder);
        this.domain = builder.domain;
        this.domainVersion = builder.domainVersion;
        this.messageType = builder.messageType;
        this.scheduled = builder.scheduled;
        this.retryCount = builder.retryCount;
        this.retryHeader = builder.retryHeader;
    }

    public static class Builder {

        private ResponseMessage.Builder superBuilder = ResponseMessage.newResponseMessageBuilder();

        private String domain;
        private String domainVersion;
        private String messageType;
        private boolean scheduled;
        private int retryCount;
        private RetryHeader retryHeader;

        public Builder deviceMessageMetadata(final DeviceMessageMetadata deviceMessageMetadata) {
            this.superBuilder.deviceMessageMetadata(deviceMessageMetadata);
            this.messageType = deviceMessageMetadata.getMessageType();
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

        public Builder result(final ResponseMessageResultType result) {
            this.superBuilder.result(result);
            return this;
        }

        public Builder osgpException(final OsgpException osgpException) {
            this.superBuilder.osgpException(osgpException);
            return this;
        }

        public Builder dataObject(final Serializable dataObject) {
            this.superBuilder.dataObject(dataObject);
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

        public Builder retryHeader(final RetryHeader retryHeader) {
            this.retryHeader = retryHeader;
            return this;
        }

        public ProtocolResponseMessage build() {
            return new ProtocolResponseMessage(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public boolean isScheduled() {
        return this.scheduled;
    }

    public RetryHeader getRetryHeader() {
        return this.retryHeader;
    }
}
