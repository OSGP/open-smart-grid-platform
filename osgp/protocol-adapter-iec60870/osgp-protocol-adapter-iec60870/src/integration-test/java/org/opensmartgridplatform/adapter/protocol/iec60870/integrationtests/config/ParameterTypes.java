//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import io.cucumber.java.ParameterType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;

public class ParameterTypes {

  @ParameterType("DISTRIBUTION_AUTOMATION_DEVICE|LIGHT_MEASUREMENT_RTU|LIGHT_SENSOR")
  public DeviceType deviceType(final String deviceType) {
    return DeviceType.valueOf(deviceType);
  }
}
