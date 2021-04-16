/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
