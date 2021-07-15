/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.domain;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.springframework.stereotype.Component;

@Component
public class Iec60870LightSensorDeviceCreator extends AbstractIec60870DeviceCreator {

  private static final String DEFAULT_DEVICE_IDENTIFICATION = "LMD-1";
  private static final String DEFAULT_GATEWAY_DEVICE_IDENTIFICATION = "RTU-1";

  @Override
  public Iec60870Device apply(final Map<String, String> settings) {
    final Iec60870Device device =
        new Iec60870Device(this.deviceIdentification(settings), this.deviceType());
    device.setGatewayDeviceIdentification(this.gatewayDeviceIdentification(settings));
    device.setInformationObjectAddress(this.informationObjectAddress(settings));
    // Field common address is not needed for light measurement device, but
    // is currently not nullable...
    device.setCommonAddress(-1);

    return device;
  }

  @Override
  protected DeviceType deviceType() {
    return DeviceType.LIGHT_SENSOR;
  }

  @Override
  protected String deviceIdentification() {
    return DEFAULT_DEVICE_IDENTIFICATION;
  }

  @Override
  protected String gatewayDeviceIdentification() {
    return DEFAULT_GATEWAY_DEVICE_IDENTIFICATION;
  }
}
