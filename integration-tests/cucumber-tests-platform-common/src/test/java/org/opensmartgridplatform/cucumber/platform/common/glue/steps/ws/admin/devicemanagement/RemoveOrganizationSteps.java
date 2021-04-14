/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the remove organization requests steps */
public class RemoveOrganizationSteps {

  @Autowired private AdminDeviceManagementClient client;

  /**
   * Send a remove organization request to the Platform.
   *
   * @param requestParameter An list with request parameters for the request.
   * @throws Throwable
   */
  @When("^receiving a remove organization request$")
  public void receivingARemoveOrganizationRequest(final Map<String, String> requestSettings)
      throws Throwable {

    final RemoveOrganisationRequest request = new RemoveOrganisationRequest();
    request.setOrganisationIdentification(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    try {
      ScenarioContext.current()
          .put(PlatformCommonKeys.RESPONSE, this.client.removeOrganization(request));
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
    }
  }

  /**
   * Verify that the create organization response is successful.
   *
   * @throws Throwable
   */
  @Then("^the remove organization response is successful$")
  public void theRemoveOrganizationResponseIsSuccessful() throws Throwable {
    assertThat(
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE)
                instanceof RemoveOrganisationResponse)
        .isTrue();
  }

  /** Verify the remove organization response */
  @Then("^the remove organization response contains$")
  public void theRemoveOrganizationResponseContains(final Map<String, String> expectedResult)
      throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
