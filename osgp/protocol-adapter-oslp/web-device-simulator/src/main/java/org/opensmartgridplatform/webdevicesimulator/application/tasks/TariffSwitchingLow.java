// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
public class TariffSwitchingLow implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(TariffSwitchingLow.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private RegisterDevice registerDevice;

  @Autowired private SwitchingServices switchingServices;

  @Autowired private DeviceManagementService deviceManagementService;

  @Override
  public void run() {

    if (Boolean.TRUE.equals(this.deviceManagementService.getTariffSwitching())) {
      LOGGER.info("traiff Switching off for devices");

      final List<Device> devices = this.deviceRepository.findAll();

      for (final Device device : devices) {
        LOGGER.info(
            "Tariff switching for : {}: {} ", device.getId(), device.getDeviceIdentification());

        // Switching off Tariff
        this.switchingServices.tariffSwitchLow(device.getId());

        // Send EventNotifications for TariffSwitching Off
        LOGGER.info(
            "Sending TARIFF_EVENTS_TARIFF_OFF event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.TARIFF_EVENTS_TARIFF_OFF_VALUE,
            "TARIFF_EVENTS_TARIFF_OFF event occurred on Tariff Switching low ",
            1);
      }
    }
  }
}
