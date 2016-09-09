/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

import cucumber.api.java.en.Given;

public class DeviceSteps {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private DeviceAuthorizationRepository authorisationRepository;

    /**
     * Generic method which adds a device using the settings.
     *
     * @param settings
     *            The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {

        // Set the required stuff
        final String deviceIdentification = settings.get("DeviceIdentification");
        final Device device = new Device(deviceIdentification);

        // Now set the optional stuff
        if (settings.containsKey("DeviceId")) {
            device.setId(Long.parseLong(settings.get("DeviceId")));
        }
        if (settings.containsKey("IsActivated")) {
            device.setActivated(settings.get("IsActivated").toLowerCase().equals("true"));
        }
        if (settings.containsKey("TechnicalInstallationDate")) {
            device.setTechnicalInstallationDate(
                    new SimpleDateFormat("dd/MM/yyyy").parse(settings.get("TechnicalInstallationDate").toString()));
        }
        if (settings.containsKey("DeviceModelId")) {
            final DeviceModel deviceModel = this.deviceModelRepository
                    .findOne(Long.parseLong(settings.get("DeviceModelId")));
            device.setDeviceModel(deviceModel);
        }
        if (settings.containsKey("Version")) {
            device.setVersion(Long.parseLong(settings.get("Version")));
        }
        if (settings.containsKey("Status")) {
            device.setActivated(settings.get("Status").toLowerCase().equals("active"));
        }
        if (settings.containsKey("Organization")) {
            device.addOrganisation(settings.get("Organization"));
        }
        // TODO: read from configuration parameter
        device.updateRegistrationData(InetAddress.getLocalHost(), "OSLP");

        // TODO: add protocol information in controlled place
        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion("OSLP", "1.0"));
        final DeviceAuthorization auth = device.addAuthorization(
                this.organizationRepository.findByOrganisationIdentification("test-org"), DeviceFunctionGroup.OWNER);
        this.authorisationRepository.save(auth);

        // TODO: Add metadata if required

        this.deviceRepository.save(device);

        if (settings.containsKey("FirmwareVersion")) {
            final Firmware firmware = new Firmware();
            firmware.setVersion(Long.parseLong(settings.get("FirmwareVersion")));

            final DeviceFirmware deviceFirmware = new DeviceFirmware();

            deviceFirmware.setDevice(device);
            deviceFirmware.setFirmware(firmware);

            this.deviceFirmwareRepository.save(deviceFirmware);
        }
    }
}
