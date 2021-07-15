/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import org.opensmartgridplatform.shared.validation.Identification;

/**
 * Set of ID's used to track things like device operations passing through the platform, i.e. the
 * organisation identification, the device identification and the correlation UID.
 */
public class CorrelationIds {
  @Identification private final String organisationIdentification;

  @Identification private final String deviceIdentification;

  private final String correlationUid;

  public CorrelationIds(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid) {
    this.organisationIdentification = organisationIdentification;
    this.deviceIdentification = deviceIdentification;
    this.correlationUid = correlationUid;
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public String getCorrelationUid() {
    return this.correlationUid;
  }

  @Override
  public String toString() {
    return "CorrelationIds [organisationIdentification="
        + this.organisationIdentification
        + ", deviceIdentification="
        + this.deviceIdentification
        + ", correlationUid="
        + this.correlationUid
        + "]";
  }
}
