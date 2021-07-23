/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

@Getter
public class RequestMessageMetadata {

  @Identification private final String deviceIdentification;
  @Identification private final String organisationIdentification;
  private final MessageType messageType;
  private final DeviceFunction deviceFunction;
  private final int messagePriority;
  private final Long scheduleTime;
  private final Long maxScheduleTime;
  private final boolean bypassRetry;

  private RequestMessageMetadata(final Builder builder) {
    this.deviceIdentification = builder.deviceIdentification;
    this.organisationIdentification = builder.organisationIdentification;
    this.messageType = builder.messageType;
    this.deviceFunction = builder.deviceFunction;
    this.messagePriority = builder.messagePriority;
    this.scheduleTime = builder.scheduleTime;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
  }

  public MessageMetadata newMessageMetadata(final String correlationUid) {
    return new MessageMetadata.Builder(
            correlationUid,
            this.getOrganisationIdentification(),
            this.getDeviceIdentification(),
            this.getMessageType().name())
        .withMessagePriority(this.getMessagePriority())
        .withScheduleTime(this.getScheduleTime())
        .withMaxScheduleTime(this.getMaxScheduleTime())
        .withBypassRetry(this.isBypassRetry())
        .build();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String deviceIdentification = null;
    private String organisationIdentification = null;
    private MessageType messageType = null;
    private DeviceFunction deviceFunction = null;
    private int messagePriority = 0;
    private Long scheduleTime = null;
    private Long maxScheduleTime = null;
    private boolean bypassRetry = false;

    public RequestMessageMetadata build() {
      return new RequestMessageMetadata(this);
    }

    public Builder withDeviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder withOrganisationIdentification(final String organisationIdentification) {
      this.organisationIdentification = organisationIdentification;
      return this;
    }

    public Builder withMessageType(final MessageType messageType) {
      this.messageType = messageType;
      return this;
    }

    public Builder withDeviceFunction(final DeviceFunction deviceFunction) {
      this.deviceFunction = deviceFunction;
      return this;
    }

    public Builder withMessagePriority(final String messagePriority) {
      this.messagePriority = MessagePriorityEnum.getMessagePriority(messagePriority);
      return this;
    }

    public Builder withScheduleTime(final String scheduleTime) {
      if (StringUtils.isNotEmpty(scheduleTime)) {
        this.scheduleTime = Long.parseLong(scheduleTime);
      }
      return this;
    }

    public Builder withMaxScheduleTime(final String maxScheduleTime) {
      if (StringUtils.isNotEmpty(maxScheduleTime)) {
        this.maxScheduleTime = Long.parseLong(maxScheduleTime);
      }
      return this;
    }

    public Builder withBypassRetry(final String bypassRetry) {
      this.bypassRetry = Boolean.parseBoolean(bypassRetry);
      return this;
    }
  }
}
