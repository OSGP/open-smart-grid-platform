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
    private int retryCount;
    private int messagePriority;

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

    // scheduled and retryCount and messagePriority
    private ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled, final int retryCount, final int messagePriority) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject);
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
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
        private ResponseMessageResultType result;
        private OsgpException osgpException;
        private Serializable dataObject;
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

        public Builder messagePriority(final int messagePriority) {
            this.messagePriority = messagePriority;
            return this;
        }

        public ProtocolResponseMessage build() {
            return new ProtocolResponseMessage(this.domain, this.domainVersion, this.messageType, this.correlationUid,
                    this.organisationIdentification, this.deviceIdentification, this.result, this.osgpException,
                    this.dataObject, this.scheduled, this.retryCount, this.messagePriority);
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

    public int getMessagePriority() {
        return this.messagePriority;
    }

}
