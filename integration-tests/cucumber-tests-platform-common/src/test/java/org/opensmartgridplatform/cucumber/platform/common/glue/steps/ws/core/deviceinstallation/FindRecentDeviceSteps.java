/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindRecentDeviceSteps {

    @Autowired
    private CoreDeviceInstallationClient client;

    @When("receiving a find recent devices request")
    public void receivingAFindRecentDevicesRequest() throws Throwable {
        final FindRecentDevicesRequest request = new FindRecentDevicesRequest();

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.findRecentDevices(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    @Then("the find recent devices response contains \"([^\"]*)\" devices?")
    public void theFindRecentDevicesResponseContains(final Integer numberOfDevices) {
        final FindRecentDevicesResponse response = (FindRecentDevicesResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        final List<Device> devices = response.getDevices();
        assertThat((devices != null) ? devices.size() : 0).isEqualTo((int) numberOfDevices);
    }

    @Then("the find recent devices response contains at index \"([^\"]*)\"")
    public void theFindRecentDevicesResponseContainsAtIndex(final Integer index,
            final Map<String, String> expectedDevice) throws Throwable {
        final FindRecentDevicesResponse response = (FindRecentDevicesResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        final Device device = response.getDevices().get(index - 1);
        assertThat(device).isNotNull();
        DeviceSteps.checkDevice(expectedDevice, device);
    }

    @Then("^the find recent devices response contains soap fault$")
    public void theAddDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
