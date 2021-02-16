/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActualPowerQualityResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualPowerQualityRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValue;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.GetActualPowerQualityRequestBuilder;

public class BundledGetActualPowerQualitySteps extends BaseBundleSteps {

    private static final String NUMBER_OF_ACTUAL_VALUES = "NumberOfActualValues";
    private static final String NUMBER_OF_CAPTURE_OBJECTS = "NumberOfCaptureObjects";
    private static final String CAPTURE_OBJECT_CLASS_ID = "CaptureObject_ClassId";
    private static final String CAPTURE_OBJECT_LOGICAL_NAME = "CaptureObject_LogicalName";
    private static final String CAPTURE_OBJECT_ATTRIBUTE_INDEX = "CaptureObject_AttributeIndex";
    private static final String CAPTURE_OBJECT_DATA_INDEX = "CaptureObject_DataIndex";
    private static final String CAPTURE_OBJECT_UNIT = "CaptureObject_Unit";

    @Given("^the bundle request contains an actual power quality request with parameters$")
    public void theBundleRequestContainsAGetActualPowerQualityRequestAction(final Map<String, String> parameters)
            throws Throwable {

        final GetActualPowerQualityRequest action = new GetActualPowerQualityRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain an actual power quality response with values$")
    public void theBundleResponseShouldContainAGetActualPowerQualityResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertThat(response)
                .as("Not a valid response, expected ActualPowerQualityResponse but is :" +
                        response.getClass().getSimpleName())
                .isInstanceOf(ActualPowerQualityResponse.class);

        final ActualPowerQualityResponse actualPowerQualityResponse = (ActualPowerQualityResponse) response;
        final ActualPowerQualityData actualPowerQualityData = actualPowerQualityResponse
                .getActualPowerQualityData();

        this.assertEqualCaptureObjects(actualPowerQualityData.getCaptureObjects().getCaptureObjects(), values);
        this.assertEqualActualValues(actualPowerQualityData.getActualValues().getActualValue(), values);
    }

    private void assertEqualCaptureObjects(final List<CaptureObject> actualCaptureObjects,
            final Map<String, String> expectedValues) throws AssertionError {

        final int expectedNumberOfCaptureObjects = SettingsHelper
                .getIntegerValue(expectedValues, NUMBER_OF_CAPTURE_OBJECTS);

        assertThat(actualCaptureObjects.size()).as("Number of capture objects")
                .isEqualTo(expectedNumberOfCaptureObjects);

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final Long expectedClassId = SettingsHelper.getLongValue(expectedValues, CAPTURE_OBJECT_CLASS_ID, i + 1);
            if (expectedClassId != null) {
                final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
                this.assertEqualCaptureObject(actualCaptureObject, expectedValues, i + 1);
            }
        }
    }

    private void assertEqualCaptureObject(final CaptureObject actualCaptureObject,
            final Map<String, String> expectedValues, final int index) throws AssertionError {
        final Long expectedClassId = SettingsHelper.getLongValue(expectedValues, CAPTURE_OBJECT_CLASS_ID, index);
        assertThat(Long.valueOf(actualCaptureObject.getClassId())).as(CAPTURE_OBJECT_CLASS_ID + index)
                                                                  .isEqualTo(expectedClassId);

        final String expectedLogicalName = SettingsHelper
                .getStringValue(expectedValues, CAPTURE_OBJECT_LOGICAL_NAME, index);
        assertThat(actualCaptureObject.getLogicalName()).as(CAPTURE_OBJECT_LOGICAL_NAME + index)
                                                        .isEqualTo(expectedLogicalName);

        final BigInteger expectedAttributeIndex = SettingsHelper
                .getBigIntegerValue(expectedValues, CAPTURE_OBJECT_ATTRIBUTE_INDEX, index);
        assertThat(actualCaptureObject.getAttributeIndex()).as(CAPTURE_OBJECT_ATTRIBUTE_INDEX + index)
                                                           .isEqualTo(expectedAttributeIndex);

        final Long expectedDataIndex = SettingsHelper.getLongValue(expectedValues, CAPTURE_OBJECT_DATA_INDEX, index);
        assertThat(Long.valueOf(actualCaptureObject.getDataIndex())).as(CAPTURE_OBJECT_DATA_INDEX + index)
                                                                    .isEqualTo(expectedDataIndex);

        final String expectedUnit = SettingsHelper.getStringValue(expectedValues, CAPTURE_OBJECT_UNIT, index);
        if (expectedUnit == null) {
            assertThat(actualCaptureObject.getUnit()).as(CAPTURE_OBJECT_UNIT + index).isNull();
        } else {
            assertThat(actualCaptureObject.getUnit().value()).as(CAPTURE_OBJECT_UNIT + index).isEqualTo(expectedUnit);
        }
    }

    private void assertEqualActualValues(final List<ActualValue> actualValues,
            final Map<String, String> expectedValues) {
        final int expectedNumberOfActualValues = SettingsHelper.getIntegerValue(expectedValues,
                NUMBER_OF_ACTUAL_VALUES);

        assertThat(actualValues.size()).as(NUMBER_OF_ACTUAL_VALUES)
                                               .isEqualTo(expectedNumberOfActualValues);
    }
}
