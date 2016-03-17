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

public class DeviceMessageMetadata {
    private String deviceIdentification;
    private String organisationIdentification;
    private String correlationUid;
    private String messageType;
    private int messagePriority;

    public DeviceMessageMetadata(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final int messagePriority) {
        this.deviceIdentification = deviceIdentification;
        this.organisationIdentification = organisationIdentification;
        this.correlationUid = correlationUid;
        this.messageType = messageType;
        this.messagePriority = messagePriority;
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

}
