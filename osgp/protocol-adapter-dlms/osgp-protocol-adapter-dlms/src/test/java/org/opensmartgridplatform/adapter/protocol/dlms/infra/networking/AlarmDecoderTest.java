/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.AlarmHelperService;

@ExtendWith(MockitoExtension.class)
class AlarmDecoderTest {

  private AlarmDecoder decoder;

  @Mock private AlarmHelperService alarmHelperService;

  @Test
  void testDecode() {

    //    final ByteBuf buffer = mock(ByteBuf.class);
    //    final DlmsPushNotification.Builder builder = mock(DlmsPushNotification.Builder.class);
    //
    //    when(buffer.readBytes((byte[]) any(), any(), 4)).thenReturn()
    //
    //    this.decoder.decodeAlarmRegisterData(buffer, builder, DlmsObjectType.ALARM_REGISTER_1);
  }
}
