/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.application.config.SchedulingConfig;
import com.alliander.osgp.core.application.services.DeviceRequestMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

/**
 * Periodic task to fetch events from devices of a manufacturer in case the
 * devices have events older than X hours. This ensures all devices are
 * contacted, and are allowed to send any new events in their buffers. See
 * {@link SchedulingConfig#eventRetrievalScheduledTaskCronTrigger()} and
 * {@link SchedulingConfig#eventRetrievalScheduler()}.
 */
@Component
public class EventRetrievalScheduledTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRetrievalScheduledTask.class);

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private String manufacturerName;

    @Autowired
    private int maximumAllowedAge;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final Manufacturer manufacturer = this.findManufacturer(this.manufacturerName);
        if (manufacturer == null) {
            return;
        }

        final List<DeviceModel> deviceModels = this.findDeviceModels(manufacturer);
        if (deviceModels == null || deviceModels.isEmpty()) {
            return;
        }

        final List<Device> devices = this.findDevices(deviceModels);
        if (devices.isEmpty()) {
            return;
        }

        final List<Device> devicesToContact = this.findDevicesToContact(devices);
        if (devicesToContact.isEmpty()) {
            return;
        }

        this.contactDevices(devicesToContact);
    }

    /**
     * Try to find a manufacturer by name (case sensitive).
     */
    private Manufacturer findManufacturer(final String name) {
        LOGGER.info("Trying to find manufacturer for name: {}", name);
        final Manufacturer manufacturer = this.manufacturerRepository.findByName(name);
        if (manufacturer == null) {
            LOGGER.warn("No manufacturer found for name: {}", name);
        } else {
            LOGGER.info("Manufacturer found for name: {}", name);
        }
        return manufacturer;
    }

    /**
     * Try to find all device models for a manufacturer.
     */
    private List<DeviceModel> findDeviceModels(final Manufacturer manufacturer) {
        LOGGER.info("Trying to find device models for manufacturer: {}", manufacturer.getName());
        final List<DeviceModel> deviceModels = this.deviceModelRepository.findByManufacturerId(manufacturer);
        if (deviceModels == null) {
            LOGGER.warn("No device models found for manufacturer with name: {}, deviceModels == null",
                    manufacturer.getName());
        } else if (deviceModels.isEmpty()) {
            LOGGER.warn("No device models found for manufacturer with name: {}, deviceModels.isEmpty()",
                    manufacturer.getName());
        } else {
            LOGGER.info("{} device models found for manufacturer with name: {}", deviceModels.size(),
                    manufacturer.getName());
            for (final DeviceModel deviceModel : deviceModels) {
                LOGGER.info(" deviceModel: {}", deviceModel.getModelCode());
            }
        }
        return deviceModels;
    }

    /**
     * Try to find all devices which are not 'in maintenance' for a list of
     * device models.
     */
    private List<Device> findDevices(final List<DeviceModel> deviceModels) {
        LOGGER.info("Trying to find devices for device models for manufacturer...");
        final List<Device> devices = new ArrayList<>();
        for (final DeviceModel deviceModel : deviceModels) {
            final List<Device> devs = this.deviceRepository.findByDeviceModelAndDeviceTypeAndInMaintenance(deviceModel,
                    Ssld.SSLD_TYPE, false);
            devices.addAll(devs);
        }
        if (devices.isEmpty()) {
            LOGGER.warn("No devices found for device models for manufacturer");
        } else {
            LOGGER.info("{} devices found for device models for manufacturer", devices.size());
        }
        return devices;
    }

    /**
     * Filter a list of given devices to determine which devices should be
     * contacted. The filtering uses the age of the latest event in comparison
     * with 'maximumAllowedAge'.
     */
    private List<Device> findDevicesToContact(final List<Device> devices) {
        List<Object> listOfObjectArrays = this.eventRepository.findLatestEventForEveryDevice(devices);
        LOGGER.info("listOfObjectArrays.size(): {}", listOfObjectArrays.size());

        final Date maxAge = DateTime.now(DateTimeZone.UTC).minusHours(this.maximumAllowedAge).toDate();
        LOGGER.info("maxAge: {}", maxAge);

        final Map<Long, Date> map = new HashMap<>();
        for (final Object objectArray : listOfObjectArrays) {
            final Object[] array = (Object[]) objectArray;
            final Long eventDeviceId = (Long) array[0];
            final Date timestamp = (Date) array[1];
            LOGGER.info("eventDeviceId: {}, timestamp: {}", eventDeviceId, timestamp);
            if (this.isEventOlderThanMaxInterval(maxAge, timestamp)) {
                map.put(eventDeviceId, timestamp);
            }
        }

        listOfObjectArrays = null;

        final List<Device> devicesToContact = this.deviceRepository.findAll(map.keySet());
        for (final Device device : devicesToContact) {
            LOGGER.info("device: {}, id: {}", device.getDeviceIdentification(), device.getId());
        }
        return devicesToContact;
    }

    /**
     * Determine if an event is older than X hours as indicated by maxAge.
     */
    private boolean isEventOlderThanMaxInterval(final Date maxAge, final Date event) {
        if (event == null) {
            // In case the event instance is null, try to contact the device.
            LOGGER.info("Event instance is null");
            return true;
        }
        final boolean result = event.before(maxAge);
        LOGGER.info("event date time: {}, current date time minus {} hours: {}, is event before? : {}", event,
                this.maximumAllowedAge, maxAge, result);
        return result;
    }

    /**
     * Send a request message to protocol adapter component for each device to
     * contact.
     */
    private void contactDevices(final List<Device> devicesToContact) {
        for (final Device device : devicesToContact) {
            final ProtocolRequestMessage protocolRequestMessage = this.createProtocolRequestMessage(device);
            if (protocolRequestMessage != null) {
                try {
                    LOGGER.info("Attempting to contact device: {}", device.getDeviceIdentification());
                    this.deviceRequestMessageService.processMessage(protocolRequestMessage);
                } catch (final FunctionalException e) {
                    LOGGER.error("Exception during sending of protocol request message", e);
                }
            }
        }
    }

    /**
     * Create a message to send to the protocol adapter component.
     */
    private ProtocolRequestMessage createProtocolRequestMessage(final Device device) {
        final String deviceIdentification = device.getDeviceIdentification();
        // Try to use the identification of the owner organization.
        final String organisation = device.getOwner() == null ? "" : device.getOwner().getOrganisationIdentification();
        // Creating message with empty CorrelationUID, in order to prevent a
        // response from protocol adapter component.
        final String correlationUid = "";
        final DeviceFunction deviceFunction = DeviceFunction.GET_STATUS;
        final String domain = "CORE";
        final String domainVersion = "1.0";

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisation, correlationUid, deviceFunction.toString(), 0);

        String ipAddress = null;
        if (device.getNetworkAddress() == null) {
            // In case the device does not have a known IP address, don't send
            // a request message.
            LOGGER.warn("Unable to create protocol request message because the IP address is empty for device: {}",
                    deviceIdentification);
            return null;
        } else {
            ipAddress = device.getNetworkAddress().getHostAddress();
        }

        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisation, deviceIdentification,
                null);

        return new ProtocolRequestMessage.Builder().deviceMessageMetadata(deviceMessageMetadata).domain(domain)
                .domainVersion(domainVersion).ipAddress(ipAddress).request(requestMessage).build();
    }
}
