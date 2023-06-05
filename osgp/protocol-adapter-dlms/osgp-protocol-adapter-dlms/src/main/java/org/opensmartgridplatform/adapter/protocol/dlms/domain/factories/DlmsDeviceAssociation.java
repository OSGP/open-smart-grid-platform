// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
