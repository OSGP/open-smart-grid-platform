/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetOwnerSteps extends GlueBase {

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepo;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving a set owner request(?: over OSGP)?$")
    public void receivingAFindDevicesRequest(final Map<String, String> requestParameters) throws Throwable {
        final SetOwnerRequest request = new SetOwnerRequest();

        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setOrganisationIdentification(getString(requestParameters, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setOwner(request));
    }

    @Then("^the set owner async response contains$")
    public void theFindDevicesResponseContainsDevices(final Map<String, String> expectedDevice) throws Throwable {
        Assert.assertTrue(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof SetOwnerResponse);
        final SetOwnerResponse response = (SetOwnerResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);
        Assert.assertNotNull(response);
    }

    @Then("^the owner of device \"([^\"]*)\" has been changed$")
    public void theFindDevicesResponseContainsAtIndex(final String deviceIdentification,
            final Map<String, String> expectedOrganization) throws Throwable {

        final List<DeviceAuthorization> deviceAuthorization = Wait.untilAndReturn(() -> {
            final List<DeviceAuthorization> retval = this.deviceAuthorizationRepo
                    .findByDevice(this.deviceRepository.findByDeviceIdentification(deviceIdentification));
            Assert.assertNotNull(retval);
            return retval;
        });

        Assert.assertEquals(
                getString(expectedOrganization, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION),
                deviceAuthorization.get(0).getOrganisation().getOrganisationIdentification());

    }
}
