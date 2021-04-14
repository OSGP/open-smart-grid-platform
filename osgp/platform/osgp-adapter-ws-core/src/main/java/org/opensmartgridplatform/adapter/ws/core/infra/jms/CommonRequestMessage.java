/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.infra.jms;

import java.io.Serializable;
import org.joda.time.DateTime;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class CommonRequestMessage extends RequestMessage {
  /** Generated serial version */
  private static final long serialVersionUID = 6094774737635965756L;

  private final MessageType messageType;
  private final DateTime scheduleTime;
  private final Integer messagePriority;

  private CommonRequestMessage(
      final DeviceMessageMetadata deviceMessageMetadata,
      final String ipAddress,
      final Serializable request) {
    super(
        deviceMessageMetadata.getCorrelationUid(),
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification(),
        ipAddress,
        request);
    this.messageType = MessageType.valueOf(deviceMessageMetadata.getMessageType());
    this.messagePriority = deviceMessageMetadata.getMessagePriority();
    if (deviceMessageMetadata.getScheduleTime() == null) {
      this.scheduleTime = null;
    } else {
      this.scheduleTime = new DateTime(deviceMessageMetadata.getScheduleTime());
    }
  }

  public MessageType getMessageType() {
    return this.messageType;
  }

  public DateTime getScheduleTime() {
    return this.scheduleTime;
  }

  public Integer getMessagePriority() {
    return this.messagePriority;
  }

  public static class Builder {
    private DeviceMessageMetadata deviceMessageMetadata;
    private String ipAddress;
    private Serializable request;

    public Builder() {
      // empty constructor
    }

    public Builder deviceMessageMetadata(final DeviceMessageMetadata deviceMessageMetadata) {
      this.deviceMessageMetadata = deviceMessageMetadata;
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

    public CommonRequestMessage build() {
      return new CommonRequestMessage(this.deviceMessageMetadata, this.ipAddress, this.request);
    }
  }
}
