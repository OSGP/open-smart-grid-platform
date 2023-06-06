// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

/**
 * A value object, containing the relevant information of an incoming request:
 *
 * <ul>
 *   <li>Correlation Uid
 *   <li>Organisation Identification
 *   <li>Device Identification
 *   <li>Message Type
 *   <li>IP Address
 *   <li>{@link DomainInfo}
 * </ul>
 */
public class RequestMetadata {

  final Serializable messageData;
  final String messageType;
  final String correlationUid;
  final String organisationIdentification;
  final String deviceIdentification;
  final String ipAddress;
  final DomainInfo domainInfo;

  public RequestMetadata(final Builder builder) {
    this.messageData = builder.messageData;
    this.messageType = builder.messageType;
    this.correlationUid = builder.correlationUid;
    this.organisationIdentification = builder.organisationIdentification;
    this.deviceIdentification = builder.deviceIdentification;
    this.ipAddress = builder.ipAddress;
    this.domainInfo = builder.domainInfo;
  }

  public static class Builder {
    private Serializable messageData = null;
    private DomainInfo domainInfo = null;
    private String messageType = null;
    private String correlationUid = null;
    private String organisationIdentification = null;
    private String deviceIdentification = null;
    private String ipAddress = null;

    public Builder messageData(final Serializable messageData) {
      this.messageData = messageData;
      return this;
    }

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.domainInfo =
          new DomainInfo(messageMetadata.getDomain(), messageMetadata.getDomainVersion());
      this.messageType = messageMetadata.getMessageType();
      this.correlationUid = messageMetadata.getCorrelationUid();
      this.organisationIdentification = messageMetadata.getOrganisationIdentification();
      this.deviceIdentification = messageMetadata.getDeviceIdentification();
      this.ipAddress = messageMetadata.getIpAddress();
      return this;
    }

    public Builder domainInfo(final DomainInfo domainInfo) {
      this.domainInfo = domainInfo;
      return this;
    }

    public Builder messageType(final String messageType) {
      this.messageType = messageType;
      return this;
    }

    public Builder correlationUid(final String correlationUid) {
      this.correlationUid = correlationUid;
      return this;
    }

    public Builder organisationIdentification(final String organisationIdentification) {
      this.organisationIdentification = organisationIdentification;
      return this;
    }

    public Builder deviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder ipAddress(final String ipAddress) {
      this.ipAddress = ipAddress;
      return this;
    }

    public RequestMetadata build() {
      return new RequestMetadata(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Serializable getMessageData() {
    return this.messageData;
  }

  public DomainInfo getDomainInfo() {
    return this.domainInfo;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }
}
