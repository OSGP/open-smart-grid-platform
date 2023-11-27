// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum ConfigurationFlagTypeDto {
  DISCOVER_ON_OPEN_COVER(0, 0, null, false),
  DISCOVER_ON_POWER_ON(1, 1, null, false),
  DYNAMIC_MBUS_ADDRESS(2, 2, null, false),
  PO_ENABLE(3, 3, 3, false),
  HLS_3_ON_P3_ENABLE(4, 4, null, false),
  HLS_4_ON_P3_ENABLE(5, 5, null, false),
  HLS_5_ON_P3_ENABLE(6, 6, 6, false),
  HLS_3_ON_P0_ENABLE(7, 7, null, false),
  HLS_4_ON_P0_ENABLE(8, 8, null, false),
  HLS_5_ON_P0_ENABLE(9, 9, 9, false),
  DIRECT_ATTACH_AT_POWER_ON(null, 10, 10, false),
  HLS_6_ON_P3_ENABLE(null, null, 11, false),
  HLS_7_ON_P3_ENABLE(null, null, 12, false),
  HLS_6_ON_P0_ENABLE(null, null, 13, false),
  HLS_7_ON_P0_ENABLE(null, null, 14, false);

  private final Integer bitPositionDsmr4;
  private final Integer bitPositionDsmr43;
  private final Integer bitPositionSmr5;
  private final boolean readOnly;

  /* TODO Should be able to set Configration Flag.DIRECT_ATTACH_AT_POWER_ON
   *      on bitposition 10 for DSMR 4.3 meters should
   */
  ConfigurationFlagTypeDto(
      final Integer bitPositionDsmr4,
      final Integer bitPositionDsmr43,
      final Integer bitPositionSmr5,
      final boolean readOnly) {
    this.bitPositionDsmr4 = bitPositionDsmr4;
    this.bitPositionDsmr43 = bitPositionDsmr43;
    this.bitPositionSmr5 = bitPositionSmr5;
    this.readOnly = readOnly;
  }

  public Optional<Integer> getBitPositionSmr5() {
    return Optional.ofNullable(this.bitPositionSmr5);
  }

  public Optional<Integer> getBitPositionDsmr4() {
    return Optional.ofNullable(this.bitPositionDsmr4);
  }

  public Optional<Integer> getBitPositionDsmr43() {
    return Optional.ofNullable(this.bitPositionDsmr43);
  }

  public boolean isReadOnly() {
    return this.readOnly;
  }

  public static Optional<ConfigurationFlagTypeDto> getSmr5FlagType(final Integer bitPosition) {
    return Arrays.stream(values())
        .filter(v -> Objects.equals(bitPosition, v.bitPositionSmr5))
        .findAny();
  }

  public static Optional<ConfigurationFlagTypeDto> getDsmr4FlagType(final Integer bitPosition) {
    return Arrays.stream(values())
        .filter(v -> Objects.equals(bitPosition, v.bitPositionDsmr4))
        .findAny();
  }

  public static Optional<ConfigurationFlagTypeDto> getDsmr43FlagType(final Integer bitPosition) {
    return Arrays.stream(values())
        .filter(v -> Objects.equals(bitPosition, v.bitPositionDsmr43))
        .findAny();
  }
}
