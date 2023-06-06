// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum SignalQualityDto {
  MINUS_113_DBM_OR_LESS(0),
  MINUS_111_DBM(1),
  MINUS_109_DBM(2),
  MINUS_107_DBM(3),
  MINUS_105_DBM(4),
  MINUS_103_DBM(5),
  MINUS_101_DBM(6),
  MINUS_99_DBM(7),
  MINUS_97_DBM(8),
  MINUS_95_DBM(9),
  MINUS_93_DBM(10),
  MINUS_91_DBM(11),
  MINUS_89_DBM(12),
  MINUS_87_DBM(13),
  MINUS_85_DBM(14),
  MINUS_83_DBM(15),
  MINUS_81_DBM(16),
  MINUS_79_DBM(17),
  MINUS_77_DBM(18),
  MINUS_75_DBM(19),
  MINUS_73_DBM(20),
  MINUS_71_DBM(21),
  MINUS_69_DBM(22),
  MINUS_67_DBM(23),
  MINUS_65_DBM(24),
  MINUS_63_DBM(25),
  MINUS_61_DBM(26),
  MINUS_59_DBM(27),
  MINUS_57_DBM(28),
  MINUS_55_DBM(29),
  MINUS_53_DBM(30),
  MINUS_51_DBM_OR_GREATER(31),
  NOT_KNOWN_OR_NOT_DETECTABLE(99);

  private final int index;

  private SignalQualityDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static SignalQualityDto fromIndexValue(final int value) {
    for (final SignalQualityDto status : SignalQualityDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("IndexValue " + value + " not found for SignalQualityDto");
  }

  public String value() {
    return this.name();
  }

  public static SignalQualityDto fromValue(final String v) {
    return valueOf(v);
  }
}
