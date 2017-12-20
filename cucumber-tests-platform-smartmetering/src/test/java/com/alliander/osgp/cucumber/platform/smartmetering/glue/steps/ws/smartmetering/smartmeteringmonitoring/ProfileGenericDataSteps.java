/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ProfileGenericDataRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ProfileGenericDataSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ProfileGenericDataAsyncResponse, ProfileGenericDataRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ProfileGenericDataResponse, ProfileGenericDataAsyncRequest> responseClient;

    @When("^the get profile generic data request is received$")
    public void theGetProfileGenericDataRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ProfileGenericDataRequest request = ProfileGenericDataRequestFactory.fromParameterMap(settings);
        final ProfileGenericDataAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the profile generic data result should be returned$")
    public void theProfileGenericDataResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ProfileGenericDataAsyncRequest asyncRequest = ProfileGenericDataRequestFactory.fromScenarioContext();

        final ProfileGenericDataResponse response = this.responseClient.getResponse(asyncRequest);
        assertNotNull("ProfileGenericDataResponse should not be null", response);

        final int expectedNumberOfCaptureObjects = Helpers.getInteger(settings, "NumberOfCaptureObjects", 0);
        final List<CaptureObject> actualCaptureObjects = response.getCaptureObjectList().getCaptureObjects();
        assertEquals("Number of capture objects", expectedNumberOfCaptureObjects, actualCaptureObjects.size());

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
            this.validateCaptureObject(actualCaptureObject, settings, i + 1);
        }

        final int expectedNumberOfProfileEntries = Helpers.getInteger(settings, "NumberOfProfileEntries", 0);
        final List<ProfileEntry> actualProfileEntries = response.getProfileEntryList().getProfileEntries();
        assertEquals("Number of profile entries", expectedNumberOfProfileEntries, actualProfileEntries.size());

        if (expectedNumberOfProfileEntries > 0) {
            /*
             * Expected value equals expectedNumberOfCaptureObjects, because the number of ProfileEntryValues in a
             * ProfileEntry should match the number of captured objects from the buffer.
             */
            assertEquals("Number of profile entry values", expectedNumberOfCaptureObjects,
                    actualProfileEntries.get(0).getProfileEntryValue().size());
        }
    }

    private void validateCaptureObject(final CaptureObject actualCaptureObject, final Map<String, String> settings,
            final int index) {

        final Long expectedClassId = SettingsHelper.getLongValue(settings, "CaptureObject_ClassId", index);
        assertEquals("ClassId of CaptureObject " + index, expectedClassId,
                Long.valueOf(actualCaptureObject.getClassId()));

        final String expectedLogicalName = SettingsHelper.getStringValue(settings, "CaptureObject_LogicalName", index);
        assertEquals("LogicalName of CaptureObject " + index, expectedLogicalName,
                actualCaptureObject.getLogicalName());

        final BigInteger expectedAttributeIndex = SettingsHelper.getBigIntegerValue(settings,
                "CaptureObject_AttributeIndex", index);
        assertEquals("AttributeIndex of CaptureObject " + index, expectedAttributeIndex,
                actualCaptureObject.getAttributeIndex());

        final Long expectedDataIndex = SettingsHelper.getLongValue(settings, "CaptureObject_DataIndex", index);
        assertEquals("DataIndex of CaptureObject " + index, expectedDataIndex,
                Long.valueOf(actualCaptureObject.getDataIndex()));

        final String expectedUnit = SettingsHelper.getStringValue(settings, "CaptureObject_Unit", index);
        assertEquals("Unit of CaptureObject " + index, expectedUnit, actualCaptureObject.getUnit().value());
    }

}
