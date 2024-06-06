// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class AlarmRegister extends ActionResponse implements Serializable {

  private static final long serialVersionUID = 2319359505656305783L;

  private final Set<AlarmType> alarmTypes;

  public AlarmRegister(final Set<AlarmType> alarmTypes) {
    this.alarmTypes = new TreeSet<AlarmType>(alarmTypes);
  }

  @Override
  public String toString() {
    return "AlarmTypes[" + this.alarmTypes + "]";
  }

  public Set<AlarmType> getAlarmTypes() {
    return new TreeSet<>(this.alarmTypes);
  }
}
