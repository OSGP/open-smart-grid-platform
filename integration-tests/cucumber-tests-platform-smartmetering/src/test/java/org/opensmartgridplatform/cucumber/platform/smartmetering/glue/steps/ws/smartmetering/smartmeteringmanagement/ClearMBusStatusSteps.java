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

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMbusStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMbusStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMbusStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMbusStatusRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMbusStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BaseBundleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ClearMBusStatusSteps extends BaseBundleSteps {

  @Autowired
  private SmartMeteringManagementRequestClient<ClearMbusStatusAsyncResponse, ClearMbusStatusRequest>
      managementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          ClearMbusStatusResponse, ClearMbusStatusAsyncRequest>
      managementResponseClient;

  @When("^the clear M-Bus status request is received$")
  public void theBundleRequestContainsAClearMBusStatusAction(final Map<String, String> requestData)
      throws Throwable {
    final ClearMbusStatusRequest request = new ClearMbusStatusRequest();
    request.setClearMbusStatusRequestData(new ClearMbusStatusRequestData());
    request.setDeviceIdentification(
        requestData.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final ClearMbusStatusAsyncResponse asyncResponse =
        this.managementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the clear M-Bus status response is \"([^\"]*)\"$")
  public void theClearMBusStatusResponseShouldBe(final String result) throws Throwable {

    final ClearMbusStatusAsyncRequest asyncRequest = new ClearMbusStatusAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    final ClearMbusStatusResponse response =
        this.managementResponseClient.getResponse(asyncRequest);

    assertThat(response).as("ClearMbusStatusResponse should not be null").isNotNull();
    assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));
  }
}
