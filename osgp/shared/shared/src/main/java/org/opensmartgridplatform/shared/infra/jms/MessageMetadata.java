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
  private String ipAddress;
  private Integer baseTransceiverStationId;
  private Integer cellId;
  private int messagePriority;
  private boolean scheduled;
  private Long scheduleTime;
  private Long maxScheduleTime;
  private boolean bypassRetry;
  private int retryCount;
  private int jmsxDeliveryCount;
  private String topic;

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
    this.baseTransceiverStationId = builder.baseTransceiverStationId;
    this.cellId = builder.cellId;
    this.messagePriority = builder.messagePriority;
    this.scheduled = builder.scheduled;
    this.scheduleTime = builder.scheduleTime;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
    this.retryCount = builder.retryCount;
    this.jmsxDeliveryCount = builder.jmsxDeliveryCount;
    this.topic = builder.topic;
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

    metadata.baseTransceiverStationId =
        metadata.getIntProperty(message, Constants.BASE_TRANSCEIVER_STATION_ID, null);
    metadata.cellId = metadata.getIntProperty(message, Constants.CELL_ID, null);

    metadata.scheduleTime = metadata.getLongProperty(message, Constants.SCHEDULE_TIME, null);
    metadata.maxScheduleTime = metadata.getLongProperty(message, Constants.MAX_SCHEDULE_TIME, null);
    metadata.scheduled = metadata.getBooleanProperty(message, Constants.IS_SCHEDULED, false);

    metadata.retryCount = metadata.getIntProperty(message, Constants.RETRY_COUNT, 0);
    metadata.bypassRetry = metadata.getBooleanProperty(message, Constants.BYPASS_RETRY, false);
    metadata.jmsxDeliveryCount = metadata.getIntProperty(message, Constants.DELIVERY_COUNT, 0);

    metadata.topic = metadata.getStringProperty(message, Constants.TOPIC, StringUtils.EMPTY);

    return metadata;
  }

  public void applyTo(final Message message) throws JMSException {

    message.setJMSCorrelationID(this.correlationUid);
    message.setJMSType(this.messageType);
    message.setJMSPriority(this.messagePriority);

    if (StringUtils.isNotBlank(this.deviceIdentification)) {
      message.setStringProperty(Constants.DEVICE_IDENTIFICATION, this.deviceIdentification);
    }
    if (StringUtils.isNotBlank(this.organisationIdentification)) {
      message.setStringProperty(
          Constants.ORGANISATION_IDENTIFICATION, this.organisationIdentification);
    }

    if (StringUtils.isNotBlank(this.domain)) {
      message.setStringProperty(Constants.DOMAIN, this.domain);
    }
    if (StringUtils.isNotBlank(this.domainVersion)) {
      message.setStringProperty(Constants.DOMAIN_VERSION, this.domainVersion);
    }

    if (StringUtils.isNotBlank(this.ipAddress)) {
      message.setStringProperty(Constants.IP_ADDRESS, this.ipAddress);
    }

    if (this.baseTransceiverStationId != null) {
      message.setIntProperty(Constants.BASE_TRANSCEIVER_STATION_ID, this.baseTransceiverStationId);
    }
    if (this.cellId != null) {
      message.setIntProperty(Constants.CELL_ID, this.cellId);
    }

    if (this.scheduleTime != null) {
      message.setLongProperty(Constants.SCHEDULE_TIME, this.scheduleTime);
    }
    if (this.maxScheduleTime != null) {
      message.setLongProperty(Constants.MAX_SCHEDULE_TIME, this.maxScheduleTime);
    }
    message.setBooleanProperty(Constants.IS_SCHEDULED, this.scheduled);

    message.setIntProperty(Constants.RETRY_COUNT, this.retryCount);
    message.setBooleanProperty(Constants.BYPASS_RETRY, this.bypassRetry);

    if (StringUtils.isNotBlank(this.topic)) {
      message.setStringProperty(Constants.TOPIC, this.topic);
    }

    /*
     * Not setting the jmsxDeliveryCount as int property named Constants.DELIVERY_COUNT, because
     * this is not some metadata to be transferred over to new JMS messages. This delivery count is
     * incremented by the message queue environment when a message is re-delivered to a consumer.
     */
  }

  public static MessageMetadata.Builder newBuilder() {
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

  private Integer getIntProperty(
      final Message message, final String name, final Integer defaultValue) throws JMSException {
    if (message.propertyExists(name)) {
      return message.getIntProperty(name);
    } else {
      return defaultValue;
    }
  }

  private boolean getBooleanProperty(
      final Message message, final String name, final boolean defaultValue) throws JMSException {
    return message.propertyExists(name) ? message.getBooleanProperty(name) : defaultValue;
  }

  public Builder builder() {
    return new Builder(this);
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
    private Integer baseTransceiverStationId = null;
    private Integer cellId = null;
    private int messagePriority = 0;
    private Long scheduleTime = null;
    private Long maxScheduleTime = null;
    private boolean scheduled = false;
    private int retryCount = 0;
    private boolean bypassRetry = false;
    private int jmsxDeliveryCount = 0;
    private String topic = StringUtils.EMPTY;

    public Builder(final MessageMetadata otherMetadata) {
      this.correlationUid = otherMetadata.getCorrelationUid();
      this.organisationIdentification = otherMetadata.getOrganisationIdentification();
      this.deviceIdentification = otherMetadata.getDeviceIdentification();
      this.messageType = otherMetadata.getMessageType();
      this.domain = otherMetadata.getDomain();
      this.domainVersion = otherMetadata.getDomainVersion();
      this.ipAddress = otherMetadata.getIpAddress();
      this.baseTransceiverStationId = otherMetadata.getBaseTransceiverStationId();
      this.cellId = otherMetadata.getCellId();
      this.messagePriority = otherMetadata.getMessagePriority();
      this.scheduled = otherMetadata.isScheduled();
      this.scheduleTime = otherMetadata.getScheduleTime();
      this.maxScheduleTime = otherMetadata.getMaxScheduleTime();
      this.bypassRetry = otherMetadata.isBypassRetry();
      this.retryCount = otherMetadata.getRetryCount();
      this.jmsxDeliveryCount = otherMetadata.getJmsxDeliveryCount();
      this.topic = otherMetadata.getTopic();
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

    public Builder withNetworkSegmentIds(
        final Integer baseTransceiverStationId, final Integer cellId) {

      this.baseTransceiverStationId = baseTransceiverStationId;
      this.cellId = cellId;
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

    public Builder withTopic(final String topic) {
      this.topic = topic;
      return this;
    }

    public MessageMetadata build() {
      return new MessageMetadata(this);
    }
  }
}
