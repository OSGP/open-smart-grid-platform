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
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

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

    /**
     * @deprecated Use builder in stead
     */
    @Deprecated
    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, false, retryCount, MessagePriorityEnum.DEFAULT.getPriority());

    }

    /**
     * @deprecated Use builder in stead
     */
    @Deprecated
    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, false, 0, MessagePriorityEnum.DEFAULT.getPriority());
    }

    /**
     * @deprecated Use builder in stead
     */
    @Deprecated
    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, scheduled, 0, MessagePriorityEnum.DEFAULT.getPriority());

    }

    /**
     * @deprecated Use builder in stead
     */
    @Deprecated
    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled, final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, scheduled, retryCount, MessagePriorityEnum.DEFAULT.getPriority());
    }

    /**
     * @deprecated Use builder in stead
     */
    @Deprecated
    private ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled, final int retryCount, final int messagePriority) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject,
                messagePriority);

        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.scheduled = scheduled;
        this.retryCount = retryCount;
        this.retryHeader = new RetryHeader();
    }

    // scheduled and retryCount and messagePriority
    private ProtocolResponseMessage(final DeviceMessageMetadata deviceMessageMetadata, final String domain,
            final String domainVersion, final ResponseMessageResultType result, final OsgpException osgpException,
            final Serializable dataObject, final boolean scheduled, final int retryCount, final RetryHeader retryHeader) {
        super(deviceMessageMetadata.getCorrelationUid(), deviceMessageMetadata.getOrganisationIdentification(),
                deviceMessageMetadata.getDeviceIdentification(), result, osgpException, dataObject,
                deviceMessageMetadata.getMessagePriority());
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = deviceMessageMetadata.getMessageType();
        this.scheduled = scheduled;
        this.retryCount = retryCount;
        this.retryHeader = retryHeader;
    }

    public static class Builder {

        private DeviceMessageMetadata deviceMessageMetadata;

        private String domain;
        private String domainVersion;
        private ResponseMessageResultType result;
        private OsgpException osgpException;
        private Serializable dataObject;
        private boolean scheduled;
        private int retryCount;
        private RetryHeader retryHeader;

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

        public Builder result(final ResponseMessageResultType result) {
            this.result = result;
            return this;
        }

        public Builder osgpException(final OsgpException osgpException) {
            this.osgpException = osgpException;
            return this;
        }

        public Builder dataObject(final Serializable dataObject) {
            this.dataObject = dataObject;
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
            return new ProtocolResponseMessage(this.deviceMessageMetadata, this.domain, this.domainVersion,
                    this.result, this.osgpException, this.dataObject, this.scheduled, this.retryCount, this.retryHeader);
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

    public boolean isScheduled() {
        return this.scheduled;
    }

    public RetryHeader getRetryHeader() {
        return this.retryHeader;
    }
}
