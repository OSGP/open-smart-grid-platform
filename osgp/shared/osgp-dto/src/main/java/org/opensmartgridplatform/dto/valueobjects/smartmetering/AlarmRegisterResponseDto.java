// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Set;
import java.util.TreeSet;

public class AlarmRegisterResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 2319359505656305783L;

  private final Set<AlarmTypeDto> alarmTypes;

  public AlarmRegisterResponseDto(final Set<AlarmTypeDto> alarmTypes) {
    this.alarmTypes = new TreeSet<>(alarmTypes);
  }

  @Override
  public String toString() {
    return "AlarmTypes[" + this.alarmTypes + "]";
  }

  public Set<AlarmTypeDto> getAlarmTypes() {
    return new TreeSet<>(this.alarmTypes);
  }
}
