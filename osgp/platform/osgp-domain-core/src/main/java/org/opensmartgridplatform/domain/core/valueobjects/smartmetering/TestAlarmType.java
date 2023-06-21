// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum TestAlarmType {
  PARTIAL_POWER_OUTAGE,
  LAST_GASP;

  public String value() {
    return this.name();
  }

  public static TestAlarmType fromValue(final String v) {
    return valueOf(v);
  }
}
