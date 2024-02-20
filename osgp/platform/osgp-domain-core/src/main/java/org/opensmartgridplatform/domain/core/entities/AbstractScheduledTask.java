// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduledTaskStatusType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/** Abstract scheduled task used for mapping fields used by subclasses. */
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

  @Column(length = 255)
  protected Timestamp maxScheduleTime;

  @Column(name = "status")
  protected ScheduledTaskStatusType status;

  @Column(name = "error_log", length = 255)
  protected String errorLog;

  @Column(name = "messagepriority")
  protected Integer messagePriority;

  @Column(name = "retry")
  protected int retry;

  @Column(name = "device_model_code", length = 1279)
  protected String deviceModelCode;

  AbstractScheduledTask() {
    // Default empty constructor for Hibernate.
  }

  protected AbstractScheduledTask(
      final MessageMetadata messageMetadata,
      final String domain,
      final String domainVersion,
      final Timestamp scheduledTime,
      final Timestamp maxScheduleTime) {

    this.correlationUid = messageMetadata.getCorrelationUid();
    this.organisationIdentification = messageMetadata.getOrganisationIdentification();
    this.deviceIdentification = messageMetadata.getDeviceIdentification();
    this.messageType = messageMetadata.getMessageType();
    this.messagePriority = messageMetadata.getMessagePriority();
    this.deviceModelCode = messageMetadata.getDeviceModelCode();
    this.domain = domain;
    this.domainVersion = domainVersion;
    this.scheduledTime = (Timestamp) scheduledTime.clone();
    this.maxScheduleTime = maxScheduleTime != null ? (Timestamp) maxScheduleTime.clone() : null;
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

  public String getDeviceModelCode() {
    return this.deviceModelCode;
  }

  public String getErrorLog() {
    return this.errorLog;
  }

  public Timestamp getScheduledTime() {
    return this.scheduledTime == null ? null : (Timestamp) this.scheduledTime.clone();
  }

  public Timestamp getMaxScheduleTime() {
    return this.maxScheduleTime == null ? null : (Timestamp) this.maxScheduleTime.clone();
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
