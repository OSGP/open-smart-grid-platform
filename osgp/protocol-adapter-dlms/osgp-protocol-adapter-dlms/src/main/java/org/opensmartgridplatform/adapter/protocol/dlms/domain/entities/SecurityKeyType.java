// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.util.Arrays;
import org.opensmartgridplatform.ws.schema.core.secret.management.SecretType;

public enum SecurityKeyType {
  /** DLMS master key (Key Encryption Key) */
  E_METER_MASTER(SecretType.E_METER_MASTER_KEY),
  /** DLMS authentication key */
  E_METER_AUTHENTICATION(SecretType.E_METER_AUTHENTICATION_KEY),
  /** DLMS global unicast encryption key */
  E_METER_ENCRYPTION(SecretType.E_METER_ENCRYPTION_KEY_UNICAST),
  /** DLMS global broadcast encryption key */
  E_METER_ENCRYPTION_BROADCAST(SecretType.E_METER_ENCRYPTION_KEY_BROADCAST),
  /** M-Bus Default key */
  G_METER_MASTER(SecretType.G_METER_MASTER_KEY),
  /** M-Bus User key */
  G_METER_ENCRYPTION(SecretType.G_METER_ENCRYPTION_KEY),
  /** M-Bus Firmware update authentication key */
  G_METER_FIRMWARE_UPDATE_AUTHENTICATION(SecretType.G_METER_FIRMWARE_UPDATE_AUTHENTICATION_KEY),
  /** M-Bus optical port key */
  G_METER_OPTICAL_PORT_KEY(SecretType.G_METER_OPTICAL_PORT_KEY),
  /** Password (e.g. used as DLMS Low Level Security secret) */
  LLS_PASSWORD(SecretType.LLS_PASSWORD);

  private final SecretType secretType;

  SecurityKeyType(final SecretType secretType) {
    this.secretType = secretType;
  }

  public SecretType toSecretType() {
    return this.secretType;
  }

  public static SecurityKeyType fromSecretType(final SecretType secretType) {
    return Arrays.stream(SecurityKeyType.values())
        .filter(skt -> skt.secretType.equals(secretType))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException("Could not get value from secret type " + secretType));
  }
}
