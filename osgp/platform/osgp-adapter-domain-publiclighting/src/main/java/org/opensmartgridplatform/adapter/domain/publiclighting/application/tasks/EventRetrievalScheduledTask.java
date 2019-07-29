/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
 * Periodic task to fetch events from devices of a manufacturer in case the
 * devices only have events older than X hours. This ensures all devices are
 * contacted, and are allowed to send any new events in their buffers. See
 * {@link SchedulingConfigForEventRetrievalScheduledTask#eventRetrievalScheduledTaskCronTrigger()}
 * and
 * {@link SchedulingConfigForEventRetrievalScheduledTask#eventRetrievalScheduler()}.
 */
@Component
public class EventRetrievalScheduledTask extends BaseTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRetrievalScheduledTask.class);

    @Autowired
    private String eventRetrievalScheduledTaskManufacturerName;

    @Autowired
    private int eventRetrievalScheduledTaskMaximumAllowedAge;

    @Autowired
    private int eventRetrievalScheduledTaskDefaultWaitTime;

    @Autowired
    private int eventRetrievalScheduledTaskMaxBackoff;

    @Override
    public void run() {
        try {
            final Manufacturer manufacturer = this.findManufacturer(this.eventRetrievalScheduledTaskManufacturerName);
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

            List<Device> devicesToContact = this.findDevicesToContact(devices,
                    this.eventRetrievalScheduledTaskMaximumAllowedAge);
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
     * Using the connection details of a device, determine if the device should
     * be contacted. Devices without connection error included in the returned
     * list. Device with connection errors will be included in the returned list
     * if the exponential back off period has elapsed.
     *
     * @param devicesToFilter
     *            List of devices.
     *
     * @return Filtered list of devices based on the exponential back off
     *         calculations.
     */
    private List<Device> filterByExponentialBackOff(final List<Device> devicesToFilter) {

        final List<Device> devicesToContact = new ArrayList<>();

        for (final Device device : devicesToFilter) {
            final String deviceIdentification = device.getDeviceIdentification();

            if (!device.hasConnectionFailures()) {
                LOGGER.info("Device: {} has no connection failures, last successful connection timestamp: {}.",
                        deviceIdentification, device.getLastSuccessfulConnectionTimestamp());
                devicesToContact.add(device);
                continue;
            }

            // waitTimeInMinutes = 2 ^ failedConnectionCount *
            // eventRetrievalScheduledTaskDefaultWaitTime
            final Integer failedConnectionCount = device.getFailedConnectionCount();
            final Integer multiplier = (int) Math.pow(2, failedConnectionCount);
            final Integer waitTimeInMinutes = this.eventRetrievalScheduledTaskDefaultWaitTime * multiplier;
            final DateTime threshold = DateTime.now(DateTimeZone.UTC).minusMinutes(waitTimeInMinutes);
            final DateTime lastFailedConnectionTimestamp = new DateTime(device.getLastFailedConnectionTimestamp())
                    .withZone(DateTimeZone.UTC);
            final DateTime maxThreshold = DateTime.now(DateTimeZone.UTC)
                    .minusHours(this.eventRetrievalScheduledTaskMaxBackoff);

            if (lastFailedConnectionTimestamp.isBefore(maxThreshold)) {
                LOGGER.info(
                        "Device: {} has last failed connection timestamp: {} which is before max threshold: {}. Contacting this device.",
                        deviceIdentification, lastFailedConnectionTimestamp, maxThreshold);
                devicesToContact.add(device);
            } else if (lastFailedConnectionTimestamp.isBefore(threshold)) {
                LOGGER.info(
                        "Device: {} has last failed connection timestamp: {} which is before threshold: {}. Contacting this device.",
                        deviceIdentification, lastFailedConnectionTimestamp, threshold);
                devicesToContact.add(device);
            } else {
                LOGGER.info(
                        "Device: {} has last failed connection timestamp: {} which is after threshold: {}. Not contacting this device.",
                        deviceIdentification, lastFailedConnectionTimestamp, threshold);
            }
        }

        return devicesToContact;
    }
}
