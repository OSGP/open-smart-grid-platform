//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetPushSetupAlarmRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -1031557913336969599L;

  private PushSetupAlarmDto pushSetupAlarm;

  public SetPushSetupAlarmRequestDto(final PushSetupAlarmDto pushSetupAlarm) {
    this.pushSetupAlarm = pushSetupAlarm;
  }

  public PushSetupAlarmDto getPushSetupAlarm() {
    return this.pushSetupAlarm;
  }
}
