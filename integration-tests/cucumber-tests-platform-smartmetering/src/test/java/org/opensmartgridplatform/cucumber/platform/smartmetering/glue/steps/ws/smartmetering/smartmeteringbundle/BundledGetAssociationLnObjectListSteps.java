/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class BundledGetAssociationLnObjectListSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get association ln objects action$")
  public void theBundleRequestContainsAGetAssociationLnObjectsAction() throws Throwable {

    final GetAssociationLnObjectsRequest action = new GetAssociationLnObjectsRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get association ln objects response$")
  public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof AssociationLnObjectsResponse)
        .as("Not a valid response")
        .isTrue();
  }

  @Then("^the bundle response should contain a get association ln objects response with values$")
  public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof AssociationLnObjectsResponse)
        .as("Not a valid response")
        .isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    assertThat(StringUtils.isNotBlank(response.getResultString()))
        .as("Result contains no data.")
        .isTrue();
  }
}
