/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The manufacturer related steps.
 */
public class ManufacturerSteps {

    @Autowired
    private ManufacturerRepository repo;

    private String DEFAULT_MANUFACTURER_ID = "1";
    public static String DEFAULT_NAME = "Test Manufacturer";
    private Boolean DEFAULT_USEPREFIX = true;

    /**
     * Generic method which adds a manufacturer using the settings.
     * 
     * @param settings
     *            The settings for the manufacturer to be used.
     * @throws Throwable
     */
    @Given("^a manufacturer")
    public void aManufacturer(final Map<String, String> settings) throws Throwable {

        Manufacturer entity = new Manufacturer(
                getString(settings, Keys.MANUFACTURER_ID, DEFAULT_MANUFACTURER_ID),
                getString(settings, "Name", DEFAULT_NAME), 
                getBoolean(settings, "UsePrefix", DEFAULT_USEPREFIX));

        repo.save(entity);
    }

    /**
     * Verify whether the entity is created as expected.
     * 
     * @param expectedEntity
     * @throws Throwable
     */
    @Then("^the entity manufacturer exists$")
    public void theEntityManufacturerExists(final Map<String, String> expectedEntity) throws Throwable {
        // TODO: Wait until the stuff is created.
        Manufacturer entity = repo.findByName(getString(expectedEntity, "Name", Defaults.DEFAULT_MANUFACTURER_NAME));

        Assert.assertEquals(getString(expectedEntity, "ManufacturerId", Defaults.DEFAULT_MANUFACTURER_ID),
                entity.getManufacturerId());
        Assert.assertEquals(getBoolean(expectedEntity, "UsesPrefix", Defaults.DEFAULT_MANUFACTURER_USE_PREFIX),
                entity.isUsePrefix());
    }
}