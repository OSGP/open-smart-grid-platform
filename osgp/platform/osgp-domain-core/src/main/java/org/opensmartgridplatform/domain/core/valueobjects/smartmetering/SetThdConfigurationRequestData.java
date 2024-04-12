// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetThdConfigurationRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -6433648884507669671L;

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
