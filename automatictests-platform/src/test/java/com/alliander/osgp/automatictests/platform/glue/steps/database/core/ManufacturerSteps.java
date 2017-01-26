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
<<<<<<< HEAD:automatictests-platform/src/test/java/com/alliander/osgp/automatictests/platform/glue/steps/database/core/ManufacturerSteps.java
=======
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
>>>>>>> 3ccf56a85cff1219f2d93ef91f86a3dd8e3e9de3:cucumber-tests-platform/src/test/java/com/alliander/osgp/platform/cucumber/steps/database/core/ManufacturerSteps.java

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

<<<<<<< HEAD:automatictests-platform/src/test/java/com/alliander/osgp/automatictests/platform/glue/steps/database/core/ManufacturerSteps.java
        Manufacturer entity = new Manufacturer(getString(settings, "ManufacturerId", Defaults.MANUFACTURER_ID),
                getString(settings, "Name", Defaults.MANUFACTURER_NAME), getBoolean(settings, "UsePrefix", Defaults.MANUFACTURER_USE_PREFIX));
=======
        Manufacturer entity = new Manufacturer(
                getString(settings, Keys.MANUFACTURER_ID, DEFAULT_MANUFACTURER_ID),
                getString(settings, "Name", DEFAULT_NAME), 
                getBoolean(settings, "UsePrefix", DEFAULT_USEPREFIX));
>>>>>>> 3ccf56a85cff1219f2d93ef91f86a3dd8e3e9de3:cucumber-tests-platform/src/test/java/com/alliander/osgp/platform/cucumber/steps/database/core/ManufacturerSteps.java

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