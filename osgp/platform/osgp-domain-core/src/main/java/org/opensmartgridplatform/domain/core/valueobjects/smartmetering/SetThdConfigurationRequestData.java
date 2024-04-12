// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetThdConfigurationRequestData implements ActionRequest {

  private final ThdConfiguration thdConfiguration;

  public SetThdConfigurationRequestData(final ThdConfiguration thdConfiguration) {
    this.thdConfiguration = thdConfiguration;
  }

  public ThdConfiguration getThdConfiguration() {
    return this.thdConfiguration;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_THD_CONFIGURATION;
  }
}
