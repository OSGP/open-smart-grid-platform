/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.database.core;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.StepsBase;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The manufacturer related steps.
 */
public class ManufacturerSteps extends StepsBase {

    @Autowired
    private ManufacturerRepository repo;

    /**
     * Generic method which adds a manufacturer using the settings.
     * 
     * @param settings
     *            The settings for the manufacturer to be used.
     * @throws Throwable
     */
    @Given("^a manufacturer")
    public void aManufacturer(final Map<String, String> settings) throws Throwable {

        Manufacturer entity = new Manufacturer(getString(settings, "ManufacturerId", Defaults.MANUFACTURER_ID),
                getString(settings, "Name", Defaults.MANUFACTURER_NAME), getBoolean(settings, "UsePrefix", Defaults.MANUFACTURER_USE_PREFIX));

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
        Manufacturer entity = repo.findByName(getString(expectedEntity, "Name", Defaults.MANUFACTURER_NAME));

        Assert.assertEquals(getString(expectedEntity, "ManufacturerId", Defaults.MANUFACTURER_ID),
                entity.getManufacturerId());
        Assert.assertEquals(getBoolean(expectedEntity, "UsesPrefix", Defaults.MANUFACTURER_USE_PREFIX),
                entity.isUsePrefix());
    }
}