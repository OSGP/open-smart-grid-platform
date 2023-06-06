// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum PacketSwitchedStatusDto {
  INACTIVE(0),
  GPRS(1),
  EDGE(2),
  UMTS(3),
  HSDPA(4),
  LTE(5),
  CDMA(6),
  LTE_CAT_M_18(7),
  LTE_NB_IOT(8),
  RESERVED(9); // 9 - 255

  private final int index;

  private PacketSwitchedStatusDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static PacketSwitchedStatusDto fromIndexValue(final int value) {
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException(
          "IndexValue " + value + " not found for PacketSwitchedStatusDto");
    }

    for (final PacketSwitchedStatusDto status : PacketSwitchedStatusDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    return RESERVED;
  }

  public String value() {
    return this.name();
  }

  public static PacketSwitchedStatusDto fromValue(final String v) {
    return valueOf(v);
  }
}
