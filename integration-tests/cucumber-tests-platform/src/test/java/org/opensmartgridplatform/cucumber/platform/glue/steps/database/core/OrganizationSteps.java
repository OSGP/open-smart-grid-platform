/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformDomain;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the organization steps
 */
public class OrganizationSteps extends GlueBase {

    @Autowired
    private OrganisationRepository organisationRepository;

    /**
     * Generic method to create an organization.
     *
     * @param settings
     *            The settings to use to create the organization.
     * @throws Throwable
     */
    @Given("^an organization$")
    public void anOrganization(final Map<String, String> settings) {

        final String organizationIdentification = getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

        Organisation entity = this.organisationRepository.findByOrganisationIdentification(organizationIdentification);
        if (entity == null) {
            entity = new Organisation(
                    (organizationIdentification.isEmpty()) ? PlatformDefaults.DEFAULT_NEW_ORGANIZATION_IDENTIFICATION
                            : organizationIdentification,
                    getString(settings, PlatformKeys.KEY_NAME, PlatformDefaults.DEFAULT_ORGANIZATION_NAME),
                    getString(settings, PlatformKeys.KEY_PREFIX, PlatformDefaults.DEFAULT_PREFIX),
                    getEnum(settings, PlatformKeys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class,
                            PlatformDefaults.PLATFORM_FUNCTION_GROUP));
        } else {
            entity.changeOrganisationData(
                    getString(settings, PlatformKeys.KEY_NAME, PlatformDefaults.DEFAULT_ORGANIZATION_NAME),
                    getEnum(settings, PlatformKeys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class,
                            PlatformDefaults.PLATFORM_FUNCTION_GROUP));
        }

        // Add all the mandatory stuff.
        entity.setDomains(new ArrayList<>());
        String domains = PlatformDefaults.DEFAULT_DOMAINS;
        if (settings.containsKey(PlatformKeys.KEY_DOMAINS) && !settings.get(PlatformKeys.KEY_DOMAINS).isEmpty()) {
            domains = settings.get(PlatformKeys.KEY_DOMAINS);
        }
        for (final String domain : domains.split(PlatformKeys.SEPARATOR_SEMICOLON)) {
            entity.addDomain(Enum.valueOf(PlatformDomain.class, domain));
        }

        entity.setIsEnabled(
                getBoolean(settings, PlatformKeys.KEY_ENABLED, PlatformDefaults.DEFAULT_ORGANIZATION_ENABLED));

        this.organisationRepository.save(entity);
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
    public void thenTheEntityOrganizationExists(final Map<String, String> expectedEntity) {

        Wait.until(() -> {
            final Organisation entity = this.organisationRepository
                    .findByOrganisationIdentification(expectedEntity.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));

            Assert.assertEquals(
                    getString(expectedEntity, PlatformKeys.KEY_NAME, PlatformDefaults.DEFAULT_NEW_ORGANIZATION_NAME),
                    entity.getName());
            final String prefix = getString(expectedEntity, PlatformKeys.KEY_PREFIX,
                    PlatformDefaults.DEFAULT_ORGANIZATION_PREFIX);
            Assert.assertEquals((prefix.isEmpty()) ? PlatformDefaults.DEFAULT_ORGANIZATION_PREFIX : prefix,
                    entity.getPrefix());

            Assert.assertEquals(getEnum(expectedEntity, PlatformKeys.KEY_PLATFORM_FUNCTION_GROUP,
                    org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup.class,
                    PlatformDefaults.PLATFORM_FUNCTION_GROUP), entity.getFunctionGroup());
            Assert.assertEquals(
                    getBoolean(expectedEntity, PlatformKeys.KEY_ENABLED, PlatformDefaults.DEFAULT_ORGANIZATION_ENABLED),
                    entity.isEnabled());

            String domains = getString(expectedEntity, PlatformKeys.KEY_DOMAINS, PlatformDefaults.DEFAULT_DOMAINS);
            if (domains.isEmpty()) {
                domains = PlatformDefaults.DEFAULT_DOMAINS;
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
    public void theOrganizationExists(final Map<String, String> expectedOrganization) {
        final Organisation entity = Wait.untilAndReturn(() -> {
            return this.organisationRepository.findByOrganisationIdentification(
                    expectedOrganization.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
        });

        Assert.assertNotNull(entity);

        if (expectedOrganization.containsKey(PlatformKeys.KEY_NAME)) {
            Assert.assertEquals(getString(expectedOrganization, PlatformKeys.KEY_NAME), entity.getName());
        }
        if (expectedOrganization.containsKey(PlatformKeys.KEY_PLATFORM_FUNCTION_GROUP)) {
            Assert.assertEquals(getEnum(expectedOrganization, PlatformKeys.KEY_PLATFORM_FUNCTION_GROUP,
                    PlatformFunctionGroup.class), entity.getFunctionGroup());
        }

        if (expectedOrganization.containsKey(PlatformKeys.KEY_DOMAINS)
                && !expectedOrganization.get(PlatformKeys.KEY_DOMAINS).isEmpty()) {
            for (final String domain : expectedOrganization.get(PlatformKeys.KEY_DOMAINS)
                    .split(PlatformKeys.SEPARATOR_SEMICOLON)) {
                Assert.assertTrue(entity.getDomains().contains(PlatformDomain.valueOf(domain)));
            }
        }
    }

    /**
     * Verify
     */
    @Then("^the organization with name \"([^\"]*)\" should not be created$")
    public void theOrganizationWithNameShouldNotBeCreated(final String name) {
        Assert.assertNull(this.organisationRepository.findByName(name));
    }

    /**
     * Ensure that the organization is disabled.
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be disabled$")
    public void theOrganizationWithOrganizationIdentificationShouldBeDisabled(final String organizationIdentification)
            throws Throwable {
        final Organisation entity = this.organisationRepository.findByOrganisationIdentification(organizationIdentification);

        // Note: 'entity' could be 'null'
        Assert.assertTrue(!entity.isEnabled());
    }

    /**
     * Ensure that the organization is enabled.
     */
    @Then("^the organization with organization identification \"([^\"]*)\" should be enabled")
    public void theOrganizationWithOrganizationIdentificationShouldBeEnabled(final String organizationIdentification)
            throws Throwable {
        final Organisation entity = this.organisationRepository.findByOrganisationIdentification(organizationIdentification);

        Assert.assertTrue(entity.isEnabled());
    }
}
