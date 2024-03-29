// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetConfigurationObjectRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -381163520662276868L;

  private final ConfigurationObject configurationObject;

  public SetConfigurationObjectRequestData(final ConfigurationObject configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObject getConfigurationObject() {
    return this.configurationObject;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_CONFIGURATION_OBJECT;
  }
}
