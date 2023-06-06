// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

import java.io.Serializable;
import lombok.Getter;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

@Getter
public class ResponseMessage implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -214808702310700742L;

  protected static final boolean DEFAULT_BYPASS_RETRY = false;

  private final String deviceIdentification;
  private final String organisationIdentification;
  private final String correlationUid;
  private final String messageType;
  private final int messagePriority;
  private final boolean scheduled;
  private final Long maxScheduleTime;
  private final boolean bypassRetry;

  private final RetryHeader retryHeader;

  private final ResponseMessageResultType result;
  private final OsgpException osgpException;
  private final Serializable dataObject;

  private final String topic;

  protected ResponseMessage(final Builder builder) {
    this.deviceIdentification = builder.deviceIdentification;
    this.organisationIdentification = builder.organisationIdentification;
    this.correlationUid = builder.correlationUid;
    this.messageType = builder.messageType;
    this.messagePriority = builder.messagePriority;
    this.scheduled = builder.scheduled;
    this.maxScheduleTime = builder.maxScheduleTime;
    this.bypassRetry = builder.bypassRetry;
    this.retryHeader = builder.retryHeader;
    this.result = builder.result;
    this.osgpException = builder.osgpException;
    this.dataObject = builder.dataObject;
    this.topic = builder.topic;
  }

  @Override
  public String toString() {
    return "ResponseMessage [messageType="
        + this.messageType
        + ", result="
        + this.result
        + ", osgpException="
        + this.osgpException
        + ", dataObject="
        + this.serializableToString(this.dataObject)
        + "]";
  }

  private String serializableToString(final Serializable dataObject) {
    if (dataObject == null) {
      return "";
    } else {
      final String stringValue = dataObject.toString();
      return stringValue.substring(0, Math.min(stringValue.length(), 40));
    }
  }

  public boolean bypassRetry() {
    return this.bypassRetry;
  }

  public MessageMetadata messageMetadata() {
    return MessageMetadata.newBuilder()
        .withDeviceIdentification(this.deviceIdentification)
        .withOrganisationIdentification(this.organisationIdentification)
        .withCorrelationUid(this.correlationUid)
        .withMessageType(this.messageType)
        .withMessagePriority(this.messagePriority)
        .withScheduled(this.scheduled)
        .withMaxScheduleTime(this.maxScheduleTime)
        .withBypassRetry(this.bypassRetry)
        .withTopic(this.topic)
        .build();
  }

  public static Builder newResponseMessageBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String messageType = null;
    private String correlationUid = null;
    private String organisationIdentification = null;
    private String deviceIdentification = null;
    private ResponseMessageResultType result = null;
    private OsgpException osgpException = null;
    private Serializable dataObject = null;
    private int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
    private boolean scheduled = false;
    private Long maxScheduleTime = null;
    private boolean bypassRetry = DEFAULT_BYPASS_RETRY;
    private RetryHeader retryHeader;
    private String topic = null;

    public Builder withMessageType(final String messageType) {
      this.messageType = messageType;
      return this;
    }

    public Builder withIds(final CorrelationIds ids) {
      this.organisationIdentification = ids.getOrganisationIdentification();
      this.deviceIdentification = ids.getDeviceIdentification();
      this.correlationUid = ids.getCorrelationUid();
      return this;
    }

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

    public Builder withResult(final ResponseMessageResultType result) {
      this.result = result;
      return this;
    }

    public Builder withOsgpException(final OsgpException osgpException) {
      this.osgpException = osgpException;
      return this;
    }

    public Builder withDataObject(final Serializable dataObject) {
      this.dataObject = dataObject;
      return this;
    }

    public Builder withMessagePriority(final int messagePriority) {
      this.messagePriority = messagePriority;
      return this;
    }

    public Builder withBypassRetry(final boolean bypassRetry) {
      this.bypassRetry = bypassRetry;
      return this;
    }

    public Builder withScheduled(final boolean scheduled) {
      this.scheduled = scheduled;
      return this;
    }

    public Builder withMaxScheduleTime(final Long maxScheduleTime) {
      this.maxScheduleTime = maxScheduleTime;
      return this;
    }

    public Builder withRetryHeader(final RetryHeader retryHeader) {
      this.retryHeader = retryHeader;
      return this;
    }

    public Builder withTopic(final String topic) {
      this.topic = topic;
      return this;
    }

    public Builder withMessageMetadata(final MessageMetadata messageMetadata) {
      this.deviceIdentification = messageMetadata.getDeviceIdentification();
      this.organisationIdentification = messageMetadata.getOrganisationIdentification();
      this.correlationUid = messageMetadata.getCorrelationUid();
      this.messageType = messageMetadata.getMessageType();
      this.messagePriority = messageMetadata.getMessagePriority();
      this.scheduled = messageMetadata.isScheduled();
      this.maxScheduleTime = messageMetadata.getMaxScheduleTime();
      this.bypassRetry = messageMetadata.isBypassRetry();
      this.retryHeader = new RetryHeader();
      this.topic = messageMetadata.getTopic();
      return this;
    }

    public ResponseMessage build() {
      return new ResponseMessage(this);
    }
  }
}
