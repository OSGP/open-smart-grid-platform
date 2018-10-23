/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.core.builders.ManufacturerBuilder;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;

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