// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class BundledGetAllAttributeValuesSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a get all attribute values action$")
  public void theBundleRequestContainsAGetAllAttributeValuesAction() throws Throwable {

    final GetAllAttributeValuesRequest action = new GetAllAttributeValuesRequest();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain a get all attribute values response$")
  public void theBundleResponseShouldContainAGetAllAttributeValuesResponse() throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
  }

  @Then("^the bundle response should contain a get all attribute values response with values$")
  public void theBundleResponseShouldContainAGetAllAttributeValuesResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response instanceof ActionResponse).as("Not a valid response").isTrue();
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(values.get(PlatformSmartmeteringKeys.RESULT));
    assertThat(StringUtils.isNotBlank(response.getResultString()))
        .as("Result contains no data.")
        .isTrue();
  }
}
