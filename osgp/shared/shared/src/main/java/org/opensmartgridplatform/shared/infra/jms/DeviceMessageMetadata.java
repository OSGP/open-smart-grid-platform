/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;

public class DeviceMessageMetadata {

  private static final int EMPTY_MESSAGE_PRIORITY = 0;

  private final String deviceIdentification;
  private final String organisationIdentification;
  private final String correlationUid;
  private final String messageType;
  private final int messagePriority;
  private final Long scheduleTime;
  private boolean bypassRetry;

  public DeviceMessageMetadata(final MessageMetadata metadata) {
    this.deviceIdentification = metadata.getDeviceIdentification();
    this.organisationIdentification = metadata.getOrganisationIdentification();
    this.correlationUid = metadata.getCorrelationUid();
    this.messageType = metadata.getMessageType();
    this.messagePriority = metadata.getMessagePriority();
    this.scheduleTime = metadata.getScheduleTime();
    this.bypassRetry = metadata.isBypassRetry();
  }

  public DeviceMessageMetadata(
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType) {
    this(
        deviceIdentification,
        organisationIdentification,
        correlationUid,
        messageType,
        EMPTY_MESSAGE_PRIORITY);
  }

  public DeviceMessageMetadata(
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority) {

    this(
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType)
            .withMessagePriority(messagePriority));
  }

  public DeviceMessageMetadata(
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority,
      final Long scheduleTime,
      final boolean bypassRetry) {

    this(
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry));
  }

  public DeviceMessageMetadata(
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority,
      final Long scheduleTime) {

    this(
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime));
  }

  public DeviceMessageMetadata(final Message message) throws JMSException {

    this(
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification(message.getStringProperty(Constants.DEVICE_IDENTIFICATION))
            .withOrganisationIdentification(
                message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION))
            .withCorrelationUid(message.getJMSCorrelationID())
            .withMessageType(message.getJMSType())
            .withMessagePriority(message.getJMSPriority())
            .withScheduleTime(
                message.propertyExists(Constants.SCHEDULE_TIME)
                    ? message.getLongProperty(Constants.SCHEDULE_TIME)
                    : null)
            .withBypassRetry(
                message.propertyExists(Constants.BYPASS_RETRY)
                    ? message.getBooleanProperty(Constants.BYPASS_RETRY)
                    : false));
  }

  public DeviceMessageMetadata(final ProtocolResponseMessage message) {
    this(
        DeviceMessageMetadata.newBuilder()
            .withDeviceIdentification(message.getDeviceIdentification())
            .withOrganisationIdentification(message.getOrganisationIdentification())
            .withCorrelationUid(message.getCorrelationUid())
            .withMessageType(message.getMessageType())
            .withMessagePriority(message.getMessagePriority()));
  }

  private DeviceMessageMetadata(final Builder builder) {
    this.deviceIdentification = builder.deviceIdentification;
    this.organisationIdentification = builder.organisationIdentification;
    this.correlationUid = builder.correlationUid;
    this.messageType = builder.messageType;
    this.messagePriority = builder.messagePriority;
    this.scheduleTime = builder.scheduleTime;
    this.bypassRetry = builder.bypassRetry;
  }

  public static class Builder {

    private String deviceIdentification = null;
    private String organisationIdentification = null;
    private String correlationUid = null;
    private String messageType = null;
    private int messagePriority = 0;
    private Long scheduleTime = null;
    private boolean bypassRetry = false;

    public DeviceMessageMetadata build() {
      return new DeviceMessageMetadata(this);
    }

    public Builder withDeviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder withOrganisationIdentification(final String organisationIdentification) {
      this.organisationIdentification = organisationIdentification;
      return this;
    }

    public Builder withCorrelationUid(final String correlationUid) {
      this.correlationUid = correlationUid;
      return this;
    }

    public Builder withMessageType(final String messageType) {
      this.messageType = messageType;
      return this;
    }

    public Builder withMessagePriority(final int messagePriority) {
      this.messagePriority = messagePriority;
      return this;
    }

    public Builder withScheduleTime(final Long scheduleTime) {
      this.scheduleTime = scheduleTime;
      return this;
    }

    public Builder withBypassRetry(final boolean bypassRetry) {
      this.bypassRetry = bypassRetry;
      return this;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
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

  /** @return the scheduling time or null if not applicable */
  public Long getScheduleTime() {
    return this.scheduleTime;
  }

  public void setBypassRetry(final boolean bypassRetry) {
    this.bypassRetry = bypassRetry;
  }

  public boolean bypassRetry() {
    return this.bypassRetry;
  }

  public boolean isScheduled() {
    return this.scheduleTime != null && this.scheduleTime > 0;
  }

  @Override
  public String toString() {
    return "DeviceMessageMetadata [deviceIdentification="
        + this.deviceIdentification
        + ", organisationIdentification="
        + this.organisationIdentification
        + ", correlationUid="
        + this.correlationUid
        + ", messageType="
        + this.messageType
        + ", messagePriority="
        + this.messagePriority
        + ", scheduleTime="
        + this.scheduleTime
        + ", bypassRetry="
        + this.bypassRetry
        + "]";
  }
}
