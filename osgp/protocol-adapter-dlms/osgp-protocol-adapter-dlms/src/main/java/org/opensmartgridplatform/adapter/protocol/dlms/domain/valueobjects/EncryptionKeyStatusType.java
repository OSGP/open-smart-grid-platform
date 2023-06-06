// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.valueobjects;

public enum EncryptionKeyStatusType {
  NO_ENCRYPTION_KEY(0),
  ENCRYPTION_KEY_SET(1),
  ENCRYPTION_KEY_TRANSFERRED(2),
  ENCRYPTION_KEY_SET_AND_TRANSFERRED(3),
  ENCRYPTION_KEY_IN_USE(4);

  private int value;

  private EncryptionKeyStatusType(final int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public static EncryptionKeyStatusType fromValue(final int value) {
    for (final EncryptionKeyStatusType encryptionKeyStatusType : EncryptionKeyStatusType.values()) {
      if (encryptionKeyStatusType.value == value) {
        return encryptionKeyStatusType;
      }
    }
    return null;
  }
}
