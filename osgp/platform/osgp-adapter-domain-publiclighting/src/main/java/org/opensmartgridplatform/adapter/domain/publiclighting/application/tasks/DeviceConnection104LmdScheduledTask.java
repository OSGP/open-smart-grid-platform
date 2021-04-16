/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.util.List;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.SchedulingConfigForDeviceConnection104LmdScheduledTask;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic task to ensure active connection to IEC 60870-5-104 light measurement devices.
 *
 * <p>See {@link
 * SchedulingConfigForDeviceConnection104LmdScheduledTask#deviceConnection104LmdScheduledTaskCronTrigger()}
 * and {@link
 * SchedulingConfigForDeviceConnection104LmdScheduledTask#deviceConnection104LmdTaskScheduler()}.
 */
@Component
public class DeviceConnection104LmdScheduledTask extends BaseTask implements Runnable {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceConnection104LmdScheduledTask.class);

  private static final String PROTOCOL = "60870-5-104";

  @Autowired private int deviceConnection104LmdScheduledTaskMaximumAllowedAge;

  @Override
  public void run() {
    LOGGER.info("Ensuring active connections with IEC 60870-5-104 light measurement devices");
    try {
      final List<LightMeasurementDevice> devices =
          this.findLightMeasurementDevicesByProtocol(PROTOCOL);
      final List<LightMeasurementDevice> devicesToConnect =
          this.findLightMeasurementDevicesToConnect(
              devices, this.deviceConnection104LmdScheduledTaskMaximumAllowedAge);

      this.connectLightMeasurementDevices(devicesToConnect);
    } catch (final Exception e) {
      LOGGER.error(
          "Exception caught ensuring active connection to IEC 60870-5-104 light measurement devices",
          e);
    }
  }
}
