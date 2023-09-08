// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.Actions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FaultResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Action;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import org.springframework.beans.factory.annotation.Autowired;

public class BundleSteps extends BaseBundleSteps {

  private static final Map<Class<?>, Class<?>> REQUEST_RESPONSE_MAP = new HashMap<>();

  static {
    REQUEST_RESPONSE_MAP.put(FindEventsRequest.class, FindEventsResponse.class);
    REQUEST_RESPONSE_MAP.put(GetActualMeterReadsRequest.class, ActualMeterReadsResponse.class);
    REQUEST_RESPONSE_MAP.put(
        GetAdministrativeStatusRequest.class, AdministrativeStatusResponse.class);
    REQUEST_RESPONSE_MAP.put(GetAllAttributeValuesRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(
        GetAssociationLnObjectsRequest.class, AssociationLnObjectsResponse.class);
    REQUEST_RESPONSE_MAP.put(
        GetConfigurationObjectRequest.class, GetConfigurationObjectResponse.class);
    REQUEST_RESPONSE_MAP.put(
        GetPowerQualityProfileRequest.class, GetPowerQualityProfileRequest.class);
    REQUEST_RESPONSE_MAP.put(GetSpecificAttributeValueRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(ReadAlarmRegisterRequest.class, ReadAlarmRegisterResponse.class);
    REQUEST_RESPONSE_MAP.put(SetActivityCalendarRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetAdministrativeStatusRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetAlarmNotificationsRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetClockConfigurationRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetConfigurationObjectRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetPushSetupAlarmRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SetSpecialDaysRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(SynchronizeTimeRequest.class, ActionResponse.class);
    REQUEST_RESPONSE_MAP.put(
        GetMbusEncryptionKeyStatusRequest.class, GetMbusEncryptionKeyStatusResponse.class);
    REQUEST_RESPONSE_MAP.put(ScanMbusChannelsRequest.class, ScanMbusChannelsResponse.class);
  }

  @Autowired private SmartMeteringBundleClient client;

  @Given("^a bundle request$")
  public void aABundleRequest(final Map<String, String> settings) throws Throwable {
    final BundleRequest request = new BundleRequest();
    request.setDeviceIdentification(
        getString(
            settings,
            PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

    final Actions actions = new Actions();
    request.setActions(actions);

    ScenarioContext.current().put(PlatformSmartmeteringKeys.BUNDLE_REQUEST, request);
  }

  @When("^the bundle request is received$")
  public void theBundleRequestIsReceived() throws Throwable {
    final BundleRequest request =
        (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

    final BundleAsyncResponse asyncResponse = this.client.sendBundleRequest(request);

    assertThat(asyncResponse).isNotNull();
    ScenarioContextHelper.saveAsyncResponse(asyncResponse);
  }

  @When("^the bundle request is received with headers$")
  public void theBundleRequestIsReceivedWithHeaders(final Map<String, String> settings)
      throws Throwable {
    final BundleRequest request =
        (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

    /*
     * Scenarios containing this step may include a data table with two columns. The first column
     * should have the local part of the name of a SOAP header element and the second column should
     * have the text value for the named SOAP header element.
     *
     * These headers can be picked up by classes like those in osgp-adapter-ws-shared package
     * org.opensmartgridplatform.adapter.ws.endpointinterceptors, for instance the MessageMetadata
     * handling SoapHeaderMessageMetadataInterceptor or configurations of the more generic
     * SoapHeaderInterceptor.
     */
    final BundleAsyncResponse asyncResponse =
        this.client.sendBundleRequest(request, this.additionalSoapHeaders(settings));

    assertThat(asyncResponse).isNotNull();
    ScenarioContextHelper.saveAsyncResponse(asyncResponse);
  }

  private Map<String, String> additionalSoapHeaders(final Map<String, String> settings) {
    /*
     * These date/time headers are interpreted as a number of milliseconds since the epoch.
     * Since it is hard to come up with nice values for hard coded numbers of milliseconds that make
     * sense in the test steps, these values can be specified in a format the DateTimeHelper
     * understands.
     */
    final Set<String> dateTimeInMillisHeaders =
        new TreeSet<>(Arrays.asList("MaxScheduleTime", "ScheduleTime"));
    final Map<String, String> extraHeaders = new HashMap<>();
    settings.forEach(
        (localPartOfName, value) -> {
          if (dateTimeInMillisHeaders.contains(localPartOfName)) {
            extraHeaders.put(
                localPartOfName,
                Long.toString(DateTimeHelper.getDateTime(value).toInstant().toEpochMilli()));
          } else {
            extraHeaders.put(localPartOfName, value);
          }
        });
    return extraHeaders;
  }

  @Then(
      "^the number of responses in the bundle response should match the number of actions in the bundle request$")
  public void
      theNumberOfResponsesInTheBundleResponseShouldMatchTheNumberOfActionsInTheBundleRequest()
          throws Throwable {

    this.ensureBundleResponse();

    final BundleRequest request =
        (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
    final BundleResponse response =
        (BundleResponse) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

    this.assertSameSize(request, response);
  }

  @Then(
      "^the order of the responses in the bundle response should match the order of actions in the bundle request$")
  public void
      theOrderOfTheResponsesInTheBundleResponseShouldMatchTheOrderOfActionsInTheBundleRequest()
          throws Throwable {

    this.ensureBundleResponse();

    final BundleRequest request =
        (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);
    final BundleResponse response =
        (BundleResponse) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_RESPONSE);

    this.assertSameSize(request, response);
    this.assertSameOrder(request, response);
  }

  private void assertSameSize(
      final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

    final int actionsSize = bundleRequest.getActions().getActionList().size();
    final int responsesSize = bundleResponse.getAllResponses().getResponseList().size();

    assertThat(responsesSize)
        .as(
            "The number of responses in the bundle responses should match the number of actions in the bundle "
                + "request")
        .isEqualTo(actionsSize);
  }

  private void assertSameOrder(
      final BundleRequest bundleRequest, final BundleResponse bundleResponse) {

    final int actionsSize = bundleRequest.getActions().getActionList().size();

    for (int i = 0; i < actionsSize; i++) {
      final Action action = bundleRequest.getActions().getActionList().get(i);
      final Response response = bundleResponse.getAllResponses().getResponseList().get(i);

      assertThat(response.getClass()).isEqualTo(REQUEST_RESPONSE_MAP.get(action.getClass()));
    }
  }

  @Then("the bundle response should be OK")
  public void theBundleResponseShouldBeOK() throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(ActionResponse.class);
    assertThat(response.getResult()).isEqualTo(OsgpResultType.OK);
  }

  @Then("the bundle response should be a FaultResponse with message containing")
  public void theBundleResponseShouldBeAFaultResponseWithMessageContaining(
      final Map<String, String> values) throws Throwable {
    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(FaultResponse.class);
    assertThat(response.getResult()).isEqualTo(OsgpResultType.NOT_OK);

    final FaultResponse faultResponse = (FaultResponse) response;

    assertThat(faultResponse.getMessage()).containsSubsequence(values.get(PlatformKeys.MESSAGE));
  }
}
