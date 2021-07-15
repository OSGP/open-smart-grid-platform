/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.tasks;

import java.util.List;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.opensmartgridplatform.webdevicesimulator.service.SwitchingServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LightSwitchingOff implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(LightSwitchingOff.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private SwitchingServices switchingServices;

  @Autowired private RegisterDevice registerDevice;

  @Autowired private DeviceManagementService deviceManagementService;

  @Override
  public void run() {

    if (Boolean.TRUE.equals(this.deviceManagementService.getLightSwitching())) {

      LOGGER.info("Publiclighting Switching off for devices without Evening/Morning Burners");

      final List<Device> devices = this.deviceRepository.findByHasEveningMorningBurner(false);

      for (final Device device : devices) {
        LOGGER.info(
            "Light switching for : {}: {} ", device.getId(), device.getDeviceIdentification());

        // Switching off Light
        this.switchingServices.lightSwitchOff(device.getId());

        // Send EventNotifications for LightSwitching Off
        LOGGER.info(
            "Sending LIGHT_EVENTS_LIGHT_OFF event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.LIGHT_EVENTS_LIGHT_OFF_VALUE,
            "LIGHT_EVENTS_LIGHT_OFF event occurred on Light Switching off ",
            null);
      }
    }
  }
}
