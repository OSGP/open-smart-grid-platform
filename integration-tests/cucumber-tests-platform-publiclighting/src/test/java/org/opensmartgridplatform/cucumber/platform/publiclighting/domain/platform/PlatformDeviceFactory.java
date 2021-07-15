/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlatformDeviceFactory implements InitializingBean {

  @Autowired private LmdDeviceCreator lmdCreator;

  @Autowired private LightMeasurementRtuDeviceCreator lmgCreator;

  private final Map<DeviceType, PlatformDeviceCreator<? extends Device>> factoryMap =
      new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_DEVICE, this.lmdCreator);
    this.factoryMap.put(DeviceType.LIGHT_MEASUREMENT_RTU, this.lmgCreator);
  }

  public Device createPlatformDevice(
      final DeviceType deviceType, final Protocol protocol, final Map<String, String> settings) {

    if (this.factoryMap.containsKey(deviceType)) {
      return this.factoryMap.get(deviceType).apply(protocol, settings);
    } else {
      throw new UnsupportedOperationException("Unsupported DeviceType: " + deviceType);
    }
  }
}
