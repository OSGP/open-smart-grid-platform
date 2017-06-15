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
     * Generic method to check if the device authorizations are created as
     * expected in the database.
     *
     * @param expectedEntity
     *            The expected settings.
     * @throws Throwable
     */
    public boolean entityDeviceHasAuthorization(final Map<String, String> expectedEntity) throws Throwable {
        Wait.until(() -> {
            final Device device = this.deviceRepository
                    .findByDeviceIdentification(expectedEntity.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
            if (device != null) {
                final List<DeviceAuthorization> deviceAuthorizations = this.deviceAuthorizationRepository
                        .findByDevice(device);
                final DeviceFunctionGroup expectedFunctionGroup = getEnum(expectedEntity,
                        PlatformKeys.KEY_DEVICE_FUNCTION_GROUP,
                        com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.class,
                        PlatformDefaults.DEVICE_FUNCTION_GROUP);
                final String expectedOrganizationIdentification = getString(expectedEntity,
                        PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);

                for (final DeviceAuthorization deviceAuthorization : deviceAuthorizations) {
                    if (expectedOrganizationIdentification
                            .equals(deviceAuthorization.getOrganisation().getOrganisationIdentification())
                            && expectedFunctionGroup == deviceAuthorization.getFunctionGroup()) {
                        this.hasAuthorization = true;
                        break;
                    } else {
                        this.hasAuthorization = false;
                    }
                }
            } else {
                this.hasAuthorization = false;
            }
        });
        return this.hasAuthorization;
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
        Assert.assertTrue(this.entityDeviceHasAuthorization(expectedEntity));
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
        Assert.assertFalse(this.entityDeviceHasAuthorization(expectedEntity));
    }
}
