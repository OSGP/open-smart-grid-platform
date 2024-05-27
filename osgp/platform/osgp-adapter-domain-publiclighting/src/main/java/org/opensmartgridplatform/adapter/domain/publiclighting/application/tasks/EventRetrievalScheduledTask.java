// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.config.SchedulingConfigForEventRetrievalScheduledTask;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Periodic task to fetch events from devices of a manufacturer in case the devices only have events
 * older than X hours. This ensures all devices are contacted, and are allowed to send any new
 * events in their buffers. See {@link
 * SchedulingConfigForEventRetrievalScheduledTask#eventRetrievalScheduledTaskCronTrigger()} and
 * {@link SchedulingConfigForEventRetrievalScheduledTask#eventRetrievalScheduler()}.
 */
@Component
public class EventRetrievalScheduledTask extends BaseTask implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventRetrievalScheduledTask.class);

  /** Name of the manufacturer used to search for devices. */
  @Autowired private String eventRetrievalScheduledTaskManufacturerName;

  /** Maximum age in hours for events of devices. */
  @Autowired private int eventRetrievalScheduledTaskMaximumAllowedAge;

  /** Multiplier for exponential back off calculation. */
  @Autowired private int eventRetrievalScheduledTaskBackOffMultiplier;

  /** Wait time in minutes for exponential back off. */
  @Autowired private int eventRetrievalScheduledTaskDefaultWaitTime;

  /** Maximum wait time in minutes for exponential back off. */
  @Autowired private int eventRetrievalScheduledTaskMaxBackoff;

  /** Hysteresis in minutes for the exponential back off calculation. */
  @Autowired private int eventRetrievalScheduledTaskHysteresis;

  @Override
  public void run() {
    try {
      final Manufacturer manufacturer =
          this.findManufacturer(this.eventRetrievalScheduledTaskManufacturerName);
      if (manufacturer == null) {
        return;
      }

      final List<DeviceModel> deviceModels = this.findDeviceModels(manufacturer);
      if (deviceModels == null || deviceModels.isEmpty()) {
        return;
      }

      final List<Device> devices = this.findDevices(deviceModels, Ssld.SSLD_TYPE);
      if (devices.isEmpty()) {
        return;
      }

      List<Device> devicesToContact =
          this.findDevicesToContact(devices, this.eventRetrievalScheduledTaskMaximumAllowedAge);
      if (devicesToContact == null || devicesToContact.isEmpty()) {
        return;
      }

      devicesToContact = this.filterByExponentialBackOff(devicesToContact);

      this.contactDevices(devicesToContact, DeviceFunction.GET_LIGHT_STATUS);
    } catch (final Exception e) {
      LOGGER.error("Exception caught during EventRetrievalScheduledTask.run()", e);
    }
  }

  /**
   * Using the connection details of a device, determine if the device should be contacted.
   *
   * <ul>
   *   <li>Devices without connection error will always be included in the returned list.
   *   <li>Devices with connection errors will only be included in the returned list if the
   *       exponential back off period has elapsed. The exponential back off period will be
   *       calculated using the failed connection counter.
   *
   * @param devicesToFilter List of devices.
   * @return Filtered list of devices based on the exponential back off calculations.
   */
  public List<Device> filterByExponentialBackOff(final List<Device> devicesToFilter) {

    final Predicate<Device> hasNoConnectionFailure = d -> !d.hasConnectionFailures();

    final Predicate<Device> hasLastConnectionFailureBeforeThreshold =
        this::calculateThresholdForDevice;

    return devicesToFilter.stream()
        .filter(hasNoConnectionFailure.or(hasLastConnectionFailureBeforeThreshold))
        .toList();
  }

  private boolean calculateThresholdForDevice(final Device device) {
    final ZonedDateTime threshold = this.determineMinimalDeviceThreshold(device);

    final boolean isBefore =
        ZonedDateTime.ofInstant(
                device.getLastFailedConnectionTimestamp().toInstant(), ZoneId.of("UTC"))
            .isBefore(threshold);

    if (isBefore) {
      LOGGER.info(
          "Device: {} has last failed connection timestamp: {} which is before threshold: {}. Contacting this device.",
          device.getDeviceIdentification(),
          device.getLastFailedConnectionTimestamp(),
          threshold);
    } else {
      LOGGER.info(
          "Device: {} has last failed connection timestamp: {} which is after threshold: {}. Not contacting this device.",
          device.getDeviceIdentification(),
          device.getLastFailedConnectionTimestamp(),
          threshold);
    }

    return isBefore;
  }

  private ZonedDateTime determineMinimalDeviceThreshold(final Device device) {
    final int failedConnectionCount = device.getFailedConnectionCount();

    final int waitTime =
        Math.min(
            this.calculateDeviceBackOff(failedConnectionCount),
            this.eventRetrievalScheduledTaskMaxBackoff);

    final ZonedDateTime threshold =
        ZonedDateTime.now(ZoneId.of("UTC"))
            .minusMinutes(waitTime)
            .plusMinutes(this.eventRetrievalScheduledTaskHysteresis)
            .withNano(0);

    LOGGER.info(
        "Determined threshold: {} for device: {} based on failed connection count: {} and hysteresis: {}",
        threshold,
        device.getDeviceIdentification(),
        failedConnectionCount,
        this.eventRetrievalScheduledTaskHysteresis);

    return threshold;
  }

  private int calculateDeviceBackOff(final int failedConnectionCount) {

    return ((int)
            Math.pow(this.eventRetrievalScheduledTaskBackOffMultiplier, failedConnectionCount))
        * this.eventRetrievalScheduledTaskDefaultWaitTime;
  }
}
