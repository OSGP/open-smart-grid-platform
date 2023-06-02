//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.ToString;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@Getter
@ToString
public class TestAlarmSchedulerRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -3773273540392997806L;

  private final Date scheduleTime;

  private final TestAlarmType alarmType;

  public TestAlarmSchedulerRequestData(final Date scheduleTime, final TestAlarmType alarmType) {
    this.scheduleTime = scheduleTime;
    this.alarmType = alarmType;
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
