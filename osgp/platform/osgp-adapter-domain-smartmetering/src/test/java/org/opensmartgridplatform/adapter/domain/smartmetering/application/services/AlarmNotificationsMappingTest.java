//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotification;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmNotificationsMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // The Set may never be null. Tests if NullPointerException is thrown
  // when constructor uses a Set that is null.
  @Test
  public void testWithNullSet() {
    // test data
    final Set<AlarmNotification> alarmNotificationSet = null;
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new AlarmNotifications(alarmNotificationSet);
            });
  }

  // The Set may be empty. Tests if mapping with an empty Set succeeds.
  @Test
  public void testWithEmptySet() {

    // create test data
    final Set<AlarmNotification> alarmNotificationSet = new TreeSet<>();
    final AlarmNotifications alarmNotifications = new AlarmNotifications(alarmNotificationSet);

    // actual mapping
    final AlarmNotificationsDto alarmNotificationsDto =
        this.configurationMapper.map(alarmNotifications, AlarmNotificationsDto.class);

    // check if mapping was successful
    assertThat(alarmNotificationsDto).isNotNull();
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet()).isNotNull();
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet()).isEmpty();
  }

  // Tests if mapping with a Set with an entry succeeds.
  @Test
  public void testWithSet() {
    // create test data
    final AlarmNotification alarmNotification =
        new AlarmNotification(AlarmType.CLOCK_INVALID, true);
    final Set<AlarmNotification> alarmNotificationSet = new TreeSet<>();
    alarmNotificationSet.add(alarmNotification);
    final AlarmNotifications alarmNotifications = new AlarmNotifications(alarmNotificationSet);

    // actual mapping
    final AlarmNotificationsDto alarmNotificationsDto =
        this.configurationMapper.map(alarmNotifications, AlarmNotificationsDto.class);

    // check if mapping was successful
    assertThat(alarmNotificationsDto).isNotNull();
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet()).isNotNull();
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet().size())
        .isEqualTo(alarmNotificationSet.size());
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet().isEmpty()).isFalse();

    // To see if there is an AlarmNotifictionDto with the same variables as
    // the AlarmNotification in the Set.
    final AlarmNotificationDto alarmNotificationDto =
        new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true);
    assertThat(alarmNotificationsDto.getAlarmNotificationsSet().contains(alarmNotificationDto))
        .isTrue();
  }
}
