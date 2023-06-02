//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum CircuitSwitchedStatusType {
  INACTIVE,
  INCOMING_CALL,
  ACTIVE,
  RESERVED;

  public String value() {
    return this.name();
  }

  public static CircuitSwitchedStatusType fromValue(final String v) {
    return valueOf(v);
  }
}
