/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.EventNotificationToBeSent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceManagementService {

    private static final List<EventNotificationToBeSent> EVENT_NOTIFICATION_TO_BE_SENT = new ArrayList<>();

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private Boolean checkboxDeviceRegistrationValue;

    @Autowired
    private Boolean checkboxDeviceRebootValue;

    @Autowired
    private Boolean checkboxLightSwitchingValue;

    @Autowired
    private Boolean checkboxTariffSwitchingValue;

    @Autowired
    private Boolean checkboxEventNotificationValue;

    @Autowired
    private Integer rebootDelayInSeconds;

    public List<EventNotificationToBeSent> getEventNotificationToBeSent() {
        return EVENT_NOTIFICATION_TO_BE_SENT;
    }

    protected void setDeviceRepository(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> findAllDevices() {
        return this.deviceRepository.findAllOrderById();
    }

    public Page<Device> findPageOfDevices(final String deviceIdentification, final int pageNumber, final int pageSize,
            final String sortDirection) {
        final PageRequest request = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDirection),
                "id");

        if (!StringUtils.isEmpty(deviceIdentification)) {
            return this.deviceRepository.findByDeviceIdentification(deviceIdentification, request);
        }

        return this.deviceRepository.findAll(request);
    }

    public Device findDevice(final Long id) {
        return this.deviceRepository.getOne(id);
    }

    public Device findDevice(final String deviceUid) {
        return this.deviceRepository.findByDeviceUid(deviceUid);
    }

    public Device addDevice(final Device device) {
        return this.deviceRepository.save(device);
    }

    public Device updateDevice(final Device device) {
        return this.deviceRepository.save(device);
    }

    public Boolean getDevRegistration() {
        return this.checkboxDeviceRegistrationValue;
    }

    public Boolean getTariffSwitching() {
        return this.checkboxTariffSwitchingValue;
    }

    public Boolean getLightSwitching() {
        return this.checkboxLightSwitchingValue;
    }

    public Boolean getDevReboot() {
        return this.checkboxDeviceRebootValue;
    }

    public Boolean getEventNotification() {
        return this.checkboxEventNotificationValue;
    }

    public void setdeviceRegistration(final Boolean deviceRegistration) {
        this.checkboxDeviceRegistrationValue = deviceRegistration;
    }

    public void setDeviceReboot(final Boolean deviceReboot) {
        this.checkboxDeviceRebootValue = deviceReboot;
    }

    public void setTariffSwitching(final Boolean tariffSwitching) {
        this.checkboxTariffSwitchingValue = tariffSwitching;
    }

    public void setLightSwitching(final Boolean lightSwitching) {
        this.checkboxLightSwitchingValue = lightSwitching;
    }

    public void setEventNotification(final Boolean eventNotification) {
        this.checkboxEventNotificationValue = eventNotification;
    }

    public int getRebootDelay() {
        return this.rebootDelayInSeconds;
    }

    public void setRebootDelay(final int rebootDelayInSeconds) {
        this.rebootDelayInSeconds = rebootDelayInSeconds;
    }
}
