/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class FindDevicesWithoutOwner extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send an find devices without organization request to the Platform.
     *
     * @param requestParameter
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a find devices without organization request$")
    public void receivingAFindDevicesWithoutOrganizationRequest() throws Throwable {

        final FindDevicesWhichHaveNoOwnerRequest request = new FindDevicesWhichHaveNoOwnerRequest();

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.findDevicesWithoutOwner(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
        }
    }

    /**
     * Verify that the find devices without organization response has items.
     *
     * @throws Throwable
     */
    @Then("^the find devices without organization response contains \"([^\"]*)\" devices?$")
    public void theFindDevicesWithoutOrganizationResponseContains(final Integer expectedCount) throws Throwable {

        final FindDevicesWhichHaveNoOwnerResponse findDevicesWhichHaveNoOwnerResponse = (FindDevicesWhichHaveNoOwnerResponse) ScenarioContext
                .current().get(PlatformCommonKeys.RESPONSE);

        Assert.assertEquals((int) expectedCount, findDevicesWhichHaveNoOwnerResponse.getDevices().size());
    }

    /**
     * Verify that the find devices without organization response is failed.
     *
     * @throws Throwable
     */
    @Then("^the find devices without organization response contains at index \"([^\"]*)\"$")
    public void theFindDevicesWithoutOrganizationResponseContainsAtIndex(final Integer expectedIndex,
            final Map<String, String> expectedResult) throws Throwable {

        final FindDevicesWhichHaveNoOwnerResponse response = (FindDevicesWhichHaveNoOwnerResponse) ScenarioContext
                .current().get(PlatformCommonKeys.RESPONSE);

        for (final Device device : response.getDevices()) {
            Assert.assertEquals(getString(expectedResult, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION),
                    device.getDeviceIdentification());
            Assert.assertTrue(device.getOrganisations().isEmpty());
            Assert.assertNull(device.getOwner());
        }
    }
}