/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BaseBundleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ClearMBusStatusSteps extends BaseBundleSteps {

  @Autowired
  private SmartMeteringManagementRequestClient<ClearMBusStatusAsyncResponse, ClearMBusStatusRequest>
      managementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          ClearMBusStatusResponse, ClearMBusStatusAsyncRequest>
      managementResponseClient;

  @When("^the clear M-Bus status request is received$")
  public void theBundleRequestContainsAClearMBusStatusAction(final Map<String, String> requestData)
      throws Throwable {
    final ClearMBusStatusRequest request = new ClearMBusStatusRequest();
    request.setClearMBusStatusRequestData(new ClearMBusStatusRequestData());
    request.setDeviceIdentification(
        requestData.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final ClearMBusStatusAsyncResponse asyncResponse =
        this.managementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the clear M-Bus status response is \"([^\"]*)\"$")
  public void theClearMBusStatusResponseShouldBe(final String result) throws Throwable {

    final ClearMBusStatusAsyncRequest asyncRequest = new ClearMBusStatusAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    final ClearMBusStatusResponse response =
        this.managementResponseClient.getResponse(asyncRequest);

    assertThat(response).as("ClearMBusStatusResponse should not be null").isNotNull();
    assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));
  }
}
