/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.io.Serializable;

/**
 * A value object, containing all data of an incoming ObjectMessage.
 */
public class RequestMessageData {

    final Serializable messageData;
    final String domain;
    final String domainVersion;
    final String messageType;
    final int retryCount;
    final boolean isScheduled;
    final String correlationUid;
    final String organisationIdentification;
    final String deviceIdentification;
    final String ipAddress;
    final int messagePriority;
    final Long scheduleTime;

    public RequestMessageData(final Serializable messageData, final String domain, final String domainVersion,
            final String messageType, final int retryCount, final boolean isScheduled, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification) {
        this.messageData = messageData;
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.retryCount = retryCount;
        this.isScheduled = isScheduled;
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.ipAddress = null;
        this.messagePriority = 0;
        this.scheduleTime = null;
    }

    public RequestMessageData(final Serializable messageData, final String domain, final String domainVersion,
            final String messageType, final int retryCount, final Boolean isScheduled, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String ipAddress,
            final int messagePriority, final Long scheduleTime) {
        this.messageData = messageData;
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageType = messageType;
        this.retryCount = retryCount;
        this.isScheduled = isScheduled;
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.ipAddress = ipAddress;
        this.messagePriority = messagePriority;
        this.scheduleTime = scheduleTime;
    }

    public Serializable getMessageData() {
        return this.messageData;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public boolean isScheduled() {
        return this.isScheduled;
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

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }

    public Long getScheduleTime() {
        return this.scheduleTime;
    }
}
