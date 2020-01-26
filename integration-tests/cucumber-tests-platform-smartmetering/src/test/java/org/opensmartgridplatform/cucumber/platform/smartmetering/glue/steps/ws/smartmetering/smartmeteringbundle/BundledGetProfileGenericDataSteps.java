/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ProfileGenericDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ProfileEntry;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ProfileGenericData;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.builders.GetProfileGenericDataRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetProfileGenericDataSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get profile generic data action with parameters$")
    public void theBundleRequestContainsAGetProfileGenericDataAction(final Map<String, String> parameters)
            throws Throwable {

        final GetProfileGenericDataRequest action = new GetProfileGenericDataRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a profile generic data response with values$")
    public void theBundleResponseShouldContainAProfileGenericDataResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertThat(response instanceof ProfileGenericDataResponse).as("Not a valid response").isTrue();

        final ProfileGenericDataResponse profileGenericDataResponse = (ProfileGenericDataResponse) response;
        final ProfileGenericData profileGenericData = profileGenericDataResponse.getProfileGenericData();

        this.assertEqualCaptureObjects(profileGenericData.getCaptureObjectList().getCaptureObjects(), values);
        this.assertEqualProfileEntries(profileGenericData.getProfileEntryList().getProfileEntries(), values);
    }

    private void assertEqualCaptureObjects(final List<CaptureObject> actualCaptureObjects,
            final Map<String, String> expectedValues) throws AssertionError {

        final int expectedNumberOfCaptureObjects = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfCaptureObjects");

        assertThat(actualCaptureObjects.size()).as("Number of capture objects")
                .isEqualTo(expectedNumberOfCaptureObjects);

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
            this.assertEqualCaptureObject(actualCaptureObject, expectedValues, i + 1);
        }
    }

    private void assertEqualCaptureObject(final CaptureObject actualCaptureObject,
            final Map<String, String> expectedValues, final int index) throws AssertionError {
        final Long expectedClassId = SettingsHelper.getLongValue(expectedValues, "CaptureObject_ClassId", index);
        assertThat(Long.valueOf(actualCaptureObject.getClassId())).as("ClassId of CaptureObject " + index)
                .isEqualTo(expectedClassId);

        final String expectedLogicalName = SettingsHelper.getStringValue(expectedValues, "CaptureObject_LogicalName",
                index);
        assertThat(actualCaptureObject.getLogicalName()).as("LogicalName of CaptureObject " + index)
                .isEqualTo(expectedLogicalName);

        final BigInteger expectedAttributeIndex = SettingsHelper.getBigIntegerValue(expectedValues,
                "CaptureObject_AttributeIndex", index);
        assertThat(actualCaptureObject.getAttributeIndex()).as("AttributeIndex of CaptureObject " + index)
                .isEqualTo(expectedAttributeIndex);

        final Long expectedDataIndex = SettingsHelper.getLongValue(expectedValues, "CaptureObject_DataIndex", index);
        assertThat(Long.valueOf(actualCaptureObject.getDataIndex())).as("DataIndex of CaptureObject " + index)
                .isEqualTo(expectedDataIndex);

        final String expectedUnit = SettingsHelper.getStringValue(expectedValues, "CaptureObject_Unit", index);
        assertThat(actualCaptureObject.getUnit().value()).as("Unit of CaptureObject " + index).isEqualTo(expectedUnit);
    }

    private void assertEqualProfileEntries(final List<ProfileEntry> actualProfileEntries,
            final Map<String, String> expectedValues) {
        final int expectedNumberOfProfileEntries = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfProfileEntries");

        assertThat(actualProfileEntries.size()).as("Number of profile entries")
                .isEqualTo(expectedNumberOfProfileEntries);
    }
}
