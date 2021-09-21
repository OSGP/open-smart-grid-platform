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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ByteRegisterConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.springframework.stereotype.Service;

@Service("alarmHelperService")
public class AlarmHelperService {
  private static final int NUMBER_OF_BITS_IN_REGISTER = 32;

  private static final Map<DlmsObjectType, ByteRegisterConverter<AlarmTypeDto>>
      BYTE_REGISTER_CONVERTERS = new EnumMap<>(DlmsObjectType.class);

  /**
   * Gives the position of the alarm code as indicated by the AlarmType in the bit string
   * representation of the alarm register.
   *
   * <p>A position of 0 means the least significant bit, up to the maximum of 31 for the most
   * significant bit. Since the 4 most significant bits in the object are not used according to the
   * DSMR documentation, the practical meaningful most significant bit is bit 27.
   */
  private static final Map<DlmsObjectType, Map<AlarmTypeDto, Integer>> ALARM_REGISTERS =
      new EnumMap<>(DlmsObjectType.class);

  static {
    // Bits for group: Other Alarms
    final Map<AlarmTypeDto, Integer> mapAlarmRegister1 = new EnumMap<>(AlarmTypeDto.class);
    ALARM_REGISTERS.put(DlmsObjectType.ALARM_FILTER_1, mapAlarmRegister1);

    mapAlarmRegister1.put(AlarmTypeDto.CLOCK_INVALID, 0);
    mapAlarmRegister1.put(AlarmTypeDto.REPLACE_BATTERY, 1);
    mapAlarmRegister1.put(AlarmTypeDto.POWER_UP, 2);
    mapAlarmRegister1.put(AlarmTypeDto.AUXILIARY_EVENT, 3);
    mapAlarmRegister1.put(AlarmTypeDto.CONFIGURATION_CHANGED, 4);
    // bits 5 to 7 are not used

    // Bits for group: Critical Alarms
    mapAlarmRegister1.put(AlarmTypeDto.PROGRAM_MEMORY_ERROR, 8);
    mapAlarmRegister1.put(AlarmTypeDto.RAM_ERROR, 9);
    mapAlarmRegister1.put(AlarmTypeDto.NV_MEMORY_ERROR, 10);
    mapAlarmRegister1.put(AlarmTypeDto.MEASUREMENT_SYSTEM_ERROR, 11);
    mapAlarmRegister1.put(AlarmTypeDto.WATCHDOG_ERROR, 12);
    mapAlarmRegister1.put(AlarmTypeDto.FRAUD_ATTEMPT, 13);
    // bits 14 and 15 are not used

    // Bits for group: M-Bus Alarms
    mapAlarmRegister1.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 16);
    mapAlarmRegister1.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 17);
    mapAlarmRegister1.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 18);
    mapAlarmRegister1.put(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 19);
    mapAlarmRegister1.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 20);
    mapAlarmRegister1.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 21);
    mapAlarmRegister1.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 22);
    mapAlarmRegister1.put(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 23);

    // Bits for group: Reserved
    mapAlarmRegister1.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 24);
    mapAlarmRegister1.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 25);
    mapAlarmRegister1.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 26);
    mapAlarmRegister1.put(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 27);
    mapAlarmRegister1.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L1, 28);
    mapAlarmRegister1.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L2, 29);
    mapAlarmRegister1.put(AlarmTypeDto.PHASE_OUTAGE_DETECTED_L3, 30);
    mapAlarmRegister1.put(AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION, 31);

    // Bits for group: alarm register 2
    final Map<AlarmTypeDto, Integer> mapAlarmRegister2 = new EnumMap<>(AlarmTypeDto.class);
    ALARM_REGISTERS.put(DlmsObjectType.ALARM_FILTER_2, mapAlarmRegister2);

    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1, 0);
    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2, 1);
    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3, 2);
    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1, 3);
    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2, 4);
    mapAlarmRegister2.put(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3, 5);

    BYTE_REGISTER_CONVERTERS.put(
        DlmsObjectType.ALARM_FILTER_1,
        new ByteRegisterConverter<>(
            Collections.unmodifiableMap(mapAlarmRegister1), NUMBER_OF_BITS_IN_REGISTER));
    BYTE_REGISTER_CONVERTERS.put(
        DlmsObjectType.ALARM_FILTER_2,
        new ByteRegisterConverter<>(
            Collections.unmodifiableMap(mapAlarmRegister2), NUMBER_OF_BITS_IN_REGISTER));
  }

  /**
   * Returns an unmodifiable instance of the map containing a bit index for every AlarmType.
   *
   * @return an unmodifiable map containing every AlarmType and it's bit index.
   */
  public Map<AlarmTypeDto, Integer> getAlarmRegisterBitIndexPerAlarmType(
      final DlmsObjectType dlmsObjectType) throws ProtocolAdapterException {
    if (!ALARM_REGISTERS.containsKey(dlmsObjectType)) {
      throw new ProtocolAdapterException("Unexpected dlmsObjectType: " + dlmsObjectType);
    }
    return ALARM_REGISTERS.get(dlmsObjectType);
  }

  /**
   * Returns the position of the bit value for the given AlarmType, in the 4-byte register space.
   *
   * @param alarmType AlarmType
   * @return position of the bit holding the alarm type value.
   */
  public Integer toBitPosition(final DlmsObjectType dlmsObjectType, final AlarmTypeDto alarmType)
      throws ProtocolAdapterException {
    if (!BYTE_REGISTER_CONVERTERS.containsKey(dlmsObjectType)) {
      throw new ProtocolAdapterException("Unexpected dlmsObjectType: " + dlmsObjectType);
    }
    return BYTE_REGISTER_CONVERTERS.get(dlmsObjectType).toBitPosition(alarmType);
  }

  /**
   * Create a set of alarm types representing the active bits in the register value.
   *
   * @param registerValue Value of the register.
   * @return List of active alarm types.
   */
  public Set<AlarmTypeDto> toAlarmTypes(
      final DlmsObjectType dlmsObjectType, final Number registerValue)
      throws ProtocolAdapterException {
    if (!BYTE_REGISTER_CONVERTERS.containsKey(dlmsObjectType)) {
      throw new ProtocolAdapterException("Unexpected dlmsObjectType: " + dlmsObjectType);
    }
    return BYTE_REGISTER_CONVERTERS.get(dlmsObjectType).toTypes(registerValue.longValue());
  }

  /**
   * Calculate the long value for the given set of AlarmTypes
   *
   * @param alarmTypes Set of AlarmTypes
   * @return Long value.
   */
  public Long toLongValue(final DlmsObjectType dlmsObjectType, final Set<AlarmTypeDto> alarmTypes)
      throws ProtocolAdapterException {
    if (!BYTE_REGISTER_CONVERTERS.containsKey(dlmsObjectType)) {
      throw new ProtocolAdapterException("Unexpected dlmsObjectType: " + dlmsObjectType);
    }
    return BYTE_REGISTER_CONVERTERS.get(dlmsObjectType).toLongValue(alarmTypes);
  }
}
