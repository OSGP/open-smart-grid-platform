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

public class ResponseMessage implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -214808702310700742L;

    protected static final boolean DEFAULT_BYPASS_RETRY = false;

    private final String correlationUid;
    private final String organisationIdentification;
    private final String deviceIdentification;
    private final ResponseMessageResultType result;
    private final OsgpException osgpException;
    private final Serializable dataObject;
    private final int messagePriority;
    private final boolean bypassRetry;

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result,
            final OsgpException osgpException, final Serializable dataObject, final int messagePriority) {
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.result = result;
        this.osgpException = osgpException;
        this.dataObject = dataObject;
        this.messagePriority = messagePriority;
        this.bypassRetry = DEFAULT_BYPASS_RETRY;
    }

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result,
            final OsgpException osgpException, final Serializable dataObject) {
        this(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject,
                MessagePriorityEnum.DEFAULT.getPriority());
    }

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result,
            final OsgpException osgpException) {
        this(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null,
                MessagePriorityEnum.DEFAULT.getPriority());
    }

    public ResponseMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final ResponseMessageResultType result,
            final OsgpException osgpException, final int messagePriority) {
        this(correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null,
                messagePriority);
    }

    protected ResponseMessage(final Builder builder) {
        this.correlationUid = builder.correlationUid;
        this.organisationIdentification = builder.organisationIdentification;
        this.deviceIdentification = builder.deviceIdentification;
        this.result = builder.result;
        this.osgpException = builder.osgpException;
        this.dataObject = builder.dataObject;
        this.messagePriority = builder.messagePriority;
        this.bypassRetry = builder.bypassRetry;
    }

    public static class Builder {

        private String correlationUid;
        private String organisationIdentification;
        private String deviceIdentification;
        private ResponseMessageResultType result;
        private OsgpException osgpException;
        private Serializable dataObject;
        private int messagePriority;
        private boolean bypassRetry;

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

        public Builder messagePriority(final int messagePriority) {
            this.messagePriority = messagePriority;
            return this;
        }

        public Builder bypassRetry(final boolean bypassRetry) {
            this.bypassRetry = bypassRetry;
            return this;
        }

        public Builder deviceMessageMetadata(final DeviceMessageMetadata deviceMessageMetadata) {
            this.correlationUid = deviceMessageMetadata.getCorrelationUid();
            this.organisationIdentification = deviceMessageMetadata.getOrganisationIdentification();
            this.deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
            this.messagePriority = deviceMessageMetadata.getMessagePriority();
            this.bypassRetry = deviceMessageMetadata.bypassRetry();
            return this;
        }

        public ResponseMessage build() {
            return new ResponseMessage(this);
        }
    }

    public static Builder newResponseMessageBuilder() {
        return new Builder();
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

    public ResponseMessageResultType getResult() {
        return this.result;
    }

    public OsgpException getOsgpException() {
        return this.osgpException;
    }

    public Serializable getDataObject() {
        return this.dataObject;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

    public boolean bypassRetry() {
        return this.bypassRetry;
    }

}
