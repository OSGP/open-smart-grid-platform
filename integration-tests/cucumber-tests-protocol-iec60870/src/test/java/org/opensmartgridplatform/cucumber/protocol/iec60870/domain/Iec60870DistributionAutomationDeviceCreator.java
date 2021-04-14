/**
 * Copyright 2021 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.domain;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DistributionAutomationDeviceCreator extends AbstractIec60870DeviceCreator {

  private static final String DEFAULT_DEVICE_IDENTIFICATION = "DA-1";

  @Override
  public Iec60870Device apply(final Map<String, String> settings) {
    final Iec60870Device device =
        new Iec60870Device(this.deviceIdentification(settings), this.deviceType());
    device.setPort(this.port(settings));
    device.setCommonAddress(this.commonAddress(settings));
    return device;
  }

  @Override
  protected DeviceType deviceType() {
    return DeviceType.DISTRIBUTION_AUTOMATION_DEVICE;
  }

  @Override
  protected String deviceIdentification() {
    return DEFAULT_DEVICE_IDENTIFICATION;
  }
}
