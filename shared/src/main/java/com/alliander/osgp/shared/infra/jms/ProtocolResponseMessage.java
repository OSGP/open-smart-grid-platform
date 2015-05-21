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
    private int retryCount;

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, false, retryCount);
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, osgpException, dataObject, false);
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled, final int retryCount) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject);
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.scheduled = scheduled;
        this.retryCount = retryCount;
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException, final Serializable dataObject,
            final boolean scheduled) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject);
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.scheduled = scheduled;
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

}
