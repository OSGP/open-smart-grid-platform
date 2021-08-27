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
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventDetail;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.FindEventsRequestBuilder;

public class BundledFindEventsSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a find events action$")
  public void theBundleRequestContainsAFindEventsAction() throws Throwable {
    final FindEventsRequest action = new FindEventsRequestBuilder().withDefaults().build();

    this.addActionToBundleRequest(action);
  }

  @Given("^the bundle request contains a find events action with parameters$")
  public void theBundleRequestContainsAFindEventsAction(final Map<String, String> parameters)
      throws Throwable {
    final FindEventsRequest action =
        new FindEventsRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a find events response with (\\d++) events$")
  public void theBundleResponseShouldContainAFindEventsResponse(
      final int nrOfEvents, final Map<String, String> parameters) throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(FindEventsResponse.class);

    final FindEventsResponse findEventsResponse = (FindEventsResponse) response;
    assertThat(findEventsResponse.getEvents().size()).isEqualTo(nrOfEvents);

    for (final Event event : findEventsResponse.getEvents()) {
      final Map<String, String> eventDetails =
          event.getEventDetails().stream()
              .collect(Collectors.toMap(EventDetail::getName, EventDetail::getValue));

      parameters.forEach(
          (key, value) -> {
            assertThat(eventDetails).containsEntry(key, value);
          });
    }
  }
}
