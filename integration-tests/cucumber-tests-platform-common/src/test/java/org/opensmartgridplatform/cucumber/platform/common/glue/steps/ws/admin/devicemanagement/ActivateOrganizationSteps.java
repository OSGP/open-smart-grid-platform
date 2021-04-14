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
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the activate organization steps. */
public class ActivateOrganizationSteps {

  @Autowired private AdminDeviceManagementClient client;

  @When("^receiving an activate organization request$")
  public void receivingAnActivateOrganizationRequest(final Map<String, String> requestSettings)
      throws Throwable {

    final ActivateOrganisationRequest request = new ActivateOrganisationRequest();
    request.setOrganisationIdentification(
        getString(
            requestSettings,
            PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    try {
      ScenarioContext.current()
          .put(PlatformCommonKeys.RESPONSE, this.client.activateOrganization(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, ex);
    }
  }

  /**
   * Verify that the activate organization response is successful.
   *
   * @throws Throwable
   */
  @Then("^the activate organization response is successful$")
  public void theActivateOrganizationResponseIsSuccessful() throws Throwable {
    assertThat(
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE)
                instanceof ActivateOrganisationResponse)
        .isTrue();
  }
}
