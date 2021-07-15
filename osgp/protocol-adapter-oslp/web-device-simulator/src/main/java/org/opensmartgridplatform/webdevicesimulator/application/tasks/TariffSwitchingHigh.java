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
public class TariffSwitchingHigh implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingHigh.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private RegisterDevice registerDevice;

  @Autowired private SwitchingServices switchingServices;

  @Autowired private DeviceManagementService deviceManagementService;

  @Override
  public void run() {

    if (Boolean.TRUE.equals(this.deviceManagementService.getTariffSwitching())) {
      LOGGER.info("TariffSwitching on for devices");

      final List<Device> devices = this.deviceRepository.findAll();

      for (final Device device : devices) {
        LOGGER.info(
            "Tariff switching on for : {}: {} ", device.getId(), device.getDeviceIdentification());

        // Switching on Tariff
        this.switchingServices.tariffSwitchHigh(device.getId());

        // Send EventNotifications for TariffSwitching Off
        LOGGER.info(
            "Sending TARIFF_EVENTS_TARIFF_ON event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.TARIFF_EVENTS_TARIFF_ON_VALUE,
            "TARIFF_EVENTS_TARIFF_ON event occurred on Tariff Switching high ",
            1);
      }
    }
  }
}
