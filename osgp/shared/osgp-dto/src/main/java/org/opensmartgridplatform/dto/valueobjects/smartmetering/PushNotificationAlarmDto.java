// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class PushNotificationAlarmDto implements Serializable {

  private static final long serialVersionUID = -5389008513362783376L;

  private final String deviceIdentification;
  private final EnumSet<AlarmTypeDto> alarms;
  private final byte[] alarmBytes;

  public PushNotificationAlarmDto(
      final String deviceIdentification, final Set<AlarmTypeDto> alarms, final byte[] alarmBytes) {
    this.deviceIdentification = deviceIdentification;

    this.alarmBytes = Arrays.copyOf(alarmBytes, alarmBytes.length);

    if (alarms == null || alarms.isEmpty()) {
      this.alarms = EnumSet.noneOf(AlarmTypeDto.class);
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

  public Set<AlarmTypeDto> getAlarms() {
    return EnumSet.copyOf(this.alarms);
  }
}
