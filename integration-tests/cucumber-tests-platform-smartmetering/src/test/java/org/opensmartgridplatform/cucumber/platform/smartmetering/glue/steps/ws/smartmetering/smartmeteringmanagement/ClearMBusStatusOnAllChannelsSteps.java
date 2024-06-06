// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.ClearMBusStatusOnAllChannelsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BaseBundleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ClearMBusStatusOnAllChannelsSteps extends BaseBundleSteps {

  @Autowired
  private SmartMeteringManagementRequestClient<
          ClearMBusStatusOnAllChannelsAsyncResponse, ClearMBusStatusOnAllChannelsRequest>
      managementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          ClearMBusStatusOnAllChannelsResponse, ClearMBusStatusOnAllChannelsAsyncRequest>
      managementResponseClient;

  @When("^the clear M-Bus status on all channels request is received$")
  public void theBundleRequestContainsAClearMBusStatusOnAllChannelsAction(
      final Map<String, String> requestData) throws Throwable {
    final ClearMBusStatusOnAllChannelsRequest request = new ClearMBusStatusOnAllChannelsRequest();
    request.setClearMBusStatusOnAllChannelsRequestData(
        new ClearMBusStatusOnAllChannelsRequestData());
    request.setDeviceIdentification(
        requestData.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    final ClearMBusStatusOnAllChannelsAsyncResponse asyncResponse =
        this.managementRequestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the clear M-Bus status on all channels response is \"([^\"]*)\"$")
  public void theClearMBusStatusOnAllChannelsResponseShouldBe(final String result)
      throws Throwable {

    final ClearMBusStatusOnAllChannelsAsyncRequest asyncRequest =
        new ClearMBusStatusOnAllChannelsAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    final ClearMBusStatusOnAllChannelsResponse response =
        this.managementResponseClient.getResponse(asyncRequest);
    assertThat(response).as("ClearMBusStatusOnAllChannelsResponse should not be null").isNotNull();
    assertThat(response.getResult()).as("Result").isEqualTo(OsgpResultType.valueOf(result));
  }
}
