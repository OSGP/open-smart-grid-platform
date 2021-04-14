/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
