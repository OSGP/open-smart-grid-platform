/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.wait.Wait;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the organization steps
 */
public class OrganizationSteps extends GlueBase {

    @Autowired
    private OrganisationRepository repo;

    /**
     * Generic method to create an organization.
     *
     * @param settings
     *            The settings to use to create the organization.
     * @throws Throwable
     */
    @Given("^an organization$")
    public void anOrganization(final Map<String, String> settings) throws Throwable {

        final String organizationIdentification = getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

        Organisation entity = this.repo.findByOrganisationIdentification(organizationIdentification);
        if (entity == null) {
            entity = new Organisation(
                    (organizationIdentification.isEmpty()) ? Defaults.DEFAULT_NEW_ORGANIZATION_IDENTIFICATION
                            : organizationIdentification,
                    getString(settings, Keys.KEY_NAME, Defaults.DEFAULT_ORGANIZATION_NAME),
                    getString(settings, Keys.KEY_PREFIX, Defaults.DEFAULT_PREFIX),
                    getEnum(settings, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class,
                            Defaults.PLATFORM_FUNCTION_GROUP));
        } else {
            entity.changeOrganisationData(getString(settings, Keys.KEY_NAME, Defaults.DEFAULT_ORGANIZATION_NAME),
                    getEnum(settings, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class,
                            Defaults.PLATFORM_FUNCTION_GROUP));
        }

        // Add all the mandatory stuff.
        entity.setDomains(new ArrayList<>());
        String domains = Defaults.DEFAULT_DOMAINS;
        if (settings.containsKey(Keys.KEY_DOMAINS) && !settings.get(Keys.KEY_DOMAINS).isEmpty()) {
            domains = settings.get(Keys.KEY_DOMAINS);
        }
        for (final String domain : domains.split(Keys.SEPARATOR_SEMICOLON)) {
            entity.addDomain(Enum.valueOf(PlatformDomain.class, domain));
        }

        entity.setIsEnabled(getBoolean(settings, Keys.KEY_ENABLED, Defaults.DEFAULT_ORGANIZATION_ENABLED));

        this.repo.save(entity);
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

        Wait.until(() -> {
            final Organisation entity = this.repo
                    .findByOrganisationIdentification(expectedEntity.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));

            Assert.assertEquals(getString(expectedEntity, Keys.KEY_NAME, Defaults.DEFAULT_NEW_ORGANIZATION_NAME),
                    entity.getName());
            final String prefix = getString(expectedEntity, Keys.KEY_PREFIX, Defaults.DEFAULT_ORGANIZATION_PREFIX);
            Assert.assertEquals((prefix.isEmpty()) ? Defaults.DEFAULT_ORGANIZATION_PREFIX : prefix, entity.getPrefix());

            Assert.assertEquals(getEnum(expectedEntity, Keys.KEY_PLATFORM_FUNCTION_GROUP,
                    com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup.class,
                    Defaults.PLATFORM_FUNCTION_GROUP), entity.getFunctionGroup());
            Assert.assertEquals(getBoolean(expectedEntity, Keys.KEY_ENABLED, Defaults.DEFAULT_ORGANIZATION_ENABLED),
                    entity.isEnabled());

            String domains = getString(expectedEntity, Keys.KEY_DOMAINS, Defaults.DEFAULT_DOMAINS);
            if (domains.isEmpty()) {
                domains = Defaults.DEFAULT_DOMAINS;
            }
            final List<String> expectedDomains = Arrays.asList(domains.split(";"));
            Assert.assertEquals(expectedDomains.size(), entity.getDomains().size());
            for (final PlatformDomain domain : entity.getDomains()) {
                Assert.assertTrue(expectedDomains.contains(domain.toString()));
            }
        });
    }

    /**
     * Generic method to check if the organization exists in the database.
     *
     * @param expectedOrganization
     *            An organization which has to exist in the database
     * @throws Throwable
     */
    @Given("^the organization exists$")
    public void theOrganizationExists(final Map<String, String> expectedOrganization) throws Throwable {
        final Organisation entity = Wait.untilAndReturn(() -> {
            return this.repo
                    .findByOrganisationIdentification(expectedOrganization.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
        });

        Assert.assertNotNull(entity);

        if (expectedOrganization.containsKey(Keys.KEY_NAME)) {
            Assert.assertEquals(getString(expectedOrganization, Keys.KEY_NAME), entity.getName());
        }
        if (expectedOrganization.containsKey(Keys.KEY_PLATFORM_FUNCTION_GROUP)) {
            Assert.assertEquals(
                    getEnum(expectedOrganization, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class),
                    entity.getFunctionGroup());
        }

        if (expectedOrganization.containsKey(Keys.KEY_DOMAINS)
                && !expectedOrganization.get(Keys.KEY_DOMAINS).isEmpty()) {
            for (final String domain : expectedOrganization.get(Keys.KEY_DOMAINS).split(Keys.SEPARATOR_SEMICOLON)) {
                Assert.assertTrue(entity.getDomains().contains(PlatformDomain.valueOf(domain)));
            }
        }
    }

    /**
     * Verify
     *
     * @param name
     * @throws Throwable
     */
    @Then("^the organization with name \"([^\"]*)\" should not be created$")
    public void theOrganizationWithNameShouldNotBeCreated(final String name) throws Throwable {
        Assert.assertNull(this.repo.findByName(name));
    }

    /**
     * Ensure that the organization is disabled.
     *
     * @param organizationIdentification
     * @throws Throwable
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be disabled$")
    public void theOrganizationWithOrganizationIdentificationShouldBeDisabled(final String organizationIdentification)
            throws Throwable {
        final Organisation entity = this.repo.findByOrganisationIdentification(organizationIdentification);

        // Note: 'entity' could be 'null'
        Assert.assertTrue(entity.isEnabled() == false);
    }

    /**
     * Ensure that the organization is enabled.
     *
     * @param organizationIdentification
     * @throws Throwable
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be enabled")
    public void theOrganizationWithOrganizationIdentificationShouldBeEnabled(final String organizationIdentification)
            throws Throwable {
        final Organisation entity = this.repo.findByOrganisationIdentification(organizationIdentification);

        Assert.assertTrue(entity.isEnabled() == true);
    }
}
