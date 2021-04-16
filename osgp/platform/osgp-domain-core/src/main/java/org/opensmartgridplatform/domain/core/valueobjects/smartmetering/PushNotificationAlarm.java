/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class PushNotificationAlarm implements Serializable {

  private static final long serialVersionUID = -7172367521771076238L;

  private final String deviceIdentification;
  private final EnumSet<AlarmType> alarms;
  private final byte[] alarmBytes;

  public PushNotificationAlarm(
      final String deviceIdentification, final Set<AlarmType> alarms, final byte[] alarmBytes) {
    this.deviceIdentification = deviceIdentification;
    this.alarmBytes = Arrays.copyOf(alarmBytes, alarmBytes.length);
    if (alarms == null || alarms.isEmpty()) {
      this.alarms = EnumSet.noneOf(AlarmType.class);
    } else {
      this.alarms = EnumSet.copyOf(alarms);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "PushNotificationAlarm[device=%s, alarms=%s]", this.deviceIdentification, this.alarms);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public byte[] getAlarmBytes() {
    return this.alarmBytes;
  }

  public Set<AlarmType> getAlarms() {
    return EnumSet.copyOf(this.alarms);
  }
}
