/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.Message;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class MessageMetadata implements Serializable {
  /** Generated serial version uid */
  private static final long serialVersionUID = 5481771135195782979L;

  private String deviceIdentification;
  private String organisationIdentification;
  private String correlationUid;
  private String messageType;
  private String domain;
  private String domainVersion;
  @Setter private String ipAddress;
  private int messagePriority;
  private boolean scheduled;
  private Long scheduleTime;
  private Long maxScheduleTime;
  private boolean bypassRetry;
  private int retryCount;
  private int jmsxDeliveryCount;

  private MessageMetadata() {
    // Default private constructor.
  }

  private MessageMetadata(final Builder builder) {
    this.deviceIdentification = builder.deviceIdentification;
    this.organisationIdentification = builder.organisationIdentification;
    this.correlationUid = builder.correlationUid;
    this.messageType = builder.messageType;
    this.domain = builder.domain;
    this.domainVersion = builder.domainVersion;
    this.ipAddress = builder.ipAddress;
    this.messagePriority = builder.messagePriority;
    this.scheduled = builder.scheduled;
    this.scheduleTime = builder.scheduleTime;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
    this.retryCount = builder.retryCount;
    this.jmsxDeliveryCount = builder.jmsxDeliveryCount;
  }

  public static MessageMetadata fromMessage(final Message message) throws JMSException {

    final MessageMetadata metadata = new MessageMetadata();
    metadata.correlationUid = message.getJMSCorrelationID();
    metadata.messageType = message.getJMSType();
    metadata.messagePriority = message.getJMSPriority();

    metadata.deviceIdentification =
        metadata.getStringProperty(message, Constants.DEVICE_IDENTIFICATION, StringUtils.EMPTY);
    metadata.organisationIdentification =
        metadata.getStringProperty(
            message, Constants.ORGANISATION_IDENTIFICATION, StringUtils.EMPTY);

    metadata.domain = metadata.getStringProperty(message, Constants.DOMAIN, StringUtils.EMPTY);
    metadata.domainVersion =
        metadata.getStringProperty(message, Constants.DOMAIN_VERSION, StringUtils.EMPTY);

    metadata.ipAddress =
        metadata.getStringProperty(message, Constants.IP_ADDRESS, StringUtils.EMPTY);

    metadata.scheduleTime = metadata.getLongProperty(message, Constants.SCHEDULE_TIME, null);
    metadata.maxScheduleTime = metadata.getLongProperty(message, Constants.MAX_SCHEDULE_TIME, null);
    metadata.scheduled = metadata.getBooleanProperty(message, Constants.IS_SCHEDULED, false);

    metadata.retryCount = metadata.getIntProperty(message, Constants.RETRY_COUNT, 0);
    metadata.bypassRetry = metadata.getBooleanProperty(message, Constants.BYPASS_RETRY, false);
    metadata.jmsxDeliveryCount = metadata.getIntProperty(message, Constants.DELIVERY_COUNT, 0);

    return metadata;
  }

  public <T extends Message> void applyTo(final T message) throws JMSException {
    message.setJMSCorrelationID(this.correlationUid);
    message.setJMSType(this.messageType);
    message.setJMSPriority(this.messagePriority);

    if (this.deviceIdentification != null) {
      message.setStringProperty(Constants.DEVICE_IDENTIFICATION, this.deviceIdentification);
    }

    if (this.organisationIdentification != null) {
      message.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION, this.organisationIdentification);
    }
    message.setBooleanProperty(Constants.BYPASS_RETRY, this.bypassRetry);
    if (this.scheduleTime != null) {
      message.setLongProperty(Constants.SCHEDULE_TIME, this.scheduleTime);
    }
    if (this.maxScheduleTime != null) {
      message.setLongProperty(Constants.MAX_SCHEDULE_TIME, this.maxScheduleTime);
    }
  }

  public static MessageMetadata.Builder newMessageMetadataBuilder() {
    return new Builder();
  }

  private String getStringProperty(
      final Message message, final String name, final String defaultValue) throws JMSException {
    return message.propertyExists(name) ? message.getStringProperty(name) : defaultValue;
  }

  private Long getLongProperty(final Message message, final String name, final Long defaultValue)
      throws JMSException {
    if (message.propertyExists(name)) {
      return message.getLongProperty(name);
    } else {
      return defaultValue;
    }
  }

  private int getIntProperty(final Message message, final String name, final int defaultValue)
      throws JMSException {
    return message.propertyExists(name) ? message.getIntProperty(name) : defaultValue;
  }

  private boolean getBooleanProperty(
      final Message message, final String name, final boolean defaultValue) throws JMSException {
    return message.propertyExists(name) ? message.getBooleanProperty(name) : defaultValue;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public static class Builder {
    private String correlationUid;
    private String organisationIdentification;
    private String deviceIdentification;
    private String messageType;

    private String domain = StringUtils.EMPTY;
    private String domainVersion = StringUtils.EMPTY;
    private String ipAddress = StringUtils.EMPTY;
    private int messagePriority = 0;
    private Long scheduleTime = null;
    private Long maxScheduleTime = null;
    private boolean scheduled = false;
    private int retryCount = 0;
    private boolean bypassRetry = false;
    private int jmsxDeliveryCount = 0;

    public Builder(final MessageMetadata otherMetadata) {
      this.correlationUid = otherMetadata.getCorrelationUid();
      this.organisationIdentification = otherMetadata.getOrganisationIdentification();
      this.deviceIdentification = otherMetadata.getDeviceIdentification();
      this.messageType = otherMetadata.getMessageType();
      this.domain = otherMetadata.getDomain();
      this.domainVersion = otherMetadata.getDomainVersion();
      this.ipAddress = otherMetadata.getIpAddress();
      this.messagePriority = otherMetadata.getMessagePriority();
      this.scheduled = otherMetadata.isScheduled();
      this.scheduleTime = otherMetadata.getScheduleTime();
      this.maxScheduleTime = otherMetadata.getMaxScheduleTime();
      this.bypassRetry = otherMetadata.isBypassRetry();
      this.retryCount = otherMetadata.getRetryCount();
      this.jmsxDeliveryCount = otherMetadata.getJmsxDeliveryCount();
    }

    public Builder(
        final String correlationUid,
        final String organisationIdentification,
        final String deviceIdentification,
        final String messageType) {
      this.correlationUid = correlationUid;
      this.organisationIdentification = organisationIdentification;
      this.deviceIdentification = deviceIdentification;
      this.messageType = messageType;
    }

    public Builder() {}

    public Builder withCorrelationUid(final String correlationUid) {
      this.correlationUid = correlationUid;
      return this;
    }

    public Builder withOrganisationIdentification(final String organisationIdentification) {
      this.organisationIdentification = organisationIdentification;
      return this;
    }

    public Builder withDeviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder withMessageType(final String messageType) {
      this.messageType = messageType;
      return this;
    }

    public Builder withDomain(final String domain) {
      this.domain = domain;
      return this;
    }

    public Builder withDomainVersion(final String domainVersion) {
      this.domainVersion = domainVersion;
      return this;
    }

    public Builder withIpAddress(final String ipAddress) {
      this.ipAddress = ipAddress;
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

    public Builder withMaxScheduleTime(final Long maxScheduleTime) {
      this.maxScheduleTime = maxScheduleTime;
      return this;
    }

    public Builder withScheduled(final boolean scheduled) {
      this.scheduled = scheduled;
      return this;
    }

    public Builder withRetryCount(final int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public Builder withBypassRetry(final boolean bypassRetry) {
      this.bypassRetry = bypassRetry;
      return this;
    }

    public Builder withJmsxDeliveryCount(final int jmsxDeliveryCount) {
      this.jmsxDeliveryCount = jmsxDeliveryCount;
      return this;
    }

    public MessageMetadata build() {
      return new MessageMetadata(this);
    }
  }
}
