/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the organization steps
 */
public class OrganizationSteps {

    @Autowired
    private OrganisationRepository repo;

    /**
     * Some defaults for the organization class.
     */
    private final Boolean DEFAULT_ENABLED = true;
    private final String DEFAULT_ORGANIZATION = "An Organization";
    private final String DEFAULT_NAME = "An Organization";
    private final String DEFAULT_PREFIX = "Tes";
    private final PlatformFunctionGroup DEFAULT_PLATFORM_FUNCTION_GROUP = PlatformFunctionGroup.ADMIN;
    private final PlatformDomain DEFAULT_PLATFORM_DOMAIN = PlatformDomain.COMMON;

    /**
     * Generic method to create an organization.
     *
     * @param settings
     *            The settings to use to create the organization.
     * @throws Throwable
     */
    @Given("^an organization$")
    public void anOrganization(final Map<String, String> settings) throws Throwable {
        final Organisation entity = new Organisation(getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                this.DEFAULT_ORGANIZATION), getString(settings, "Name", this.DEFAULT_NAME), getString(settings,
                        "Prefix", this.DEFAULT_PREFIX), getEnum(settings, "PlatformFunctionGroup", PlatformFunctionGroup.class,
                                this.DEFAULT_PLATFORM_FUNCTION_GROUP));

        // Add all the mandatory stuff.
        entity.addDomain(getEnum(settings, "PlatformDomain", PlatformDomain.class, this.DEFAULT_PLATFORM_DOMAIN));

        entity.setIsEnabled(getBoolean(settings, "Enabled", this.DEFAULT_ENABLED));

        // TODO: Add all the optional stuff
        this.repo.save(entity);

        // Save the created id for the organization in the scenario context.
        final Organisation savedEntity = this.repo.findByName(getString(settings, "Name", this.DEFAULT_NAME));
    }
    
    /**
     * Generic method to check if the organization exists in the database.
     * 
     * @param expectedOrganization
     *            An organization which has to exist in the database
     * @throws Throwable
     */
    @Given("^the organization exists$")
    public void theOrganizationExists(final Map<String, String> expectedOrganization) throws Throwable
    {
    	final Organisation entity = this.repo.findByOrganisationIdentification(expectedOrganization
                .get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
    	
    	Assert.assertNotNull(entity);
    	
    	if (expectedOrganization.containsKey(Keys.KEY_NAME)) {
    		Assert.assertEquals(getString(expectedOrganization, Keys.KEY_NAME), entity.getName());
    	}
    	if (expectedOrganization.containsKey(Keys.KEY_PLATFORM_FUNCTION_GROUP)) {
    		Assert.assertEquals(getEnum(expectedOrganization, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class), entity.getFunctionGroup());
    	}
    	// TODO:
    	//if (expectedOrganization.containsKey(Keys.KEY_DOMAINS)) {
    	//	Assert.assertEquals(getString(expectedOrganization, Keys.KEY_DOMAINS), entity.getDomains().);
    	//}
    }

    /**
     * Generic method to check if the organization is created as expected in the
     * database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity organization exists$")
    public void thenTheEntityOrganizationExists(final Map<String, String> expectedEntity) throws Throwable {

        final Organisation entity = this.repo.findByOrganisationIdentification(expectedEntity
                .get(Keys.KEY_ORGANIZATION_IDENTIFICATION));

        Assert.assertEquals(expectedEntity.get("Name"), entity.getName());
        Assert.assertEquals(expectedEntity.get("Prefix"), entity.getPrefix());
        Assert.assertTrue(expectedEntity.get("FunctionGroup").toUpperCase()
                .equals(entity.getFunctionGroup().toString()));
        Assert.assertTrue(expectedEntity.get("Enabled").toLowerCase().equals("true") == entity.isEnabled());
        final List<String> expectedDomains = Arrays.asList(expectedEntity.get("Domains").split(";"));
        Assert.assertEquals(expectedDomains.size(), entity.getDomains().size());
        for (final PlatformDomain domain : entity.getDomains()) {
            Assert.assertTrue(expectedDomains.contains(domain.toString()));
        }
    }

    /**
     * Ensure that the organization is disabled.
     *
     * @param organizationIdentification
     * @throws Throwable
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be disabled$")
    public void the_organization_with_organization_identification_should_be_disabled(
            final String organizationIdentification) throws Throwable {
        final Organisation entity = this.repo.findByOrganisationIdentification(organizationIdentification);

        Assert.assertTrue(entity.isEnabled() == false);
    }

    /**
     * Verify
     *
     * @param name
     * @throws Throwable
     */
    @Then("^the organization with name \"([^\"]*)\" should not be created$")
    public void the_organization_with_name_should_not_be_created(final String name) throws Throwable {
        Assert.assertNull(this.repo.findByName(name));
    }
}
