// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetFirmwareVersionRequestData implements ActionRequest {

  private static final long serialVersionUID = 5044944004218551417L;

  @Override
  public void validate() throws FunctionalException {
    // No validation needed

  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_FIRMWARE_VERSION;
  }
}
