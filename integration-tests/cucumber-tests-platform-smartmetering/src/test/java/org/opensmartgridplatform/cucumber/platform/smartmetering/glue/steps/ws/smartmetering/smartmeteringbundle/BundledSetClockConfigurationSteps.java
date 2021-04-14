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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetClockConfigurationRequestDataFactory;

public class BundledSetClockConfigurationSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a set clock configuration action with parameters$")
  public void theBundleRequestContainsASetClockConfigurationAction(
      final Map<String, String> settings) throws Throwable {

    final SetClockConfigurationRequest action =
        this.mapperFacade.map(
            SetClockConfigurationRequestDataFactory.fromParameterMap(settings),
            SetClockConfigurationRequest.class);

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a set clock configuration response with values$")
  public void theBundleResponseShouldContainASetClockConfigurationResponse(
      final Map<String, String> settings) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult())
        .isEqualTo(OsgpResultType.fromValue(settings.get(PlatformSmartmeteringKeys.RESULT)));
  }
}
