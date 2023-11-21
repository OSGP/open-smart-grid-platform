// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.CLOCK_INVALID;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.PHASE_OUTAGE_DETECTED_L1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.RAM_ERROR;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.REPLACE_BATTERY;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;

@ExtendWith(MockitoExtension.class)
class AlarmDecoderTest {

  private final AlarmDecoder decoder = new AlarmDecoder();

  @Test
  void testDecodeEmptyAlarm() throws UnrecognizedMessageDataException {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream = new ByteArrayInputStream(new byte[] {0x00, 0x00, 0x00, 0x00});

    this.decoder.decodeAlarmRegisterData(inputStream, builder, DlmsObjectType.ALARM_REGISTER_1);

    assertThat(builder.build().getAlarms()).isEmpty();
  }

  @Test
  void testDecodeSingleAlarmLowestBit() throws UnrecognizedMessageDataException {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream = new ByteArrayInputStream(new byte[] {0x00, 0x00, 0x00, 0x01});

    this.decoder.decodeAlarmRegisterData(inputStream, builder, DlmsObjectType.ALARM_REGISTER_1);

    assertThat(builder.build().getAlarms()).containsExactlyInAnyOrder(CLOCK_INVALID);
  }

  @Test
  void testDecodeSingleAlarmHighestBit() throws UnrecognizedMessageDataException {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream =
        new ByteArrayInputStream(new byte[] {(byte) 0x80, 0x00, 0x00, 0x00});

    this.decoder.decodeAlarmRegisterData(inputStream, builder, DlmsObjectType.ALARM_REGISTER_1);

    assertThat(builder.build().getAlarms()).containsExactlyInAnyOrder(PHASE_OUTAGE_TEST_INDICATION);
  }

  @Test
  void testDecodeMultipleAlarms() throws UnrecognizedMessageDataException {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream =
        new ByteArrayInputStream(new byte[] {(byte) 0x91, 0x04, 0x02, 0x03});

    this.decoder.decodeAlarmRegisterData(inputStream, builder, DlmsObjectType.ALARM_REGISTER_1);

    assertThat(builder.build().getAlarms())
        .containsExactlyInAnyOrder(
            CLOCK_INVALID,
            REPLACE_BATTERY,
            RAM_ERROR,
            COMMUNICATION_ERROR_M_BUS_CHANNEL_3,
            NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1,
            PHASE_OUTAGE_DETECTED_L1,
            PHASE_OUTAGE_TEST_INDICATION);
  }

  @Test
  void testDecodeAlarmRegister2() throws UnrecognizedMessageDataException {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream = new ByteArrayInputStream(new byte[] {0x00, 0x00, 0x00, 0x3F});

    this.decoder.decodeAlarmRegisterData(inputStream, builder, DlmsObjectType.ALARM_REGISTER_2);

    assertThat(builder.build().getAlarms())
        .containsExactlyInAnyOrder(
            VOLTAGE_SAG_IN_PHASE_DETECTED_L1,
            VOLTAGE_SAG_IN_PHASE_DETECTED_L2,
            VOLTAGE_SAG_IN_PHASE_DETECTED_L3,
            VOLTAGE_SWELL_IN_PHASE_DETECTED_L1,
            VOLTAGE_SWELL_IN_PHASE_DETECTED_L2,
            VOLTAGE_SWELL_IN_PHASE_DETECTED_L3);
  }

  @Test
  void testDecodeAlarmRegisterWithUnknownAlarmSet() {
    final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();
    final InputStream inputStream =
        new ByteArrayInputStream(new byte[] {0x00, 0x00, 0x00, (byte) 0x80});

    assertThrows(
        UnrecognizedMessageDataException.class,
        () ->
            this.decoder.decodeAlarmRegisterData(
                inputStream, builder, DlmsObjectType.ALARM_REGISTER_1));
  }
}
