/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;

class PushNotificationAlarmMappingTest {

  private static final String DEVICE_ID = "id1";
  private static final AlarmType ALARMTYPE = AlarmType.CLOCK_INVALID;
  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  private final byte[] alarmBytes = "some bytes".getBytes();

  /** Tests if a PushNotificationAlarm is mapped correctly with an empty Set. */
  @Test
  void testPushNotificationAlarmMappingWithEmptySet() {

    // build test data
    final Set<AlarmType> alarms = new TreeSet<>();
    final PushNotificationAlarm original =
        new PushNotificationAlarm(DEVICE_ID, alarms, this.alarmBytes);

    // actual mapping
    final RetrievePushNotificationAlarmResponse mapped =
        this.monitoringMapper.map(original, RetrievePushNotificationAlarmResponse.class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isNotNull();
    assertThat(mapped.getAlarmRegister()).isNotNull();
    assertThat(mapped.getAlarmRegister().getAlarmTypes()).isNotNull();

    assertThat(mapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(mapped.getAlarmRegister().getAlarmTypes().isEmpty()).isTrue();
  }

  /** Tests if a PushNotificationAlarm object is mapped correctly with a filled Set. */
  @Test
  void testPushNotificationAlarmMappingWithFilledSet() {

    // build test data
    final Set<AlarmType> alarms = new TreeSet<>();
    alarms.add(ALARMTYPE);

    final PushNotificationAlarm original =
        new PushNotificationAlarm(DEVICE_ID, alarms, this.alarmBytes);

    // actual mapping
    final RetrievePushNotificationAlarmResponse mapped =
        this.monitoringMapper.map(original, RetrievePushNotificationAlarmResponse.class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isNotNull();
    assertThat(mapped.getAlarmRegister()).isNotNull();
    assertThat(mapped.getAlarmRegister().getAlarmTypes()).isNotEmpty();
    assertThat(mapped.getAlarmRegister().getAlarmTypes().get(0)).isNotNull();

    assertThat(mapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);
    assertThat(mapped.getAlarmRegister().getAlarmTypes().get(0).name()).isEqualTo(ALARMTYPE.name());
  }

  /** Tests if mapping a PushNotificationAlarm object succeeds with a null Set. */
  @Test
  void testPushNotificiationAlarmMappingWithNullSet() {

    // build test data
    final Set<AlarmType> alarms = null;
    final PushNotificationAlarm original =
        new PushNotificationAlarm(DEVICE_ID, alarms, this.alarmBytes);

    // actual mapping
    final RetrievePushNotificationAlarmResponse mapped =
        this.monitoringMapper.map(original, RetrievePushNotificationAlarmResponse.class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isNotNull();
    assertThat(mapped.getDeviceIdentification()).isEqualTo(DEVICE_ID);

    // constructor for PushNotificationAlarm creates a new Set when passed a
    // null value, so we should check for an empty List
    assertThat(mapped.getAlarmRegister()).isNotNull();
    assertThat(mapped.getAlarmRegister().getAlarmTypes()).isNotNull();
    assertThat(mapped.getAlarmRegister().getAlarmTypes().isEmpty()).isTrue();
  }
}
