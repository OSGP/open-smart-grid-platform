/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

/**
 * Device association, as described in Section 2.1 of the DSMR/SMR P3 standard: "The logical device
 * can have 3 associations: Public client (client Id 16), Management client (client Id 1) and
 * Pre-established client (client Id 102)."
 */
public enum DlmsDeviceAssociation {
  PUBLIC_CLIENT(16),
  MANAGEMENT_CLIENT(1);

  private final int clientId;

  DlmsDeviceAssociation(final int clientId) {
    this.clientId = clientId;
  }

  public int getClientId() {
    return this.clientId;
  }
}
