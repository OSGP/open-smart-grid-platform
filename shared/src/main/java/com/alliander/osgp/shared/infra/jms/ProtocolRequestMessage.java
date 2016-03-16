/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

public class ProtocolRequestMessage extends RequestMessage {

    private static final long serialVersionUID = -2282645261481872781L;

    private final String domain;
    private final String domainVersion;
    private final String messageType;
    private final Serializable messageData;
    private final boolean scheduled;
    private final int retryCount;

    public ProtocolRequestMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final String ipAddress, final Serializable request, final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                ipAddress, request, false, retryCount);
    }

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
}
