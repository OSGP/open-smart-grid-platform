// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.domain;

public enum SecretType {
  E_METER_MASTER_KEY,
  E_METER_AUTHENTICATION_KEY,
  E_METER_ENCRYPTION_KEY_UNICAST,
  E_METER_ENCRYPTION_KEY_BROADCAST,
  G_METER_MASTER_KEY,
  G_METER_ENCRYPTION_KEY,
  G_METER_FIRMWARE_UPDATE_AUTHENTICATION_KEY,
  G_METER_OPTICAL_PORT_KEY,
  PPP_PASSWORD,

  LLS_PASSWORD
}
