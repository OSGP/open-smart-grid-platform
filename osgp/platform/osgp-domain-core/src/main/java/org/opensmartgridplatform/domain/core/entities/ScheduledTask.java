/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

@Entity
@Table(name = "scheduled_task")
public class ScheduledTask extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -77027372763748726L;

    @Column(length = 255)
    private String domain;

    @Column(length = 255)
    private String domainVersion;

    @Column(length = 255)
    private String correlationUid;

    @Column(length = 255)
    private String organisationIdentification;

    @Column(length = 255)
    private String deviceIdentification;

    @Column(length = 255)
    private String messageType;

    @Type(type = "java.io.Serializable")
    private Serializable messageData;

    @Column(length = 255)
    private Timestamp scheduledTime;

    @Column(name = "status")
    private ScheduledTaskStatusType status;

    @Column(name = "error_log", length = 255)
    private String errorLog;

    @Column(name = "messagepriority")
    private Integer messagePriority;

    @Column(name = "retry")
    private int retry;

    @SuppressWarnings("unused")
    private ScheduledTask() {

    }

    public ScheduledTask(final DeviceMessageMetadata deviceMessageMetadata, final String domain,
            final String domainVersion, final Serializable messageData, final Timestamp scheduledTime) {

        this.correlationUid = deviceMessageMetadata.getCorrelationUid();
        this.organisationIdentification = deviceMessageMetadata.getOrganisationIdentification();
        this.deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        this.messageType = deviceMessageMetadata.getMessageType();
        this.messagePriority = deviceMessageMetadata.getMessagePriority();
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.messageData = messageData;
        this.scheduledTime = (Timestamp) scheduledTime.clone();
        this.status = ScheduledTaskStatusType.NEW;
        this.retry = 0;
    }

    // public static

    public String getDomain() {
        return this.domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    public String getCorrelationId() {
        return this.correlationUid;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getErrorLog() {
        return this.errorLog;
    }

    public Timestamp getscheduledTime() {
        return (Timestamp) this.scheduledTime.clone();
    }

    public Serializable getMessageData() {
        return this.messageData;
    }

    public Integer getMessagePriority() {
        return this.messagePriority;
    }

    public int getRetry() {
        return this.retry;
    }

    public ScheduledTaskStatusType getStatus() {
        return this.status;
    }

    public void setPending() {
        this.status = ScheduledTaskStatusType.PENDING;
    }

    public void setFailed(final String errorLog) {
        this.status = ScheduledTaskStatusType.FAILED;
        this.errorLog = errorLog;
    }

    public void setComplete() {
        this.status = ScheduledTaskStatusType.COMPLETE;
    }

    public void retryOn(final Date retryTime) {
        this.retry++;
        this.scheduledTime = new Timestamp(retryTime.getTime());
        this.status = ScheduledTaskStatusType.RETRY;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledTask)) {
            return false;
        }
        final ScheduledTask scheduledTask = (ScheduledTask) o;
        return Objects.equals(this.correlationUid, scheduledTask.correlationUid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.correlationUid);
    }
}
