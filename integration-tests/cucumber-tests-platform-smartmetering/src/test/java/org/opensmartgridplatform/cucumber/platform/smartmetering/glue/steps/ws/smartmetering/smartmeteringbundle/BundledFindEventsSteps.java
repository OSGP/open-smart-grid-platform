// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
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
  public void theBundleResponseShouldContainAFindEventsResponse(final int nrOfEvents)
      throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(FindEventsResponse.class);

    final FindEventsResponse findEventsResponse = (FindEventsResponse) response;
    assertThat(findEventsResponse.getEvents()).hasSize(nrOfEvents);
  }
}
