// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class AlarmNotification implements Comparable<AlarmNotification>, Serializable {

  private static final long serialVersionUID = -944840401788172403L;

  private final AlarmType alarmType;
  private final boolean enabled;

  public AlarmNotification(final AlarmType alarmType, final boolean enabled) {
    this.alarmType = alarmType;
    this.enabled = enabled;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AlarmNotification)) {
      return false;
    }
    return this.alarmType == ((AlarmNotification) obj).alarmType;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.alarmType);
  }

  @Override
  public int compareTo(final AlarmNotification o) {
    return this.alarmType.compareTo(o.alarmType);
  }

  @Override
  public String toString() {
    return "AlarmNotification[type=" + this.alarmType + ", enabled=" + this.enabled + "]";
  }

  public AlarmType getAlarmType() {
    return this.alarmType;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}
