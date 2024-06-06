// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ByteRegisterConverter;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.springframework.stereotype.Service;

@Service("alarmHelperService")
public class AlarmHelperService {
  private static final int NUMBER_OF_BITS_IN_REGISTER = 32;

  private static final Map<DlmsObjectType, ByteRegisterConverter<AlarmTypeDto>>
      BYTE_REGISTER_CONVERTERS = new EnumMap<>(DlmsObjectType.class);

  static {
    for (final DlmsObjectType dlmsObjectTypeAlarmRegister :
        AlarmTypeRegisterLookup.getAlarmRegisters()) {
      final Map<AlarmTypeDto, Integer> alarmRegisterMap =
          AlarmTypeRegisterLookup.findByAlarmRegister(dlmsObjectTypeAlarmRegister).stream()
              .collect(
                  Collectors.toMap(
                      AlarmTypeRegisterLookup::getAlarmTypeDto, AlarmTypeRegisterLookup::getBit));

      BYTE_REGISTER_CONVERTERS.put(
          dlmsObjectTypeAlarmRegister,
          new ByteRegisterConverter<>(
              Collections.unmodifiableMap(alarmRegisterMap), NUMBER_OF_BITS_IN_REGISTER));
    }
  }

  /**
   * Returns bit index for an AlarmType.
   *
   * @param alarmType AlarmType
   * @return an unmodifiable map containing every AlarmType and it's bit index.
   */
  public Integer getAlarmRegisterBitIndex(final AlarmTypeDto alarmType) {
    return AlarmTypeRegisterLookup.getByAlarmType(alarmType).getBit();
  }

  /**
   * Returns the position of the bit value for the given AlarmType, in the 4-byte register space.
   *
   * @param alarmType AlarmType
   * @return position of the bit holding the alarm type value.
   */
  public Integer toBitPosition(final AlarmTypeDto alarmType) {
    final DlmsObjectType alarmRegister =
        AlarmTypeRegisterLookup.getByAlarmType(alarmType).getAlarmRegisterDlmsObjectType();

    return this.getByteRegisterConverter(alarmRegister).toBitPosition(alarmType);
  }

  /**
   * Returns the alarm types possible in register
   *
   * @param alarmRegisterDlmsObjectType DlmsObjectType (DlmsObjectType.ALARM_REGISTER_1 or
   *     DlmsObjectType.ALARM_REGISTER_2)
   * @return Set<AlarmTypeDto> AlarmTypeDto alarm types in register.
   */
  public Set<AlarmTypeDto> alarmTypesForRegister(final DlmsObjectType alarmRegisterDlmsObjectType) {
    return AlarmTypeRegisterLookup.getAlarmTypesForRegister(alarmRegisterDlmsObjectType);
  }

  /**
   * Create a set of alarm types representing the active bits in the register value.
   *
   * @param alarmRegisterDlmsObjectType DlmsObjectType (DlmsObjectType.ALARM_REGISTER_1 or
   *     DlmsObjectType.ALARM_REGISTER_2)
   * @param registerValue Value of the register.
   * @return List of active alarm types.
   */
  public Set<AlarmTypeDto> toAlarmTypes(
      final DlmsObjectType alarmRegisterDlmsObjectType, final Number registerValue) {
    return this.getByteRegisterConverter(alarmRegisterDlmsObjectType)
        .toTypes(registerValue.longValue());
  }

  /**
   * Calculate the long value for the given set of AlarmTypes
   *
   * @param alarmRegisterDlmsObjectType DlmsObjectType (DlmsObjectType.ALARM_REGISTER_1 or
   *     DlmsObjectType.ALARM_REGISTER_2)
   * @param alarmTypes Set of AlarmTypes
   * @return Long value.
   */
  public Long toLongValue(
      final DlmsObjectType alarmRegisterDlmsObjectType, final Set<AlarmTypeDto> alarmTypes) {
    return this.getByteRegisterConverter(alarmRegisterDlmsObjectType).toLongValue(alarmTypes);
  }

  private ByteRegisterConverter<AlarmTypeDto> getByteRegisterConverter(
      final DlmsObjectType alarmRegisterDlmsObjectType) {
    if (!AlarmTypeRegisterLookup.getAlarmRegisters().contains(alarmRegisterDlmsObjectType)) {
      throw new IllegalArgumentException(
          "Unexpected alarmRegisterDlmsObjectType: " + alarmRegisterDlmsObjectType);
    }
    return BYTE_REGISTER_CONVERTERS.get(alarmRegisterDlmsObjectType);
  }
}
