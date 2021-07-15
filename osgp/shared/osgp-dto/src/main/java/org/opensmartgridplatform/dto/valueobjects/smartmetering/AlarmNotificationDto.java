/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class AlarmNotificationDto implements Comparable<AlarmNotificationDto>, Serializable {

  private static final long serialVersionUID = -944840401788172403L;

  private AlarmTypeDto alarmType;
  private boolean enabled;

  public AlarmNotificationDto(final AlarmTypeDto alarmType, final boolean enabled) {
    this.alarmType = alarmType;
    this.enabled = enabled;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AlarmNotificationDto)) {
      return false;
    }
    return this.alarmType == ((AlarmNotificationDto) obj).alarmType;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.alarmType);
  }

  @Override
  public int compareTo(final AlarmNotificationDto o) {
    return this.alarmType.compareTo(o.alarmType);
  }

  @Override
  public String toString() {
    return "AlarmNotification[type=" + this.alarmType + ", enabled=" + this.enabled + "]";
  }

  public AlarmTypeDto getAlarmType() {
    return this.alarmType;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}
