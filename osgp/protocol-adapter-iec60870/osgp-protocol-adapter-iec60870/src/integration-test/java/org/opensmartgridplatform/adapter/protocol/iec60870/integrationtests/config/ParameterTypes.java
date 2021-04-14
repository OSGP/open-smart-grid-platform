/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import io.cucumber.java.ParameterType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;

public class ParameterTypes {

  @ParameterType("DISTRIBUTION_AUTOMATION_DEVICE|LIGHT_MEASUREMENT_RTU|LIGHT_SENSOR")
  public DeviceType deviceType(final String deviceType) {
    return DeviceType.valueOf(deviceType);
  }
}
