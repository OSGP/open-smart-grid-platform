/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class AlarmNotifications implements Serializable {

  private static final long serialVersionUID = 2319359505656305783L;

  private Set<AlarmNotification> alarmNotificationsSet;

  public AlarmNotifications(final Set<AlarmNotification> alarmNotificationsSet) {
    this.alarmNotificationsSet = new TreeSet<AlarmNotification>(alarmNotificationsSet);
  }

  @Override
  public String toString() {
    return "AlarmNotifications[" + this.alarmNotificationsSet + "]";
  }

  public Set<AlarmNotification> getAlarmNotificationsSet() {
    return new TreeSet<>(this.alarmNotificationsSet);
  }
}
