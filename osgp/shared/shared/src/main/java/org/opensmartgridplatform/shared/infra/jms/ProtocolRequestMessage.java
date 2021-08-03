/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class ProtocolRequestMessage extends RequestMessage {

  private static final long serialVersionUID = 1385410110395599481L;

  private ProtocolRequestMessage(final Builder builder) {
    super(builder.messageMetadata(), builder.request);
  }

  public String getDomain() {
    return this.messageMetadata.getDomain();
  }

  public int getRetryCount() {
    return this.messageMetadata.getRetryCount();
  }

  public String getDomainVersion() {
    return this.messageMetadata.getDomainVersion();
  }

  public String getMessageType() {
    return this.messageMetadata.getMessageType();
  }

  public boolean isScheduled() {
    return this.messageMetadata.isScheduled();
  }

  public Long getMaxScheduleTime() {
    return this.messageMetadata.getMaxScheduleTime();
  }

  public int getMessagePriority() {
    return this.messageMetadata.getMessagePriority();
  }

  public boolean bypassRetry() {
    return this.messageMetadata.isBypassRetry();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String deviceIdentification;
    private String organisationIdentification;
    private String correlationUid;
    private String messageType;
    private String domain;
    private String domainVersion;
    private String ipAddress;
    private int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    private boolean scheduled;
    private Long scheduleTime;
    private Long maxScheduleTime;
    private boolean bypassRetry;
    private int retryCount;

    private Serializable request;

    private MessageMetadata messageMetadata() {
      return MessageMetadata.newBuilder()
          .withDeviceIdentification(this.deviceIdentification)
          .withOrganisationIdentification(this.organisationIdentification)
          .withCorrelationUid(this.correlationUid)
          .withMessageType(this.messageType)
          .withDomain(this.domain)
          .withDomainVersion(this.domainVersion)
          .withIpAddress(this.ipAddress)
          .withMessagePriority(this.messagePriority)
          .withScheduled(this.scheduled)
          .withScheduleTime(this.scheduleTime)
          .withMaxScheduleTime(this.maxScheduleTime)
          .withBypassRetry(this.bypassRetry)
          .withRetryCount(this.retryCount)
          .build();
    }

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.deviceIdentification = messageMetadata.getDeviceIdentification();
      this.organisationIdentification = messageMetadata.getOrganisationIdentification();
      this.correlationUid = messageMetadata.getCorrelationUid();
      this.messageType = messageMetadata.getMessageType();
      this.domain = messageMetadata.getDomain();
      this.domainVersion = messageMetadata.getDomainVersion();
      this.ipAddress = messageMetadata.getIpAddress();
      this.messagePriority = messageMetadata.getMessagePriority();
      this.scheduled = messageMetadata.isScheduled();
      this.scheduleTime = messageMetadata.getScheduleTime();
      this.maxScheduleTime = messageMetadata.getMaxScheduleTime();
      this.bypassRetry = messageMetadata.isBypassRetry();
      this.retryCount = messageMetadata.getRetryCount();
      return this;
    }

    public Builder domain(final String domain) {
      this.domain = domain;
      return this;
    }

    public Builder domainVersion(final String domainVersion) {
      this.domainVersion = domainVersion;
      return this;
    }

    public Builder ipAddress(final String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public Builder request(final Serializable request) {
      this.request = request;
      return this;
    }

    public Builder scheduled(final boolean scheduled) {
      this.scheduled = scheduled;
      return this;
    }

    public Builder maxScheduleTime(final Long maxScheduleTime) {
      this.maxScheduleTime = maxScheduleTime;
      return this;
    }

    public Builder retryCount(final int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public ProtocolRequestMessage build() {
      return new ProtocolRequestMessage(this);
    }
  }
}
