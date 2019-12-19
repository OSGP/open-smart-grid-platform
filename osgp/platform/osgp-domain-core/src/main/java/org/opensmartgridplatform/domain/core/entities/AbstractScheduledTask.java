/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

/**
 * Abstract scheduled task used for mapping fields used by subclasses.
 */
@MappedSuperclass
public abstract class AbstractScheduledTask extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ERROR_MESSAGE_LENGTH = 255;

    @Column(length = 255)
    protected String domain;

    @Column(length = 255)
    protected String domainVersion;

    @Column(length = 255)
    protected String correlationUid;

    @Column(length = 255)
    protected String organisationIdentification;

    @Column(length = 255)
    protected String deviceIdentification;

    @Column(length = 255)
    protected String messageType;

    @Column(length = 255)
    protected Timestamp scheduledTime;

    @Column(name = "status")
    protected ScheduledTaskStatusType status;

    @Column(name = "error_log", length = 255)
    protected String errorLog;

    @Column(name = "messagepriority")
    protected Integer messagePriority;

    @Column(name = "retry")
    protected int retry;

    AbstractScheduledTask() {
        // Default empty constructor for Hibernate.
    }

    protected AbstractScheduledTask(final DeviceMessageMetadata deviceMessageMetadata, final String domain,
            final String domainVersion, final Timestamp scheduledTime) {

        this.correlationUid = deviceMessageMetadata.getCorrelationUid();
        this.organisationIdentification = deviceMessageMetadata.getOrganisationIdentification();
        this.deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
        this.messageType = deviceMessageMetadata.getMessageType();
        this.messagePriority = deviceMessageMetadata.getMessagePriority();
        this.domain = domain;
        this.domainVersion = domainVersion;
        this.scheduledTime = (Timestamp) scheduledTime.clone();
        this.status = ScheduledTaskStatusType.NEW;
        this.retry = 0;
    }

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
        this.errorLog = StringUtils.left(errorLog, MAX_ERROR_MESSAGE_LENGTH);
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
        if (!(o instanceof AbstractScheduledTask)) {
            return false;
        }
        final AbstractScheduledTask scheduledTask = (AbstractScheduledTask) o;
        return Objects.equals(this.correlationUid, scheduledTask.correlationUid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.correlationUid);
    }
}
