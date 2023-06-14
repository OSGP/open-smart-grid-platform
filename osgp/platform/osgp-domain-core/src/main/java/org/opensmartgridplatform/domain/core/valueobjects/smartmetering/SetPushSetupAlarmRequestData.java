// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetPushSetupAlarmRequestData implements ActionRequest {

  private static final long serialVersionUID = -5185544572873050431L;

  private final PushSetupAlarm pushSetupAlarm;

  public SetPushSetupAlarmRequestData(final PushSetupAlarm pushSetupAlarm) {
    this.pushSetupAlarm = pushSetupAlarm;
  }

  public PushSetupAlarm getPushSetupAlarm() {
    return this.pushSetupAlarm;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_PUSH_SETUP_ALARM;
  }
}
