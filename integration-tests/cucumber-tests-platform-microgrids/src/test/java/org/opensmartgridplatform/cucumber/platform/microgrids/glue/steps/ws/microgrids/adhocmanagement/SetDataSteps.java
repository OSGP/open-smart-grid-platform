/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.ws.microgrids.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.SetDataRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class SetDataSteps {

  @Autowired private AdHocManagementClient client;

  @When("^a set data request is received$")
  public void aSetDataRequestIsReceived(final Map<String, String> requestParameters)
      throws Throwable {
    final String organizationIdentification =
        (String)
            ScenarioContext.current()
                .get(
                    PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
    ScenarioContext.current()
        .put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, organizationIdentification);
    final String userName =
        (String)
            ScenarioContext.current()
                .get(PlatformKeys.KEY_USER_NAME, PlatformDefaults.DEFAULT_USER_NAME);
    ScenarioContext.current().put(PlatformKeys.KEY_USER_NAME, userName);

    final SetDataRequest setDataRequest = SetDataRequestBuilder.fromParameterMap(requestParameters);
    final SetDataAsyncResponse response = this.client.setDataAsync(setDataRequest);

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, response.getAsyncResponse().getCorrelationUid());
  }

  @Then("^the set data response should be returned$")
  public void theSetDataResponseShouldBeReturned(final Map<String, String> responseParameters)
      throws Throwable {

    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final SetDataAsyncRequest setDataAsyncRequest =
        SetDataRequestBuilder.fromParameterMapAsync(extendedParameters);
    final SetDataResponse response = this.client.setData(setDataAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }
}
