/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ByteRegisterConverter;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.springframework.stereotype.Service;

@Service("alarmHelperService")
public class AlarmHelperService {
  private static final int NUMBER_OF_BITS_IN_REGISTER = 32;

  private static final ByteRegisterConverter<AlarmTypeDto> BYTE_REGISTER_CONVERTER;

  /**
   * Gives the position of the alarm code as indicated by the AlarmType in the bit string
   * representation of the alarm register.
   *
   * <p>A position of 0 means the least significant bit, up to the maximum of 31 for the most
   * significant bit. Since the 4 most significant bits in the object are not used according to the
   * DSMR documentation, the practical meaningful most significant bit is bit 27.
   */
  private static final Map<AlarmTypeDto, Integer> map = new EnumMap<>(AlarmTypeDto.class);

  static {

    // Bits for group: Other Alarms
    map.put(AlarmTypeDto.CLOCK_INVALID, 0);
    map.put(AlarmTypeDto.REPLACE_BATTERY, 1);
    map.put(AlarmTypeDto.POWER_UP, 2);
    map.put(AlarmTypeDto.AUXILIARY_EVENT, 3);
    map.put(AlarmTypeDto.CONFIGURATION_CHANGED, 4);
    // bits 5 to 7 are not used

    // Bits for group: Critical Alarms
    map.put(AlarmTypeDto.PROGRAM_MEMORY_ERROR, 8);
    map.put(AlarmTypeDto.RAM_ERROR, 9);
    map.put(AlarmTypeDto.NV_MEMORY_ERROR, 10);
    map.put(AlarmTypeDto.MEASUREMENT_SYSTEM_ERROR, 11);
    map.put(AlarmTypeDto.WATCHDOG_ERROR, 12);
    map.put(AlarmTypeDto.FRAUD_ATTEMPT, 13);
    // bits 14 and 15 are not used

    // Bits for group: M-Bus Alarms
    map.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 16);
    map.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 17);
    map.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 18);
    map.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 19);
    map.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 20);
    map.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 21);
    map.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 22);
    map.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 23);

    // Bits for group: Reserved
    map.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 24);
    map.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 25);
    map.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 26);
    map.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 27);
    map.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L_1, 28);
    map.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L_2, 29);
    map.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L_3, 30);
    map.put(AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION, 31);

    BYTE_REGISTER_CONVERTER =
        new ByteRegisterConverter<>(Collections.unmodifiableMap(map), NUMBER_OF_BITS_IN_REGISTER);
  }

  /**
   * Returns an unmodifiable instance of the map containing a bit index for every AlarmType.
   *
   * @return an unmodifiable map containing every AlarmType and it's bit index.
   */
  public Map<AlarmTypeDto, Integer> getAlarmRegisterBitIndexPerAlarmType() {
    return Collections.unmodifiableMap(map);
  }

  /**
   * Returns the position of the bit value for the given AlarmType, in the 4-byte register space.
   *
   * @param alarmType AlarmType
   * @return position of the bit holding the alarm type value.
   */
  public Integer toBitPosition(final AlarmTypeDto alarmType) {
    return BYTE_REGISTER_CONVERTER.toBitPosition(alarmType);
  }

  /**
   * Create a set of alarm types representing the active bits in the register value.
   *
   * @param registerValue Value of the register.
   * @return List of active alarm types.
   */
  public Set<AlarmTypeDto> toAlarmTypes(final Number registerValue) {
    return BYTE_REGISTER_CONVERTER.toTypes(registerValue.longValue());
  }

  /**
   * Calculate the long value for the given set of AlarmTypes
   *
   * @param alarmTypes Set of AlarmTypes
   * @return Long value.
   */
  public Long toLongValue(final Set<AlarmTypeDto> alarmTypes) {
    return BYTE_REGISTER_CONVERTER.toLongValue(alarmTypes);
  }
}
