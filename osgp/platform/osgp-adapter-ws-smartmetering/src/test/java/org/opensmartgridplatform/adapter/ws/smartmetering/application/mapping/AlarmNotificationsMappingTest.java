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

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;

public class AlarmNotificationsMappingTest {

  private static final AlarmType ALARMTYPE = AlarmType.CLOCK_INVALID;
  private static final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType
      ALARMTYPEMAPPED =
          org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType.CLOCK_INVALID;
  private static final boolean ENABLED = true;
  private ConfigurationMapper configurationMapper = new ConfigurationMapper();

  /** Test to see if AlarmNotifications can be mapped. */
  @Test
  public void testAlarmNotificationsMapping() {

    // build test data
    final AlarmNotification alarmNotification = new AlarmNotification();
    alarmNotification.setAlarmType(ALARMTYPE);
    alarmNotification.setEnabled(ENABLED);
    final AlarmNotifications alarmNotificationsOriginal = new AlarmNotifications();
    alarmNotificationsOriginal.getAlarmNotification().add(alarmNotification);

    // actual mapping
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications
        alarmNotificationsMapped =
            this.configurationMapper.map(
                alarmNotificationsOriginal,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications
                    .class);

    // check mapping
    assertThat(alarmNotificationsMapped).isNotNull();
    assertThat(alarmNotificationsMapped.getAlarmNotificationsSet()).isNotNull();
    assertThat(alarmNotificationsMapped.getAlarmNotificationsSet().isEmpty()).isFalse();
    assertThat(alarmNotificationsMapped.getAlarmNotificationsSet().size())
        .isEqualTo(alarmNotificationsOriginal.getAlarmNotification().size());
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotification
        alarmNotificationMapped =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotification(
                ALARMTYPEMAPPED, ENABLED);
    assertThat(
            alarmNotificationsMapped.getAlarmNotificationsSet().contains(alarmNotificationMapped))
        .isTrue();
  }
}
