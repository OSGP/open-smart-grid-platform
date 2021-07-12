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

  private static final long serialVersionUID = -6951175556510738951L;

  private final String domain;
  private final String domainVersion;
  private final String messageType;
  private final Serializable messageData;
  private final boolean scheduled;
  private final int retryCount;
  private int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
  private final boolean bypassRetry;

  private ProtocolRequestMessage(final Builder builder) {
    super(
        builder.messageMetadata.getCorrelationUid(),
        builder.messageMetadata.getOrganisationIdentification(),
        builder.messageMetadata.getDeviceIdentification(),
        builder.ipAddress,
        builder.request);

    this.domain = builder.domain;
    this.domainVersion = builder.domainVersion;
    this.messageData = builder.request;
    this.scheduled = builder.scheduled;
    this.retryCount = builder.retryCount;

    this.messageType = builder.messageMetadata.getMessageType();
    this.messagePriority = builder.messageMetadata.getMessagePriority();
    this.bypassRetry = builder.messageMetadata.isBypassRetry();
  }

  public static class Builder {
    private String domain;
    private String domainVersion;
    private String ipAddress;
    private Serializable request;
    private boolean scheduled;
    private int retryCount;

    private MessageMetadata messageMetadata;

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.messageMetadata = messageMetadata;
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

    public Builder retryCount(final int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public ProtocolRequestMessage build() {
      return new ProtocolRequestMessage(this);
    }
  }

  public String getDomain() {
    return this.domain;
  }

  public int getRetryCount() {
    return this.retryCount;
  }

  public String getDomainVersion() {
    return this.domainVersion;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public Serializable getMessageData() {
    return this.messageData;
  }

  public boolean isScheduled() {
    return this.scheduled;
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }

  public boolean bypassRetry() {
    return this.bypassRetry;
  }
}
