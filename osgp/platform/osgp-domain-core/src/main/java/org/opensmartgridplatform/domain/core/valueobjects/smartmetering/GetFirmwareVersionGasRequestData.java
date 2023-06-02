//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import lombok.Getter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class GetFirmwareVersionGasRequestData implements ActionRequest {

  private static final long serialVersionUID = -4882051657922326245L;

  @Getter private final String deviceIdentification;

  public GetFirmwareVersionGasRequestData(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.GET_FIRMWARE_VERSION;
  }
}
