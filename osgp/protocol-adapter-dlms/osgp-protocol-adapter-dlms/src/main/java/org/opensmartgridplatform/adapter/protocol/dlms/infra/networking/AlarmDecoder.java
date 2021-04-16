/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.AlarmHelperService;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmDecoder {

  static final int EQUIPMENT_IDENTIFIER_LENGTH = 17;
  static final int NUMBER_OF_BYTES_FOR_ALARM = 4;
  static final int NUMBER_OF_BYTES_FOR_LOGICAL_NAME = 6;

  static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
  static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
  static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
  static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

  void decodeAlarmRegisterData(final ByteBuf buffer, final DlmsPushNotification.Builder builder) {

    final byte[] alarmBytes = this.read(buffer, NUMBER_OF_BYTES_FOR_ALARM);

    final Set<AlarmTypeDto> alarms =
        new AlarmHelperService().toAlarmTypes(ByteBuffer.wrap(alarmBytes).getInt());

    builder.withTriggerType(PUSH_ALARM_TRIGGER);
    builder.withAlarms(alarms);
    builder.appendBytes(alarmBytes);
  }

  byte[] read(final ByteBuf buffer, final int size) {
    final byte[] result = new byte[size];
    buffer.readBytes(result, 0, size);
    return result;
  }
}
