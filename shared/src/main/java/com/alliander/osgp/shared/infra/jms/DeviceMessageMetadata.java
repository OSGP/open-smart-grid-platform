/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public class DeviceMessageMetadata {
    private final String deviceIdentification;
    private final String organisationIdentification;
    private final String correlationUid;
    private final String messageType;
    private final int messagePriority;
    private final Long scheduleTime;

    public DeviceMessageMetadata(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final int messagePriority, final Long scheduleTime) {
        this.deviceIdentification = deviceIdentification;
        this.organisationIdentification = organisationIdentification;
        this.correlationUid = correlationUid;
        this.messageType = messageType;
        this.messagePriority = messagePriority;
        this.scheduleTime = scheduleTime;
    }

    public DeviceMessageMetadata(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final int messagePriority) {
        this(deviceIdentification, organisationIdentification, correlationUid, messageType, messagePriority,
                (Long) null);
    }

    public DeviceMessageMetadata(final Message message) throws JMSException {
        this(message.getStringProperty(Constants.DEVICE_IDENTIFICATION), message
                .getStringProperty(Constants.ORGANISATION_IDENTIFICATION), message.getJMSCorrelationID(), message
                .getJMSType(), message.getJMSPriority(), message.propertyExists(Constants.SCHEDULE_TIME) ? message
                .getLongProperty(Constants.SCHEDULE_TIME) : null);
    }

    public DeviceMessageMetadata(final ProtocolResponseMessage message) {
        this(message.getDeviceIdentification(), message.getOrganisationIdentification(), message.getCorrelationUid(),
                message.getMessageType(), message.getMessagePriority());
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

    /**
     *
     * @return the scheduling time or null if not applicable
     */
    public Long getScheduleTime() {
        return this.scheduleTime;
    }

    @Override
    public String toString() {
        return "DeviceMessageMetadata [deviceIdentification=" + this.deviceIdentification
                + ", organisationIdentification=" + this.organisationIdentification + ", correlationUid="
                + this.correlationUid + ", messageType=" + this.messageType + ", messagePriority="
                + this.messagePriority + ", scheduleTime=" + this.scheduleTime + "]";
    }

}
