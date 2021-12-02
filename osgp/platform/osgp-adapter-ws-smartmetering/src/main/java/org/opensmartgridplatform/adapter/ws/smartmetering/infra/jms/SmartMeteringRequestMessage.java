/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

  private static final long serialVersionUID = 8978488633831083383L;

  private final String messageType;
  private final Integer messagePriority;
  private final Long scheduleTime;
  private final Long maxScheduleTime;
  private final boolean bypassRetry;

  private SmartMeteringRequestMessage(final Builder builder) {
    super(
        builder.correlationUid,
        builder.organisationIdentification,
        builder.deviceIdentification,
        builder.request);
    this.messageType = builder.messageType;
    this.messagePriority = builder.messagePriority;
    this.scheduleTime = builder.scheduleTime;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
  }

  public Integer getMessagePriority() {
    return this.messagePriority;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public Long getScheduleTime() {
    return this.scheduleTime;
  }

  public boolean bypassRetry() {
    return this.bypassRetry;
  }

  @Override
  public MessageMetadata messageMetadata() {
    return super.messageMetadata()
        .builder()
        .withMessageType(this.messageType)
        .withMessagePriority(this.messagePriority)
        .withScheduleTime(this.scheduleTime)
        .withMaxScheduleTime(this.maxScheduleTime)
        .withBypassRetry(this.bypassRetry)
        .build();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String deviceIdentification;
    private String organisationIdentification;
    private String correlationUid;
    private String messageType;
    private Integer messagePriority;
    private Long scheduleTime;
    private Long maxScheduleTime;
    private boolean bypassRetry;
    private Serializable request = null;

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.deviceIdentification = messageMetadata.getDeviceIdentification();
      this.organisationIdentification = messageMetadata.getOrganisationIdentification();
      this.correlationUid = messageMetadata.getCorrelationUid();
      this.messageType = messageMetadata.getMessageType();
      this.messagePriority = messageMetadata.getMessagePriority();
      this.scheduleTime = messageMetadata.getScheduleTime();
      this.maxScheduleTime = messageMetadata.getMaxScheduleTime();
      this.bypassRetry = messageMetadata.isBypassRetry();
      return this;
    }

    public Builder request(final Serializable request) {
      this.request = request;
      return this;
    }

    public SmartMeteringRequestMessage build() {
      return new SmartMeteringRequestMessage(this);
    }
  }
}
