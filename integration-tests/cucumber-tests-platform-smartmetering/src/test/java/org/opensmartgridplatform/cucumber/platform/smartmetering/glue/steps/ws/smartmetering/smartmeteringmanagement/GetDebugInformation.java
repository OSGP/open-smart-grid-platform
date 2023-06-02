//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindMessageLogsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.logging.DeviceLogItemBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.FindMessageLogsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class GetDebugInformation {

  @Autowired private DeviceLogItemPagingRepository logItemRepository;

  @Autowired private DeviceLogItemBuilder deviceLogItemBuilder;

  @Autowired
  private SmartMeteringManagementRequestClient<FindMessageLogsAsyncResponse, FindMessageLogsRequest>
      smartMeteringManagementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          FindMessageLogsResponse, FindMessageLogsAsyncRequest>
      smartMeteringManagementResponseClient;

  @Given("^there is debug information logged for the device$")
  public void thereIsDebugInformationLoggedForTheDevice() throws Throwable {
    final DeviceLogItem deviceLogItem =
        this.deviceLogItemBuilder
            .withDeviceIdentification(
                ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString())
            .build();

    this.logItemRepository.save(deviceLogItem);
  }

  @When("^the get debug information request is received$")
  public void theGetDebugInformationRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final FindMessageLogsRequest findMessageLogsRequest =
        FindMessageLogsRequestFactory.fromParameterMap(requestData);
    final FindMessageLogsAsyncResponse findMessageLogsAsyncResponse =
        this.smartMeteringManagementRequestClient.doRequest(findMessageLogsRequest);

    assertThat(findMessageLogsAsyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            findMessageLogsAsyncResponse.getCorrelationUid());
  }

  @Then("^the device debug information should be in the response message$")
  public void theDeviceDebugInformationShouldBeInTheResponseMessage() throws Throwable {
    final FindMessageLogsAsyncRequest findMessageLogsAsyncRequest =
        FindMessageLogsRequestFactory.fromScenarioContext();
    final FindMessageLogsResponse findMessageLogsResponse =
        this.smartMeteringManagementResponseClient.getResponse(findMessageLogsAsyncRequest);

    assertThat(findMessageLogsResponse)
        .as("FindMessageLogsRequestResponse should not be null")
        .isNotNull();
    assertThat(findMessageLogsResponse.getMessageLogPage()).as("Expected logs").isNotNull();
  }
}
