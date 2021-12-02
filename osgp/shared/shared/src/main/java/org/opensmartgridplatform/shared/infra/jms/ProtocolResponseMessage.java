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
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ProtocolResponseMessage extends ResponseMessage {

  /** Serial Version UID. */
  private static final long serialVersionUID = -7720502773704936266L;

  private final String domain;
  private final String domainVersion;

  private final int retryCount;

  private ProtocolResponseMessage(final Builder builder) {
    super(builder.superBuilder);
    this.domain = builder.domain;
    this.domainVersion = builder.domainVersion;
    this.retryCount = builder.retryCount;
  }

  public static class Builder {

    private final ResponseMessage.Builder superBuilder =
        ResponseMessage.newResponseMessageBuilder();

    private String domain;
    private String domainVersion;
    private int retryCount;

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.superBuilder.withMessageMetadata(messageMetadata);
      return this;
    }

    public Builder correlationUid(final String correlationUid) {
      this.superBuilder.withCorrelationUid(correlationUid);
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

    public Builder result(final ResponseMessageResultType result) {
      this.superBuilder.withResult(result);
      return this;
    }

    public Builder osgpException(final OsgpException osgpException) {
      this.superBuilder.withOsgpException(osgpException);
      return this;
    }

    public Builder dataObject(final Serializable dataObject) {
      this.superBuilder.withDataObject(dataObject);
      return this;
    }

    public Builder scheduled(final boolean scheduled) {
      this.superBuilder.withScheduled(scheduled);
      return this;
    }

    public Builder retryCount(final int retryCount) {
      this.retryCount = retryCount;
      return this;
    }

    public Builder retryHeader(final RetryHeader retryHeader) {
      this.superBuilder.withRetryHeader(retryHeader);
      return this;
    }

    public ProtocolResponseMessage build() {
      return new ProtocolResponseMessage(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
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
}
