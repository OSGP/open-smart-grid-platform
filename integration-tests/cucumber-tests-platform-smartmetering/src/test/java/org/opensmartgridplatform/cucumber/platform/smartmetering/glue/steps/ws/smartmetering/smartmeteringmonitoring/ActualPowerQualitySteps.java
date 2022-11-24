/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValue;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualPowerQualityRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ActualPowerQualitySteps {

  @Autowired
  private SmartMeteringMonitoringRequestClient<
          ActualPowerQualityAsyncResponse, ActualPowerQualityRequest>
      requestClient;

  @Autowired
  private SmartMeteringMonitoringResponseClient<
          ActualPowerQualityResponse, ActualPowerQualityAsyncRequest>
      responseClient;

  @When("^the get actual power quality request is received$")
  public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings)
      throws Throwable {
    final ActualPowerQualityRequest request =
        ActualPowerQualityRequestFactory.fromParameterMap(settings);

    final ActualPowerQualityAsyncResponse asyncResponse = this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @When("^the get actual power quality request generating an error is received$")
  public void theGetActualMeterReadsRequestGeneratingAnErrorIsReceived(
      final Map<String, String> settings) throws Throwable {
    final ActualPowerQualityRequest request =
        ActualPowerQualityRequestFactory.fromParameterMap(settings);
    final ActualPowerQualityAsyncResponse asyncResponse = this.requestClient.doRequest(request);

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

    final ActualPowerQualityAsyncRequest actualPowerQualityAsyncRequest =
        ActualPowerQualityRequestFactory.fromScenarioContext();

    try {
      final ActualPowerQualityResponse response =
          this.responseClient.getResponse(actualPowerQualityAsyncRequest);
      fail("Expected exception, but got a response: %s", response.toString());
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @Then("^the actual power quality result should be returned$")
  public void theActualPowerQualityResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final ActualPowerQualityAsyncRequest asyncRequest =
        ActualPowerQualityRequestFactory.fromScenarioContext();

    final ActualPowerQualityResponse response = this.responseClient.getResponse(asyncRequest);
    assertThat(response).as("ActualPowerQualityResponseData should not be null").isNotNull();

    final int expectedNumberOfPowerQualityObjects =
        getInteger(settings, "NumberOfPowerQualityObjects", 0);
    final List<PowerQualityObject> actualPowerQualityObjects =
        response.getActualPowerQualityData().getPowerQualityObjects().getPowerQualityObject();
    assertThat(actualPowerQualityObjects.size())
        .as("Number of power quality objects")
        .isEqualTo(expectedNumberOfPowerQualityObjects);

    final String expectedName =
        SettingsHelper.getStringValue(settings, "PowerQualityObject_Name", 1);
    // Only check the received objects if there are expected objects defined in the settings
    if (expectedName != null) {
      for (int i = 0; i < expectedNumberOfPowerQualityObjects; i++) {
        final PowerQualityObject actualPowerQualityObject = actualPowerQualityObjects.get(i);
        this.validatePowerQualityObject(actualPowerQualityObject, settings, i + 1);
      }
    }

    final int expectedNumberOfPowerQualityValues =
        getInteger(settings, "NumberOfPowerQualityValues", 0);
    final List<PowerQualityValue> powerQualityValues =
        response.getActualPowerQualityData().getPowerQualityValues().getPowerQualityValue();
    assertThat(powerQualityValues.size())
        .as("Number of power quality values")
        .isEqualTo(expectedNumberOfPowerQualityValues);

    if (expectedNumberOfPowerQualityValues > 0) {
      /*
       * Expected value equals expectedNumberOfPowerQualityObjects,
       * because the number of PowerQualityValues should match the number
       * of power quality objects from the buffer.
       */
      assertThat(powerQualityValues.size())
          .as("Number of power quality values")
          .isEqualTo(expectedNumberOfPowerQualityObjects);
    }
  }

  private void validatePowerQualityObject(
      final PowerQualityObject actualPowerQualityObject,
      final Map<String, String> settings,
      final int index) {

    final String expectedName =
        SettingsHelper.getStringValue(settings, "PowerQualityObject_Name", index);
    assertThat(actualPowerQualityObject.getName())
        .as("LogicalName of PowerQualityObject " + index)
        .isEqualTo(expectedName);

    final String expectedUnit =
        SettingsHelper.getStringValue(settings, "PowerQualityObject_Unit", index);
    if (expectedUnit != null) {
      assertThat(actualPowerQualityObject.getUnit().value())
          .as("Unit of PowerQualityObject " + index)
          .isEqualTo(expectedUnit);
    }
  }
}
