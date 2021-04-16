/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;

public class AmrProfileStatusCodeHelperTest {

  private final AmrProfileStatusCodeHelper helperService = new AmrProfileStatusCodeHelper();

  @Test
  public void testConvertToLong() {
    final Set<AmrProfileStatusCodeFlagDto> amrStatusCodeFlags = new HashSet<>();

    amrStatusCodeFlags.add(AmrProfileStatusCodeFlagDto.DATA_NOT_VALID);
    amrStatusCodeFlags.add(AmrProfileStatusCodeFlagDto.POWER_DOWN);

    assertThat((short) this.helperService.toValue(amrStatusCodeFlags)).isEqualTo((short) 132);
  }

  @Test
  public void testConvertToAmrProfileStatusCodeFlags() {
    final short registerValue = Short.parseShort("00100100", 2);

    final Set<AmrProfileStatusCodeFlagDto> amrStatusCodeFlags =
        this.helperService.toAmrProfileStatusCodeFlags(registerValue);

    assertThat(amrStatusCodeFlags.contains(AmrProfileStatusCodeFlagDto.DATA_NOT_VALID)).isTrue();
    assertThat(amrStatusCodeFlags.contains(AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED)).isTrue();
  }

  @Test
  public void testBitPositions() {
    assertThat((int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.CLOCK_INVALID))
        .isEqualTo(1);
    assertThat((int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.DAYLIGHT_SAVING))
        .isEqualTo(3);
    assertThat((int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.POWER_DOWN))
        .isEqualTo(7);
  }
}
