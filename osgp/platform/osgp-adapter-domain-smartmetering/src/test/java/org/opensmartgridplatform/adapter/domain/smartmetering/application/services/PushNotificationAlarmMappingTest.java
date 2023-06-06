// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;

public class PushNotificationAlarmMappingTest {

  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  private final byte[] alarmBytes = "some bytes".getBytes();

  // Test if mapping a PushNotificationAlarm object succeeds with null
  // variables
  @Test
  public void testWithNullVariables() {
    // build test data
    final String deviceId = null;
    final EnumSet<AlarmTypeDto> alarms = null;
    final PushNotificationAlarmDto pushNotificationAlarmDto =
        new PushNotificationAlarmDto(deviceId, alarms, this.alarmBytes);
    // actual mapping
    final PushNotificationAlarm pushNotificationAlarm =
        this.mapperFactory
            .getMapperFacade()
            .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
    // test mapping
    assertThat(pushNotificationAlarm).isNotNull();
    assertThat(pushNotificationAlarm.getDeviceIdentification()).isNull();
    // the constructor creates an empty EnumSet when passed a null value.
    assertThat(pushNotificationAlarmDto.getAlarms()).isEmpty();
    assertThat(pushNotificationAlarm.getAlarms()).isEmpty();
    assertThat(this.alarmBytes)
        .withFailMessage("The alarm bytes should be the same after the mapping")
        .isEqualTo(pushNotificationAlarm.getAlarmBytes());
  }

  // Test if mapping a PushNotificationAlarm object succeeds when the EnumSet
  // is empty
  @Test
  public void testWithEmptyEnumSet() {
    // build test data
    final String deviceId = "device1";
    final EnumSet<AlarmTypeDto> alarms = EnumSet.noneOf(AlarmTypeDto.class);
    final PushNotificationAlarmDto pushNotificationAlarmDto =
        new PushNotificationAlarmDto(deviceId, alarms, this.alarmBytes);
    // actual mapping
    final PushNotificationAlarm pushNotificationAlarm =
        this.mapperFactory
            .getMapperFacade()
            .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
    // test mapping
    assertThat(pushNotificationAlarm).isNotNull();
    assertThat(pushNotificationAlarm.getDeviceIdentification()).isEqualTo(deviceId);
    assertThat(pushNotificationAlarm.getAlarms()).isEmpty();
    assertThat(this.alarmBytes).isEqualTo(pushNotificationAlarm.getAlarmBytes());
  }

  // Test if mapping a PushNotificationAlarm object succeeds when the EnumSet
  // is not empty
  @Test
  public void testWithNonEmptyEnumSet() {
    // build test data
    final String deviceId = "device1";
    final EnumSet<AlarmTypeDto> alarms = EnumSet.of(AlarmTypeDto.CLOCK_INVALID);
    final PushNotificationAlarmDto pushNotificationAlarmDto =
        new PushNotificationAlarmDto(deviceId, alarms, this.alarmBytes);
    // actual mapping
    final PushNotificationAlarm pushNotificationAlarm =
        this.mapperFactory
            .getMapperFacade()
            .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
    // test mapping
    assertThat(pushNotificationAlarm).isNotNull();
    assertThat(pushNotificationAlarm.getDeviceIdentification()).isEqualTo(deviceId);
    assertThat(pushNotificationAlarm.getAlarms().size()).isEqualTo(alarms.size());
    assertThat(pushNotificationAlarm.getAlarms().contains(AlarmType.CLOCK_INVALID)).isTrue();
    assertThat(this.alarmBytes).isEqualTo(pushNotificationAlarm.getAlarmBytes());
  }
}
