//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;
import org.springframework.stereotype.Service;

@Service("amrProfileStatusCodeHelper")
public class AmrProfileStatusCodeHelper {
  private static final int NUMBER_OF_BITS_IN_REGISTER = 8;

  private static final ByteRegisterConverter<AmrProfileStatusCodeFlagDto> BYTE_REGISTER_CONVERTER;

  static {
    final Map<AmrProfileStatusCodeFlagDto, Integer> map =
        new EnumMap<>(AmrProfileStatusCodeFlagDto.class);

    map.put(AmrProfileStatusCodeFlagDto.CRITICAL_ERROR, 0);
    map.put(AmrProfileStatusCodeFlagDto.CLOCK_INVALID, 1);
    map.put(AmrProfileStatusCodeFlagDto.DATA_NOT_VALID, 2);
    map.put(AmrProfileStatusCodeFlagDto.DAYLIGHT_SAVING, 3);
    map.put(AmrProfileStatusCodeFlagDto.NOT_USED, 4);
    map.put(AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED, 5);
    map.put(AmrProfileStatusCodeFlagDto.RECOVERED_VALUE, 6);
    map.put(AmrProfileStatusCodeFlagDto.POWER_DOWN, 7);

    BYTE_REGISTER_CONVERTER =
        new ByteRegisterConverter<>(Collections.unmodifiableMap(map), NUMBER_OF_BITS_IN_REGISTER);
  }

  public Integer toBitPosition(final AmrProfileStatusCodeFlagDto amrProfileStatus) {
    return BYTE_REGISTER_CONVERTER.toBitPosition(amrProfileStatus);
  }

  public Set<AmrProfileStatusCodeFlagDto> toAmrProfileStatusCodeFlags(final Number registerValue) {
    return BYTE_REGISTER_CONVERTER.toTypes(registerValue.longValue());
  }

  public Short toValue(final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlags) {
    return BYTE_REGISTER_CONVERTER.toLongValue(amrProfileStatusCodeFlags).shortValue();
  }
}
