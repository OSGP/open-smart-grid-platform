//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.AlarmHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmDecoder {

  static final int EQUIPMENT_IDENTIFIER_LENGTH = 17;
  static final int NUMBER_OF_BYTES_FOR_ALARM = 4;
  static final int NUMBER_OF_BYTES_FOR_LOGICAL_NAME = 6;

  static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
  static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
  static final String PUSH_UDP_TRIGGER = "Push udp monitor";
  static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
  static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

  final AlarmHelperService alarmHelperService;

  public AlarmDecoder() {
    this.alarmHelperService = new AlarmHelperService();
  }

  void decodeAlarmRegisterData(
      final InputStream inputStream,
      final DlmsPushNotification.Builder builder,
      final DlmsObjectType dlmsObjectType)
      throws UnrecognizedMessageDataException {

    final byte[] alarmBytes = this.readBytes(inputStream, NUMBER_OF_BYTES_FOR_ALARM);

    final Long alarmsAsLongValue = this.convertToLongValue(alarmBytes);

    final Set<AlarmTypeDto> alarms =
        this.alarmHelperService.toAlarmTypes(dlmsObjectType, alarmsAsLongValue);

    if (alarms.contains(null)) {
      throw new UnrecognizedMessageDataException(
          "Received alarm with unused bits set: " + Arrays.toString(alarmBytes));
    }

    builder.withTriggerType(PUSH_ALARM_TRIGGER);
    builder.addAlarms(alarms);
    builder.appendBytes(alarmBytes);
  }

  byte readByte(final InputStream inputStream) throws UnrecognizedMessageDataException {
    return this.readBytes(inputStream, 1)[0];
  }

  byte[] readBytes(final InputStream inputStream, final int length)
      throws UnrecognizedMessageDataException {
    try {
      final byte[] byteArray = new byte[length];
      inputStream.read(byteArray, 0, length);
      return byteArray;
    } catch (final IOException io) {
      throw new UnrecognizedMessageDataException(io.getMessage(), io);
    }
  }

  void skip(final InputStream inputStream, final int length)
      throws UnrecognizedMessageDataException {
    try {
      inputStream.skip(length);
    } catch (final IOException io) {
      throw new UnrecognizedMessageDataException(io.getMessage(), io);
    }
  }

  private Long convertToLongValue(final byte[] bytes) {
    long value = 0;
    for (final byte aByte : bytes) {
      value = (value << 8) + (aByte & 0xff);
    }

    return value;
  }
}
