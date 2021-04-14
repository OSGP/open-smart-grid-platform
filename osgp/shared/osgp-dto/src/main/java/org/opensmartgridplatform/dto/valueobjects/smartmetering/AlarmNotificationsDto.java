/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
