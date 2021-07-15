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

  protected String correlationUid;
  protected String organisationIdentification;
  protected String deviceIdentification;
  protected String ipAddress;
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
    this.correlationUid = correlationUid;
    this.organisationIdentification = organisationIdentification;
    this.deviceIdentification = deviceIdentification;
    this.ipAddress = ipAddress;
    this.request = request;
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

  public Serializable getRequest() {
    return this.request;
  }
}
