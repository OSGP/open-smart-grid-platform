/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponseData;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.BundleRequestBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.GetProfileGenericDataRequestBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetProfileGenericDataSteps {
    @Autowired
    private SmartMeteringBundleClient client;

    @When("^a get profile generic data request is received as part of a bundled request$")
    public void whenAGetProfileGenericDataBundleRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final GetProfileGenericDataRequest action = new GetProfileGenericDataRequestBuilder().fromParameterMap(settings)
                .build();

        final Actions actions = new Actions();
        actions.getActionList().add(action);

        final BundleRequest request = new BundleRequestBuilder()
                .withDeviceIdentification(settings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION)).withActions(actions).build();
        final BundleAsyncResponse response = this.client.sendBundleRequest(request);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.CORRELATION_UID, response.getCorrelationUid());
        ScenarioContext.current().put(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION, response.getDeviceIdentification());
    }

    @Then("^the profile generic data should be part of the bundle response$")
    public void thenTheProfileGenericDataShouldBePartOfTheBundleResponse(final Map<String, String> settings)
            throws Throwable {

        final BundleAsyncRequest request = new BundleAsyncRequest();
        request.setCorrelationUid((String) ScenarioContext.current().get(PlatformSmartmeteringKeys.CORRELATION_UID));
        request.setDeviceIdentification((String) ScenarioContext.current().get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

        final BundleResponse bundleResponse = this.client.retrieveBundleResponse(request);
        final ProfileGenericDataResponseData profileGenericDataResponseData = (ProfileGenericDataResponseData) bundleResponse
                .getAllResponses().getResponseList().get(0);
        final ProfileGenericData profileGenericData = profileGenericDataResponseData.getProfileGenericData();

        this.assertEqualCaptureObjects(profileGenericData.getCaptureObjectList().getCaptureObjects(), settings);
        this.assertEqualProfileEntries(profileGenericData.getProfileEntryList().getProfileEntries(), settings);
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
