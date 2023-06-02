//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.webdevicesimulator.application.tasks;

import java.util.List;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutonomousDeviceRegister implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousDeviceRegister.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DeviceManagementService deviceManagementService;

  @Autowired private RegisterDevice registerDevice;

  @Override
  public void run() {

    if (this.deviceManagementService.getDevRegistration()) {

      LOGGER.info("Registering devices");

      final List<Device> devices = this.deviceRepository.findAll();

      for (final Device device : devices) {
        LOGGER.info(
            "Autonomous device register for : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendRegisterDeviceCommand(device.getId(), false);
        LOGGER.info(
            "Autonomous device register confirmation for : {}: {} ",
            device.getId(),
            device.getDeviceIdentification());
        this.registerDevice.sendConfirmDeviceRegistrationCommand(device.getId());
      }
    }
  }
}
