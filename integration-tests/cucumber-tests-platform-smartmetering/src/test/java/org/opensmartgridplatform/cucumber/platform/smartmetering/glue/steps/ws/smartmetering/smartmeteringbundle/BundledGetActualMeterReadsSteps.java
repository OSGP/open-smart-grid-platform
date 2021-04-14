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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;

public class BundledGetActualMeterReadsSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get actual meter reads action$")
  public void theBundleRequestContainsAGetActualMeterReadsAction() throws Throwable {

    final GetActualMeterReadsRequest action = new GetActualMeterReadsRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get actual meter reads response$")
  public void theBundleResponseShouldContainAGetActualMeterReadsResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActualMeterReadsResponse).as("Not a valid response").isTrue();
  }
}
