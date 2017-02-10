/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The manufacturer related steps.
 */
public class FirmwareSteps {

    public static String DEFAULT_NAME = "TestFirmware";

    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepo;

    @Autowired
    private DeviceModelRepository deviceModelRepo;
    @Autowired
    private DeviceRepository deviceRepo;
    @Autowired
    private FirmwareRepository firmwareRepo;

    /**
     * Generic method which adds a firmware using the settings.
     *
     * @param settings
     *            The settings for the firmware to be used.
     * @throws Throwable
     */
    @Given("^a firmware")
    public void aFirmware(final Map<String, String> settings) throws Throwable {

        final DeviceModel deviceModel = this.deviceModelRepo
                .findByModelCode(getString(settings, Keys.DEVICEMODEL_MODELCODE));

        final FirmwareModuleData firmwareModuleData = new FirmwareModuleData(null, null, null, null, null);

        final Firmware entity = new Firmware(deviceModel, getString(settings, Keys.FIRMWARE_FILENAME, ""),
                getString(settings, Keys.FIRMWARE_DESCRIPTION, ""),
                getBoolean(settings, Keys.FIRMWARE_PUSH_TO_NEW_DEVICES, Defaults.FIRMWARE_PUSH_TO_NEW_DEVICE),
                firmwareModuleData);

        this.firmwareRepo.save(entity);

        final DeviceFirmware deviceFirmware = new DeviceFirmware();

        final Device device = this.deviceRepo.findByDeviceIdentification(
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        deviceFirmware.setDevice(device);
        deviceFirmware.setFirmware(entity);

        this.deviceFirmwareRepo.save(deviceFirmware);
    }

    /**
     * Verify whether the entity is created as expected.
     *
     * @param expectedEntity
     * @throws Throwable
     */
    @Then("^the entity firmware exists$")
    public void theEntityFirmwareExists(final Map<String, String> expectedEntity) throws Throwable {
        // TODO: Wait until the stuff is created.
        final Firmware entity = this.firmwareRepo.findByFilename(getString(expectedEntity, Keys.FIRMWARE_FILENAME));

        // Assert.assertEquals(getString(expectedEntity, "ManufacturerId",
        // Defaults.DEFAULT_MANUFACTURER_ID),
        // entity.getManufacturerId());
        // Assert.assertEquals(getBoolean(expectedEntity, "UsesPrefix",
        // Defaults.DEFAULT_MANUFACTURER_USE_PREFIX),
        // entity.isUsePrefix());
    }
}