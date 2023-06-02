//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

@ExtendWith(MockitoExtension.class)
class DlmsPushNotificationDecoderTest {

  private static final String IDENTIFIER = "EXXXX123456789012";

  private static final byte[] SCHEDULER_OBISCODE_BYTES =
      new byte[] {0x00, 0x00, 0x0F, 0x00, 0x04, (byte) 0xFF};
  private static final byte[] SCHEDULER_SETUP_OBISCODE_BYTES =
      new byte[] {0x00, 0x00, 0x19, 0x09, 0x00, (byte) 0xFF};
  private static final byte[] CSD_OBISCODE_BYTES =
      new byte[] {0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF};
  private static final byte[] SMS_OBISCODE_BYTES =
      new byte[] {0x00, 0x00, 0x02, 0x03, 0x00, (byte) 0xFF};

  private static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
  private static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
  private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";
  private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";

  private static final byte[] CLOCK_INVALID_ALARM_BYTES = new byte[] {0x00, 0x00, 0x00, 0x01};

  private static final int PUSH_SETUP_TCP_BIT = 1;

  private DlmsPushNotificationDecoder decoder;

  @Mock private ChannelHandlerContext ctx;

  @BeforeEach
  void setUp() {
    this.decoder = new DlmsPushNotificationDecoder();
  }

  @Test
  void testDecodeDsmr4() throws UnrecognizedMessageDataException, IOException {
    final List<Object> out = new ArrayList<>();
    final ByteBuf byteBuf =
        this.newByteBuf(AlarmGeneratorUtil.dsmr4Alarm(IDENTIFIER, CLOCK_INVALID_ALARM_BYTES));
    this.decoder.decode(this.ctx, byteBuf, out);

    assertThat(out).hasSize(1);
    this.assertPushNotification(
        (DlmsPushNotification) out.get(0),
        IDENTIFIER,
        PUSH_ALARM_TRIGGER,
        Collections.singleton(AlarmTypeDto.CLOCK_INVALID));
  }

  @Test
  void testDecodeSmr5() throws UnrecognizedMessageDataException, IOException {
    final List<Object> out = new ArrayList<>();
    final ByteBuf byteBuf =
        this.newByteBuf(
            AlarmGeneratorUtil.smr5Alarm(
                IDENTIFIER,
                PUSH_SETUP_TCP_BIT,
                Collections.singletonList(CLOCK_INVALID_ALARM_BYTES)));
    this.decoder.decode(this.ctx, byteBuf, out);

    assertThat(out).hasSize(1);
    this.assertPushNotification(
        (DlmsPushNotification) out.get(0),
        IDENTIFIER,
        PUSH_ALARM_TRIGGER,
        Collections.singleton(AlarmTypeDto.CLOCK_INVALID));
  }

  @Test
  void decodeDsmr4AlarmsWithLogicalNames() throws UnrecognizedMessageDataException, IOException {

    // Test the 4 possible logical names (3 different trigger types)
    this.decodeDsmr4AlarmsWithLogicalName(SCHEDULER_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
    this.decodeDsmr4AlarmsWithLogicalName(SCHEDULER_SETUP_OBISCODE_BYTES, PUSH_SCHEDULER_TRIGGER);
    this.decodeDsmr4AlarmsWithLogicalName(CSD_OBISCODE_BYTES, PUSH_CSD_TRIGGER);
    this.decodeDsmr4AlarmsWithLogicalName(SMS_OBISCODE_BYTES, PUSH_SMS_TRIGGER);

    // Any other logical name should result in an empty trigger type
    this.decodeDsmr4AlarmsWithLogicalName(new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06}, "");
  }

  private void decodeDsmr4AlarmsWithLogicalName(
      final byte[] logicalName, final String expectedTriggerType)
      throws IOException, UnrecognizedMessageDataException {

    final List<Object> out = new ArrayList<>();
    final ByteBuf byteBuf = this.newByteBuf(AlarmGeneratorUtil.dsmr4Alarm(IDENTIFIER, logicalName));
    this.decoder.decode(this.ctx, byteBuf, out);

    assertThat(out).hasSize(1);
    this.assertPushNotification(
        (DlmsPushNotification) out.get(0), IDENTIFIER, expectedTriggerType, Sets.newHashSet());
  }

  private ByteBuf newByteBuf(final byte[] byteArray) {
    final ByteBuf byteBuf = mock(ByteBuf.class);
    when(byteBuf.readableBytes()).thenReturn(byteArray.length);
    doAnswer(
            invocation -> {
              final byte[] outputValue = (byte[]) invocation.getArguments()[0];
              System.arraycopy(byteArray, 0, outputValue, 0, byteArray.length);
              return null;
            })
        .when(byteBuf)
        .readBytes(any(byte[].class));
    return byteBuf;
  }

  private void assertPushNotification(
      final DlmsPushNotification dlmsPushNotification,
      final String equipmentIdentifier,
      final String triggerType,
      final Set<AlarmTypeDto> alarms) {
    assertThat(dlmsPushNotification.getTriggerType()).isEqualTo(triggerType);
    assertThat(dlmsPushNotification.getEquipmentIdentifier()).isEqualTo(equipmentIdentifier);
    assertThat(dlmsPushNotification.getAlarms()).isEqualTo(alarms);
  }
}
