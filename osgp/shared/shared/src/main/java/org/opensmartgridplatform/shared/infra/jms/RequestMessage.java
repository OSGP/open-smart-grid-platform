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

public class RequestMessage implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8377090502244471258L;

  protected MessageMetadata messageMetadata;
  protected Serializable request;

  public RequestMessage(final CorrelationIds ids, final Serializable request) {
    this(
        ids.getCorrelationUid(),
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification(),
        null,
        request);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification) {
    this(correlationUid, organisationIdentification, deviceIdentification, null, null);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final Serializable request) {
    this(correlationUid, organisationIdentification, deviceIdentification, null, request);
  }

  public RequestMessage(
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final String ipAddress,
      final Serializable request) {
    this(
        new MessageMetadata.Builder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withIpAddress(ipAddress)
            .build(),
        request);
  }

  public RequestMessage(final MessageMetadata messageMetadata, final Serializable request) {
    this.messageMetadata = messageMetadata;
    this.request = request;
  }

  public MessageMetadata getMessageMetadata() {
    return this.messageMetadata;
  }

  public Serializable getRequest() {
    return this.request;
  }

  // Delegated getters for metadata

  public String getCorrelationUid() {
    return this.messageMetadata.getCorrelationUid();
  }

  public String getOrganisationIdentification() {
    return this.messageMetadata.getOrganisationIdentification();
  }

  public String getDeviceIdentification() {
    return this.messageMetadata.getDeviceIdentification();
  }

  public String getIpAddress() {
    return this.messageMetadata.getIpAddress();
  }
}
