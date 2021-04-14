/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.messaging;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

public class CoreMessageMetadata {

  private String correlationUid;
  private String domain;
  private String domainVersion;
  private String messageType;
  private String organisationIdentification;
  private String deviceIdentification;
  private String ipAddress;
  private int retryCount;
  private int messagePriority;

  @Override
  public String toString() {
    return String.format(
        "CoreMessageMetadata[correlationUid=%s, domain=%s, domainVersion=%s, messageType=%s, organisation=%s, device=%s, ipAddress=%s, retryCount=%d, messagePriority=%d]",
        this.correlationUid,
        this.domain,
        this.domainVersion,
        this.messageType,
        this.organisationIdentification,
        this.deviceIdentification,
        this.ipAddress,
        this.retryCount,
        this.messagePriority);
  }

  /**
   * By using the ObjectMessage, the attributes of the data container are set
   *
   * @param message
   * @throws JMSException
   */
  public void handleMessage(final ObjectMessage message) throws JMSException {
    this.correlationUid = message.getJMSCorrelationID();
    this.domain = message.getStringProperty(Constants.DOMAIN);
    this.domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
    this.messageType = message.getJMSType();
    this.organisationIdentification =
        message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
    this.deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
    this.ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
    this.retryCount = message.getIntProperty(Constants.RETRY_COUNT);
    this.messagePriority = message.getJMSPriority();
  }

  public int getMessagePriority() {
    return this.messagePriority;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  public void setCorrelationUid(final String correlationUid) {
    this.correlationUid = correlationUid;
  }

  public String getDomain() {
    return this.domain;
  }

  public void setDomain(final String domain) {
    this.domain = domain;
  }

  public String getDomainVersion() {
    return this.domainVersion;
  }

  public void setDomainVersion(final String domainVersion) {
    this.domainVersion = domainVersion;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public void setMessageType(final String messageType) {
    this.messageType = messageType;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public void setOrganisationIdentification(final String organisationIdentification) {
    this.organisationIdentification = organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public int getRetryCount() {
    return this.retryCount;
  }

  public void setRetryCount(final int retryCount) {
    this.retryCount = retryCount;
  }

  public DeviceMessageMetadata asDeviceMessageMetadata() {
    return new DeviceMessageMetadata(
        this.getDeviceIdentification(),
        this.getOrganisationIdentification(),
        this.getCorrelationUid(),
        this.getMessageType(),
        this.getMessagePriority());
  }
}
