/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.core.builders.AddressBuilder;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class UpdateDeviceSettingsSteps {

  @Autowired private CoreDeviceManagementClient client;

  @When("^receiving a device management update device request")
  public void receivingAnUpdateDeviceRequest(final Map<String, String> settings) {
    final UpdateDeviceRequest request = new UpdateDeviceRequest();

    String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    // Note: The regular expression below matches at spaces between two
    // quotation marks("), this check is used for a test with a
    // DeviceIdentification with only spaces
    if (deviceIdentification.matches("(?!\")\\s*(?=\")")) {
      deviceIdentification = deviceIdentification.replaceAll("\"", " ");
    }
    final UpdatedDevice device = this.createUpdatedDevice(settings);
    request.setDeviceIdentification(deviceIdentification);
    request.setUpdatedDevice(device);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.updateDevice(request));
    } catch (final WebServiceSecurityException | SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  private UpdatedDevice createUpdatedDevice(final Map<String, String> settings) {

    final UpdatedDevice device = new UpdatedDevice();
    device.setAlias(getString(settings, PlatformKeys.ALIAS, PlatformCommonDefaults.DEFAULT_ALIAS));
    device.setContainerAddress(new AddressBuilder().withSettings(settings).build());
    device.setGpsLatitude(
        getString(
            settings, PlatformKeys.KEY_LATITUDE, PlatformCommonDefaults.DEFAULT_LATITUDE_STRING));
    device.setGpsLongitude(
        getString(
            settings, PlatformKeys.KEY_LONGITUDE, PlatformCommonDefaults.DEFAULT_LONGITUDE_STRING));
    device.setActivated(
        getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformCommonDefaults.DEFAULT_ACTIVATED));

    return device;
  }

  @Then("^the device management update device response is successful$")
  public void theUpdateDeviceResponseIsSuccessful() {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE))
        .isInstanceOf(UpdateDeviceResponse.class);
  }

  @Then("^the device management update device response contains soap fault$")
  public void theUpdateDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
