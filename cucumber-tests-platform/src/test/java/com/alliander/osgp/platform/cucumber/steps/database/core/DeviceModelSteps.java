/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceModelSteps {

    @Autowired
    private DeviceModelRepository repo;

    @Autowired
    private ManufacturerRepository manufacturerRepo;

    private final Boolean DEFAULT_FILESTORAGE = true;

    /**
     * Generic method which adds a device model using the settings.
     *
     * @param settings
     *            The settings for the device model to be used.
     * @throws Throwable
     */
    @Given("^a device model")
    public void aDeviceModel(final Map<String, String> settings) throws Throwable {
        this.insertDeviceModel(settings);
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

        final DeviceModel entity = this.repo
                .findByModelCode(getString(expectedEntity, "ModelCode", Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));

        Assert.assertEquals(getString(expectedEntity, Keys.MANUFACTURER_ID, Defaults.DEFAULT_MANUFACTURER_ID),
                entity.getManufacturerId().getManufacturerId());
        Assert.assertEquals(
                getString(expectedEntity, Keys.KEY_DEVICE_MODEL_DESCRIPTION, Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION),
                entity.getDescription());
        Assert.assertEquals(
                getBoolean(expectedEntity, Keys.KEY_DEVICE_MODEL_METERED, Defaults.DEFAULT_DEVICE_MODEL_METERED),
                entity.isMetered());
    }

    /**
     * This inserts a default DeviceModel
     *
     * @param settings
     * @return
     */
    public DeviceModel insertDeviceModel(Map<String, String> settings) {
        // Get the given manufacturer (or the default).
        final Manufacturer manufacturer = this.manufacturerRepo
                .findByName(getString(settings, "ManufacturerName", ManufacturerSteps.DEFAULT_NAME));

        final String description = getString(settings, Keys.KEY_DESCRIPTION, Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);

        // Create the new device model.
        final DeviceModel entity = new DeviceModel(manufacturer,
                getString(settings, Keys.KEY_DEVICE_MODEL_MODELCODE, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE),
                description, getBoolean(settings, Keys.KEY_DEVICE_MODEL_FILESTORAGE, this.DEFAULT_FILESTORAGE));

        entity.updateData(description,
                getBoolean(settings, Keys.KEY_DEVICE_MODEL_METERED, Defaults.DEFAULT_DEVICE_MODEL_METERED));
        entity.setVersion(getLong(settings, "Version"));

        this.repo.save(entity);

        return entity;
    }
}
