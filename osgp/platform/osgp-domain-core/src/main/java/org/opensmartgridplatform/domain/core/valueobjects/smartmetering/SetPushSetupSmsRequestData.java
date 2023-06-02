//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetPushSetupSmsRequestData implements ActionRequest {

  private static final long serialVersionUID = 6093319027662713873L;

  private final PushSetupSms pushSetupSms;

  public SetPushSetupSmsRequestData(final PushSetupSms pushSetupSms) {
    this.pushSetupSms = pushSetupSms;
  }

  public PushSetupSms getPushSetupSms() {
    return this.pushSetupSms;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed

  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_PUSH_SETUP_SMS;
  }
}
