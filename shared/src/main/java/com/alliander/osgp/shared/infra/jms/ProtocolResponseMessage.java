package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

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
            final ResponseMessageResultType result, final String description, final Serializable dataObject,
            final int retryCount) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, description, dataObject, false, retryCount);
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final String errorMessage, final Serializable dataObject) {
        this(domain, domainVersion, messageType, correlationUid, organisationIdentification, deviceIdentification,
                result, errorMessage, dataObject, false);
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final String description, final Serializable dataObject,
            final boolean scheduled, final int retryCount) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, description, dataObject);
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.scheduled = scheduled;
        this.retryCount = retryCount;
    }

    public ProtocolResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final String errorMessage, final Serializable dataObject,
            final boolean scheduled) {
        super(correlationUid, organisationIdentification, deviceIdentification, result, errorMessage, dataObject);
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
