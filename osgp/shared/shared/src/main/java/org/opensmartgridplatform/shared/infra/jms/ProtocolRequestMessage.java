// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

public class ProtocolRequestMessage extends RequestMessage {

  private static final long serialVersionUID = -6951175556510738951L;

  private final String messageType;
  private final String domain;
  private final String domainVersion;
  private final int messagePriority;
  private final boolean scheduled;
  private final Long scheduleTime;
  private final Long maxScheduleTime;
  private final boolean bypassRetry;
  private final int retryCount;

  private ProtocolRequestMessage(final Builder builder) {
    super(
        builder.correlationUid,
        builder.organisationIdentification,
        builder.deviceIdentification,
        builder.ipAddress,
        builder.baseTransceiverStationId,
        builder.cellId,
        builder.request);

    this.messageType = builder.messageType;
    this.domain = builder.domain;
    this.domainVersion = builder.domainVersion;
    this.messagePriority = builder.messagePriority;
    this.scheduled = builder.scheduled;
    this.scheduleTime = builder.scheduleTime;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
    this.retryCount = builder.retryCount;
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

  public boolean isScheduled() {
    return this.scheduled;
  }

  public Long getScheduleTime() {
    return this.scheduleTime;
  }

  public Long getMaxScheduleTime() {
    return this.maxScheduleTime;
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }

  public boolean bypassRetry() {
    return this.bypassRetry;
  }

  @Override
  public MessageMetadata messageMetadata() {
    return super.messageMetadata()
        .builder()
        .withMessageType(this.messageType)
        .withDomain(this.domain)
        .withDomainVersion(this.domainVersion)
        .withMessagePriority(this.messagePriority)
        .withScheduled(this.scheduled)
        .withMaxScheduleTime(this.maxScheduleTime)
        .withBypassRetry(this.bypassRetry)
        .withRetryCount(this.retryCount)
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
    private String domain;
    private String domainVersion;
    private String ipAddress;
    private Integer baseTransceiverStationId;
    private Integer cellId;
    private int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    private boolean scheduled;
    private Long scheduleTime;
    private Long maxScheduleTime;
    private boolean bypassRetry;
    private int retryCount;
    private Serializable request;

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.deviceIdentification = messageMetadata.getDeviceIdentification();
      this.organisationIdentification = messageMetadata.getOrganisationIdentification();
      this.correlationUid = messageMetadata.getCorrelationUid();
      this.messageType = messageMetadata.getMessageType();
      this.domain = messageMetadata.getDomain();
      this.domainVersion = messageMetadata.getDomainVersion();
      this.ipAddress = messageMetadata.getNetworkAddress();
      this.baseTransceiverStationId = messageMetadata.getBaseTransceiverStationId();
      this.cellId = messageMetadata.getCellId();
      this.messagePriority = messageMetadata.getMessagePriority();
      this.scheduled = messageMetadata.isScheduled();
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

    public Builder networkSegementIds(
        final Integer baseTransceiverStationId, final Integer cellId) {
      this.baseTransceiverStationId = baseTransceiverStationId;
      this.cellId = cellId;
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
