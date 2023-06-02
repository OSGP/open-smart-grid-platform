//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum PacketSwitchedStatusType {
  INACTIVE,
  GPRS,
  EDGE,
  UMTS,
  HSDPA,
  LTE,
  CDMA,
  LTE_CAT_M_18,
  LTE_NB_IOT,
  RESERVED;

  public String value() {
    return this.name();
  }

  public static PacketSwitchedStatusType fromValue(final String v) {
    return valueOf(v);
  }
}
