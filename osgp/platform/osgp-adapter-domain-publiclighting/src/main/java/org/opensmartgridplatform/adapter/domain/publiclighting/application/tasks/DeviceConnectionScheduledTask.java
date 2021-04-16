/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.util.List;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.SchedulingConfigForDeviceConnectionScheduledTask;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic task to ensure active connection to devices of a given manufacturer. See {@link
 * SchedulingConfigForDeviceConnectionScheduledTask#deviceConnectionScheduledTaskCronTrigger()} and
 * {@link SchedulingConfigForDeviceConnectionScheduledTask#deviceConnectionTaskScheduler()}.
 */
@Component
public class DeviceConnectionScheduledTask extends BaseTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConnectionScheduledTask.class);

  @Autowired private String deviceConnectionScheduledTaskManufacturerName;

  @Autowired private int deviceConnectionScheduledTaskMaximumAllowedAge;

  @Override
  public void run() {
    LOGGER.info(
        "Ensuring active connections with LMD devices of manufacturer '{}'",
        this.deviceConnectionScheduledTaskManufacturerName);
    try {
      final Manufacturer manufacturer =
          this.findManufacturer(this.deviceConnectionScheduledTaskManufacturerName);
      if (manufacturer == null) {
        return;
      }

      final List<DeviceModel> deviceModels = this.findDeviceModels(manufacturer);
      if (deviceModels == null || deviceModels.isEmpty()) {
        return;
      }

      final List<Device> devices = this.findDevices(deviceModels, LightMeasurementDevice.LMD_TYPE);
      if (devices.isEmpty()) {
        return;
      }

      final List<Device> devicesToContact =
          this.findDevicesToContact(devices, this.deviceConnectionScheduledTaskMaximumAllowedAge);
      if (devicesToContact == null || devicesToContact.isEmpty()) {
        return;
      }

      this.contactDevices(devicesToContact, DeviceFunction.GET_LIGHT_SENSOR_STATUS);
    } catch (final Exception e) {
      LOGGER.error("Exception caught during DeviceConnectionScheduledTask.run()", e);
    }
  }
}
