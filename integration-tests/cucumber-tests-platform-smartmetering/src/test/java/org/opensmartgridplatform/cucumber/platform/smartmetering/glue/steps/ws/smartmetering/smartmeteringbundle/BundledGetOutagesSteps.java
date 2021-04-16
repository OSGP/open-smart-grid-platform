/*
 * Copyright 2020 Alliander N.V.
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetOutagesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetOutagesResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Outage;

public class BundledGetOutagesSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get outages action$")
  public void theBundleRequestContainsAGetOutagesAction() {
    this.addActionToBundleRequest(new GetOutagesRequest());
  }

  @Then("^the bundle response should contain a get outages response with (\\d++) outages$")
  public void theBundleResponseShouldContainAGetOutagesResponseWithValues(
      final int numberOfEvents, final Map<String, String> expectedValues) throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof GetOutagesResponse).isTrue();

    final GetOutagesResponse outagesResponse = (GetOutagesResponse) response;
    final List<Outage> outages = outagesResponse.getOutages();

    assertThat(outages.size()).isEqualTo(numberOfEvents);

    final Map<String, String> values =
        outages.stream()
            .collect(
                Collectors.toMap(o -> o.getEndTime().toString(), o -> o.getDuration().toString()));

    assertThat(values).containsExactlyInAnyOrderEntriesOf(expectedValues);
  }
}
