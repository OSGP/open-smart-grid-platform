//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.protocol.iec60870.domain;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.ProtocolDeviceCreator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DeviceFactory implements InitializingBean {

  @Autowired private Iec60870LightSensorDeviceCreator lightSensorCreator;

  @Autowired private Iec60870LightMeasurementRtuDeviceCreator lightMeasurementRtuCreator;

  @Autowired private Iec60870DistributionAutomationDeviceCreator daCreator;

  private final Map<DeviceType, ProtocolDeviceCreator<Iec60870Device>> factoryMap = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    this.factoryMap.put(DeviceType.DISTRIBUTION_AUTOMATION_DEVICE, this.daCreator);
    this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_DEVICE, this.lightSensorCreator);
    this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_RTU, this.lightMeasurementRtuCreator);
  }

  public Iec60870Device create(final DeviceType deviceType, final Map<String, String> settings) {
    if (this.factoryMap.containsKey(deviceType)) {
      return this.factoryMap.get(deviceType).apply(settings);
    } else {
      throw new UnsupportedOperationException("Unsuppported DeviceType: " + deviceType);
    }
  }
}
