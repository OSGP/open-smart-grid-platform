/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform.PlatformDeviceFactory;
import org.opensmartgridplatform.cucumber.platform.publiclighting.domain.protocol.ProtocolDeviceFactory;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceFactory implements InitializingBean {

  @Autowired private PlatformDeviceFactory platformDeviceFactory;

  @Autowired private ProtocolDeviceFactory protocolDeviceFactory;

  private final Map<
          DeviceType, BiFunction<Protocol, Map<String, String>, Collection<AbstractEntity>>>
      deviceFactoryMap = new HashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    this.deviceFactoryMap.put(
        DeviceType.LIGHT_MEASUREMENT_DEVICE, this::createLightMeasurementDevice);
    this.deviceFactoryMap.put(DeviceType.LIGHT_MEASUREMENT_RTU, this::createLightMeasurementRtu);
  }

  public Collection<AbstractEntity> createDevice(
      final DeviceType deviceType, final Protocol protocol, final Map<String, String> settings) {
    if (this.deviceFactoryMap.containsKey(deviceType)) {
      return this.deviceFactoryMap.get(deviceType).apply(protocol, settings);
    } else {
      throw new UnsupportedOperationException("Unsupported DeviceType: " + deviceType);
    }
  }

  private Collection<AbstractEntity> createLightMeasurementDevice(
      final Protocol protocol, final Map<String, String> settings) {
    return Arrays.asList(
        this.platformDeviceFactory.createPlatformDevice(
            DeviceType.LIGHT_MEASUREMENT_DEVICE, protocol, settings),
        this.protocolDeviceFactory.createProtocolDevice(
            DeviceType.LIGHT_MEASUREMENT_DEVICE, protocol, settings));
  }

  private Collection<AbstractEntity> createLightMeasurementRtu(
      final Protocol protocol, final Map<String, String> settings) {
    return Arrays.asList(
        this.platformDeviceFactory.createPlatformDevice(
            DeviceType.LIGHT_MEASUREMENT_RTU, protocol, settings),
        this.protocolDeviceFactory.createProtocolDevice(
            DeviceType.LIGHT_MEASUREMENT_RTU, protocol, settings));
  }
}
