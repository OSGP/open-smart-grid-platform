/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getBoolean;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.core.builders.ManufacturerBuilder;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The manufacturer related steps.
 */
public class ManufacturerSteps extends GlueBase {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    /**
     * Generic method which adds a manufacturer using the settings.
     *
     * @param settings
     *            The settings for the manufacturer to be used.
     * @throws Throwable
     */
    @Given("^a manufacturer$")
    public void aManufacturer(final Map<String, String> settings) throws Throwable {

        final Manufacturer manufacturer = new ManufacturerBuilder().withSettings(settings).build();

        this.manufacturerRepository.save(manufacturer);
    }

    /**
     * Verify whether the entity is created as expected.
     *
     * @param settings
     * @throws Throwable
     */
    @Then("^the entity manufacturer exists$")
    public void theEntityManufacturerExists(final Map<String, String> settings) throws Throwable {
        final Manufacturer manufacturer = this.manufacturerRepository.findByCode(
                getString(settings, PlatformKeys.MANUFACTURER_CODE, PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

        Assert.assertEquals(
                getString(settings, PlatformKeys.MANUFACTURER_NAME, PlatformDefaults.DEFAULT_MANUFACTURER_NAME),
                manufacturer.getName());
        Assert.assertEquals(getBoolean(settings, PlatformKeys.MANUFACTURER_USE_PREFIX,
                PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX), manufacturer.isUsePrefix());
    }

}