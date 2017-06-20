/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class DeviceAuthorizationSteps extends GlueBase {

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    public boolean hasAuthorization;

    /**
     * Generic method which adds a device authorization using the settings.
     *
     * @param settings
     *            The settings for the device authorization to be used.
     * @throws Throwable
     */
    @Given("^a device authorization$")
    @Transactional("txMgrCore")
    public void aDeviceAuthorization(final Map<String, String> settings) throws Throwable {

        final Device device = this.deviceRepository.findByDeviceIdentification(getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final Organisation organization = this.organizationRepository
                .findByOrganisationIdentification(getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        final DeviceFunctionGroup functionGroup = getEnum(settings, PlatformKeys.KEY_DEVICE_FUNCTION_GROUP,
                DeviceFunctionGroup.class, DeviceFunctionGroup.OWNER);

        final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);

        this.deviceAuthorizationRepository.save(authorization);
    }

    /**
     * The test passes if the device authorizations are created as expected in
     * the database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity device authorization exists$")
    public void thenTheEntityDeviceAuthorizationExists(final Map<String, String> expectedEntity) throws Throwable {
        this.thenTheEntityDeviceAuthorizationsExist(expectedEntity);
    }

    /**
     * The test passes if all the device authorizations are created as expected
     * in the database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity device authorizations exist$")
    public void thenTheEntityDeviceAuthorizationsExist(final Map<String, String> expectedEntity) throws Throwable {
        final String authorizationsStringList = expectedEntity.get(PlatformKeys.KEY_DEVICE_FUNCTION_GROUP);
        final String[] authorizations = StringUtils.split(authorizationsStringList, ',');

        final Device device = this.deviceRepository
                .findByDeviceIdentification(expectedEntity.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        Wait.until(() -> {
            final List<DeviceAuthorization> storedDeviceAuthorizations = this.deviceAuthorizationRepository
                    .findByDevice(device);

            // There's always an owner for the device. If OWNER isn't in the
            // given list of expected authorizations, an additional
            // authorization will be retrieved from the database.
            if (authorizationsStringList.contains(DeviceFunctionGroup.OWNER.toString())) {
                Assert.assertEquals(storedDeviceAuthorizations.size(), authorizations.length);
            } else {
                Assert.assertEquals(storedDeviceAuthorizations.size(), authorizations.length + 1);
            }
        });

        final List<DeviceAuthorization> storedDeviceAuthorizations = this.deviceAuthorizationRepository
                .findByDevice(device);

        final String organizationIdentification = getString(expectedEntity,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

        for (final String authorization : authorizations) {
            Assert.assertTrue(this.entityDeviceHasAuthorization(authorization, organizationIdentification,
                    storedDeviceAuthorizations));
        }
    }

    /**
     * Checks if the expected authorization of a certain organization is in the
     * list of stored authorizations.
     *
     * @param expectedAuthorization
     * @param expectedOrganizationIdentification
     * @param storedAuthorizations
     * @return
     */
    private boolean entityDeviceHasAuthorization(final String expectedAuthorization,
            final String expectedOrganizationIdentification, final List<DeviceAuthorization> storedAuthorizations) {
        boolean hasExpectedAuthorization = false;

        final DeviceFunctionGroup expectedFunctionGroup = com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup
                .valueOf(expectedAuthorization);

        for (final DeviceAuthorization deviceAuthorization : storedAuthorizations) {
            if (expectedOrganizationIdentification
                    .equals(deviceAuthorization.getOrganisation().getOrganisationIdentification())
                    && expectedFunctionGroup == deviceAuthorization.getFunctionGroup()) {
                hasExpectedAuthorization = true;
                break;
            }
        }

        return hasExpectedAuthorization;
    }

    /**
     * The test passes if the device authorizations are NOT created as expected
     * in the database.
     *
     * @param expectedEntityl
     *            The expected settings.
     * @throws Throwable
     */
    @Then("^the entity device authorization does not exist$")
    public void thenTheEntityDeviceAuthorizationDoesNotExist(final Map<String, String> expectedEntity)
            throws Throwable {
        final String expectedAuthorization = expectedEntity.get(PlatformKeys.KEY_DEVICE_FUNCTION_GROUP);
        final Device device = this.deviceRepository
                .findByDeviceIdentification(expectedEntity.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        Wait.until(() -> {
            final List<DeviceAuthorization> storedDeviceAuthorizations = this.deviceAuthorizationRepository
                    .findByDevice(device);

            final String organizationIdentification = getString(expectedEntity,
                    PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

            Assert.assertFalse(this.entityDeviceHasAuthorization(expectedAuthorization, organizationIdentification,
                    storedDeviceAuthorizations));
        });
    }
}
