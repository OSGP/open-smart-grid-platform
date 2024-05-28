// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import lombok.Getter;

@Getter
public enum AccessType {
  NO_ACCESS(0),
  R(1),
  W(2),
  RW(3),
  X(4);

  final int id;

  AccessType(final int id) {
    this.id = id;
  }

  public static AccessType accessTypeFor(final int id) {
    for (final AccessType accessType : AccessType.values()) {
      if (accessType.id == id) {
        return accessType;
      }
    }

    return null;
  }
}
