// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms;

import java.io.Serializable;
import org.joda.time.DateTime;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class PublicLightingRequestMessage extends RequestMessage {

  /** Generated Serial Version UID. */
  private static final long serialVersionUID = 8981706563226676098L;

  private final MessageType messageType;
  private final DateTime scheduleTime;
  private final Integer messagePriority;

  private PublicLightingRequestMessage(
      final MessageMetadata messageMetadata, final String ipAddress, final Serializable request) {
    super(
        messageMetadata.getCorrelationUid(),
        messageMetadata.getOrganisationIdentification(),
        messageMetadata.getDeviceIdentification(),
        ipAddress,
        messageMetadata.getBaseTransceiverStationId(),
        messageMetadata.getCellId(),
        request);
    this.messageType = MessageType.valueOf(messageMetadata.getMessageType());
    this.messagePriority = messageMetadata.getMessagePriority();
    if (messageMetadata.getScheduleTime() == null) {
      this.scheduleTime = null;
    } else {
      this.scheduleTime = new DateTime(messageMetadata.getScheduleTime());
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
    private MessageMetadata messageMetadata;
    private String ipAddress;
    private Serializable request;

    public Builder() {
      // empty constructor
    }

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.messageMetadata = messageMetadata;
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

    public PublicLightingRequestMessage build() {
      return new PublicLightingRequestMessage(this.messageMetadata, this.ipAddress, this.request);
    }
  }
}
