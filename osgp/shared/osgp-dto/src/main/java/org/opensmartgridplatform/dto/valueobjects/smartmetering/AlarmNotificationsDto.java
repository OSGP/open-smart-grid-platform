// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class AlarmNotificationsDto implements Serializable {

  private static final long serialVersionUID = 2319359505656305783L;

  private Set<AlarmNotificationDto> alarmNotificationsSet;

  public AlarmNotificationsDto(final Set<AlarmNotificationDto> alarmNotificationsSet) {
    this.alarmNotificationsSet = new TreeSet<>(alarmNotificationsSet);
  }

  @Override
  public String toString() {
    return "AlarmNotifications[" + this.alarmNotificationsSet + "]";
  }

  public Set<AlarmNotificationDto> getAlarmNotificationsSet() {
    return new TreeSet<>(this.alarmNotificationsSet);
  }
}
