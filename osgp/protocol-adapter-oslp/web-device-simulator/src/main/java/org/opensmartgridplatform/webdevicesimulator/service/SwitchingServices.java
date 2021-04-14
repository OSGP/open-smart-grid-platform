/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.service;

import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.springframework.beans.factory.annotation.Autowired;

public class SwitchingServices {

  @Autowired private DeviceManagementService deviceManagementService;

  public void tariffSwitchHigh(final long deviceId) {
    // Find device
    final Device device = this.deviceManagementService.findDevice(deviceId);
    device.setTariffOn(false);
    this.deviceManagementService.updateDevice(device);
  }

  public void tariffSwitchLow(final long deviceId) {
    // Find device
    final Device device = this.deviceManagementService.findDevice(deviceId);
    device.setTariffOn(true);
    this.deviceManagementService.updateDevice(device);
  }

  public void lightSwitchOn(final long deviceId) {
    // Find device
    final Device device = this.deviceManagementService.findDevice(deviceId);
    device.setLightOn(true);
    this.deviceManagementService.updateDevice(device);
  }

  public void lightSwitchOff(final long deviceId) {
    // Find device
    final Device device = this.deviceManagementService.findDevice(deviceId);
    device.setLightOn(false);
    this.deviceManagementService.updateDevice(device);
  }
}
