/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.core.builders.DeviceModelBuilder;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceModelSteps extends GlueBase {

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    /**
     * Generic method which adds a device model using the settings.
     *
     * @param settings
     *            The settings for the device model to be used.
     * @throws Throwable
     */
    @Given("^a device model")
    public DeviceModel aDeviceModel(final Map<String, String> settings) {
        return this.insertDeviceModel(settings);
    }

    /**
     * Generic method to check if the device model is created as expected in the
     * database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity device model exists$")
    public void theEntityDeviceModelExists(final Map<String, String> expectedEntity) throws Throwable {
        final String modelCode = getString(expectedEntity, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE);
        final String modelDescription = getString(expectedEntity, PlatformKeys.KEY_DEVICE_MODEL_DESCRIPTION,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);
        final boolean modelMetered = getBoolean(expectedEntity, PlatformKeys.KEY_DEVICE_MODEL_METERED,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_METERED);

        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(
                getString(expectedEntity, PlatformKeys.MANUFACTURER_CODE, PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

        final List<DeviceModel> entityList = this.deviceModelRepository.findByManufacturer(manufacturer);

        for (final DeviceModel deviceModel : entityList) {
            if (deviceModel.getModelCode().equals(modelCode)) {
                Assert.assertEquals(modelDescription, deviceModel.getDescription());
                Assert.assertEquals(modelMetered, deviceModel.isMetered());
                return;
            }
        }
        Assert.assertFalse(true);
    }

    /**
     * Generic method to check if the device model is NOT created as expected in
     * the database.
     *
     * @param entity
     *            The settings.
     * @throws Throwable
     */
    @Then("^the entity device model does not exist$")
    public void theEntityDeviceModelDoesNotExists(final Map<String, String> entity) throws Throwable {
        final String modelCode = getString(entity, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
                PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE);

        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(
                getString(entity, PlatformKeys.MANUFACTURER_CODE, PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

        final List<DeviceModel> entityList = this.deviceModelRepository.findByManufacturer(manufacturer);

        for (final DeviceModel deviceModel : entityList) {
            Assert.assertNotEquals(deviceModel.getModelCode(), modelCode);
        }
    }

    /**
     * This inserts a default DeviceModel
     *
     * @param settings
     * @return
     */
    public DeviceModel insertDeviceModel(final Map<String, String> settings) {

        final Manufacturer manufacturer = this.manufacturerRepository.findByName(
                getString(settings, PlatformKeys.MANUFACTURER_NAME, PlatformDefaults.DEFAULT_MANUFACTURER_NAME));
        final DeviceModel deviceModel = new DeviceModelBuilder().withSettings(settings).withManufacturer(manufacturer)
                .build();
        return this.deviceModelRepository.save(deviceModel);
    }
}
