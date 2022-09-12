/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@Getter
@NoArgsConstructor
@ToString
public class TestAlarmSchedulerRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -3773273540392997806L;

  private Date scheduleTime;

  private TestAlarmType alarmType;

  public TestAlarmSchedulerRequestData(final Date scheduleTime, final TestAlarmType testAlarmType) {
    this.scheduleTime = scheduleTime;
    this.alarmType = testAlarmType;
  }

  @Override
  public void validate() throws FunctionalException {
    if (this.scheduleTime == null || new Date().getTime() > this.scheduleTime.getTime()) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING, "The scheduled time should be in the future"));
    }

    if (this.alarmType == null) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(ComponentType.DOMAIN_SMART_METERING, "The alarm type is mandatory"));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_ALARM_NOTIFICATIONS;
  }
}
