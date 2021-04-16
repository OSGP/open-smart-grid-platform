/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the remove organization requests steps */
public class UpdateKeySteps {

  @Autowired private AdminDeviceManagementClient client;

  /**
   * Send an update key request to the Platform.
   *
   * @param requestParameter An list with request parameters for the request.
   * @throws Throwable
   */
  @When("^receiving an update key request$")
  public void receiving_an_update_key_request(final Map<String, String> requestSettings)
      throws Throwable {

    // TODO: Change to Update Key
    final UpdateKeyRequest request = new UpdateKeyRequest();
    request.setDeviceIdentification(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setPublicKey(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_PUBLIC_KEY,
            PlatformCommonDefaults.DEFAULT_PUBLIC_KEY));
    request.setProtocolInfoId(
        getLong(
            requestSettings,
            PlatformCommonKeys.KEY_PROTOCOL_INFO_ID,
            PlatformCommonDefaults.NON_EXISTENT_PROTOCOL_INFO_ID));

    try {
      ScenarioContext.current()
          .put(PlatformCommonKeys.RESPONSE, this.client.getUpdateKeyResponse(request));
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
    }
  }

  /**
   * Verify that the update key response is successful.
   *
   * @throws Throwable
   */
  @Then("^the update key response contains$")
  public void the_update_key_response_contains(final Map<String, String> expectedResult)
      throws Throwable {
    // TODO: Check what the "Update Key Response" has to return, for now
    // there is no information to check.
    final UpdateKeyResponse response =
        (UpdateKeyResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
    assertThat(response).isNotNull();
  }

  /**
   * Verify that the update key response is failed.
   *
   * @throws Throwable
   */
  @Then("^the update key response contains soap fault$")
  public void the_update_key_response_contains_soap_fault(final Map<String, String> expectedResult)
      throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
