//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualPowerQualityRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PowerQualityValue;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.GetActualPowerQualityRequestBuilder;

public class BundledGetActualPowerQualitySteps extends BaseBundleSteps {

  private static final String NUMBER_OF_POWER_QUALITY_VALUES = "NumberOfPowerQualityValues";
  private static final String NUMBER_OF_POWER_QUALITY_OBJECTS = "NumberOfPowerQualityObjects";
  private static final String POWER_QUALITY_OBJECT_NAME = "PowerQualityObject_Name";
  private static final String POWER_QUALITY_OBJECT_UNIT = "PowerQualityObject_Unit";

  @Given("^the bundle request contains an actual power quality request with parameters$")
  public void theBundleRequestContainsAGetActualPowerQualityRequestAction(
      final Map<String, String> parameters) throws Throwable {

    final GetActualPowerQualityRequest action =
        new GetActualPowerQualityRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }

  @Then("^the bundle response should contain an actual power quality response with values$")
  public void theBundleResponseShouldContainAGetActualPowerQualityResponse(
      final Map<String, String> values) throws Throwable {

    final Response response = this.getNextBundleResponse();

    assertThat(response).isInstanceOf(ActualPowerQualityResponse.class);

    final ActualPowerQualityResponse actualPowerQualityResponse =
        (ActualPowerQualityResponse) response;
    final ActualPowerQualityData actualPowerQualityData =
        actualPowerQualityResponse.getActualPowerQualityData();

    this.assertEqualPowerQualityObjects(
        actualPowerQualityData.getPowerQualityObjects().getPowerQualityObject(), values);
    this.assertEqualPowerQualityValues(
        actualPowerQualityData.getPowerQualityValues().getPowerQualityValue(), values);
  }

  private void assertEqualPowerQualityObjects(
      final List<PowerQualityObject> actualPowerQualityObjects,
      final Map<String, String> expectedValues)
      throws AssertionError {

    final int expectedNumberOfPowerQualityObjects =
        SettingsHelper.getIntegerValue(expectedValues, NUMBER_OF_POWER_QUALITY_OBJECTS);

    assertThat(actualPowerQualityObjects.size())
        .as("Number of power quality objects")
        .isEqualTo(expectedNumberOfPowerQualityObjects);

    for (int i = 0; i < expectedNumberOfPowerQualityObjects; i++) {
      final String expectedName =
          SettingsHelper.getStringValue(expectedValues, POWER_QUALITY_OBJECT_NAME, i + 1);
      if (expectedName != null) {
        final PowerQualityObject actualPowerQualityObject = actualPowerQualityObjects.get(i);
        this.assertEqualPowerQualityObject(actualPowerQualityObject, expectedValues, i + 1);
      }
    }
  }

  private void assertEqualPowerQualityObject(
      final PowerQualityObject actualPowerQualityObject,
      final Map<String, String> expectedValues,
      final int index)
      throws AssertionError {
    final String expectedName =
        SettingsHelper.getStringValue(expectedValues, POWER_QUALITY_OBJECT_NAME, index);
    assertThat(actualPowerQualityObject.getName())
        .as(POWER_QUALITY_OBJECT_NAME + index)
        .isEqualTo(expectedName);

    final String expectedUnit =
        SettingsHelper.getStringValue(expectedValues, POWER_QUALITY_OBJECT_UNIT, index);
    if (expectedUnit == null) {
      assertThat(actualPowerQualityObject.getUnit()).as(POWER_QUALITY_OBJECT_UNIT + index).isNull();
    } else {
      assertThat(actualPowerQualityObject.getUnit().value())
          .as(POWER_QUALITY_OBJECT_UNIT + index)
          .isEqualTo(expectedUnit);
    }
  }

  private void assertEqualPowerQualityValues(
      final List<PowerQualityValue> powerQualityValues, final Map<String, String> expectedValues) {
    final int expectedNumberOfPowerQualityValues =
        SettingsHelper.getIntegerValue(expectedValues, NUMBER_OF_POWER_QUALITY_VALUES);

    assertThat(powerQualityValues.size())
        .as(NUMBER_OF_POWER_QUALITY_VALUES)
        .isEqualTo(expectedNumberOfPowerQualityValues);
  }
}
