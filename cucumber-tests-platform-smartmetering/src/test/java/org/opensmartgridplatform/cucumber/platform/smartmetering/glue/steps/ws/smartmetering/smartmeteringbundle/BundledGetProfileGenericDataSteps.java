/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        assertTrue("Not a valid response", response instanceof ProfileGenericDataResponse);

        final ProfileGenericDataResponse profileGenericDataResponse = (ProfileGenericDataResponse) response;
        final ProfileGenericData profileGenericData = profileGenericDataResponse.getProfileGenericData();

        this.assertEqualCaptureObjects(profileGenericData.getCaptureObjectList().getCaptureObjects(), values);
        this.assertEqualProfileEntries(profileGenericData.getProfileEntryList().getProfileEntries(), values);
    }

    private void assertEqualCaptureObjects(final List<CaptureObject> actualCaptureObjects,
            final Map<String, String> expectedValues) throws AssertionError {

        final int expectedNumberOfCaptureObjects = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfCaptureObjects");

        assertEquals("Number of capture objects", expectedNumberOfCaptureObjects, actualCaptureObjects.size());

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
            this.assertEqualCaptureObject(actualCaptureObject, expectedValues, i + 1);
        }
    }

    private void assertEqualCaptureObject(final CaptureObject actualCaptureObject,
            final Map<String, String> expectedValues, final int index) throws AssertionError {
        final Long expectedClassId = SettingsHelper.getLongValue(expectedValues, "CaptureObject_ClassId", index);
        assertEquals("ClassId of CaptureObject " + index, expectedClassId,
                Long.valueOf(actualCaptureObject.getClassId()));

        final String expectedLogicalName = SettingsHelper.getStringValue(expectedValues, "CaptureObject_LogicalName",
                index);
        assertEquals("LogicalName of CaptureObject " + index, expectedLogicalName,
                actualCaptureObject.getLogicalName());

        final BigInteger expectedAttributeIndex = SettingsHelper.getBigIntegerValue(expectedValues,
                "CaptureObject_AttributeIndex", index);
        assertEquals("AttributeIndex of CaptureObject " + index, expectedAttributeIndex,
                actualCaptureObject.getAttributeIndex());

        final Long expectedDataIndex = SettingsHelper.getLongValue(expectedValues, "CaptureObject_DataIndex", index);
        assertEquals("DataIndex of CaptureObject " + index, expectedDataIndex,
                Long.valueOf(actualCaptureObject.getDataIndex()));

        final String expectedUnit = SettingsHelper.getStringValue(expectedValues, "CaptureObject_Unit", index);
        assertEquals("Unit of CaptureObject " + index, expectedUnit, actualCaptureObject.getUnit().value());
    }

    private void assertEqualProfileEntries(final List<ProfileEntry> actualProfileEntries,
            final Map<String, String> expectedValues) {
        final int expectedNumberOfProfileEntries = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfProfileEntries");

        assertEquals("Number of profile entries", expectedNumberOfProfileEntries, actualProfileEntries.size());
    }
}
