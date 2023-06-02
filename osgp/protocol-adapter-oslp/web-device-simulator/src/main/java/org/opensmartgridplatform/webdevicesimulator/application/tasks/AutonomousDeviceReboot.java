//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.application.tasks;

import java.util.List;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutonomousDeviceReboot implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousDeviceReboot.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private RegisterDevice registerDevice;

  @Autowired private DeviceManagementService deviceManagementService;

  @Override
  public void run() {
    if (this.deviceManagementService.getDevReboot()) {
      LOGGER.info("Rebooting devices");

      final List<Device> devices = this.deviceRepository.findAll();

      for (final Device device : devices) {
        LOGGER.info(
            "Autonomous device reboot for : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());

        // registering device with hasSchedule as false
        LOGGER.info(
            "device registration for : {}: {} ", device.getId(), device.getDeviceIdentification());
        this.registerDevice.sendRegisterDeviceCommand(device.getId(), false);

        // Confirm device registration
        LOGGER.info(
            "device register confirmation for : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendConfirmDeviceRegistrationCommand(device.getId());

        // Sending events on device reboot
        LOGGER.info(
            "Sending TARIFF_EVENTS_TARIFF_OFF event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.TARIFF_EVENTS_TARIFF_OFF_VALUE,
            "TARIFF_EVENTS_TARIFF_OFF_VALUE event occurred on Device reboot ",
            null);

        LOGGER.info(
            "Sending LIGHT_EVENTS_LIGHT_OFF event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.LIGHT_EVENTS_LIGHT_OFF_VALUE,
            "LIGHT_EVENTS_LIGHT_OFF event occurred on Device reboot ",
            null);

        LOGGER.info(
            "Sending DIAG_EVENTS_GENERAL event for device : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendEventNotificationCommand(
            device.getId(),
            Oslp.Event.DIAG_EVENTS_GENERAL_VALUE,
            "DIAG_EVENTS_GENERAL event occurred on Device reboot ",
            null);
      }
    }
  }
}
