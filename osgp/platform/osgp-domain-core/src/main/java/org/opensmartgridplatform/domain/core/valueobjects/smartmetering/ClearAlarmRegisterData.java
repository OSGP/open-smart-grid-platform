//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class ClearAlarmRegisterData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 1640673610366821375L;

  @Override
  public void validate() throws FunctionalException {
    // no validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.CLEAR_ALARM_REGISTER;
  }
}
