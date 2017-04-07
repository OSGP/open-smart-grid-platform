/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getDate;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.dlms.builders.CaptureObjectDefinitionsBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.ObisCodeValuesBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.ProfileGenericDataAsyncRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.ProfileGenericDataRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.monitoring.SmartMeteringMonitoringClient;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ProfileGenericData extends SmartMeteringStepsBase {

    @Autowired
    private SmartMeteringMonitoringClient client;

    @When("^the get profile generic data request is received$")
    public void theGetProfileGenericDataRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final ProfileGenericDataRequest request = new ProfileGenericDataRequestBuilder()
                .withDeviceidentification(getString(settings, "DeviceIdentification", "TEST1024000000001"))
                .withObisCode(this.fillObisCode(settings)).withBeginDate(this.fillDate(settings, "BeginDate"))
                .withEndDate(this.fillDate(settings, "EndDate")).withSelectedValues(this.fillSelectedValues(settings))
                .build();

        final ProfileGenericDataAsyncResponse asyncResponse = this.client.requestProfileGenericData(request);
        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    @Then("^the profile generic data result should be returned$")
    public void theProfileGenericDataResultShouldBeReturned(final Map<String, String> settings) throws Throwable {
        final ProfileGenericDataAsyncRequest request = new ProfileGenericDataAsyncRequestBuilder().fromContext()
                .build();

        final ProfileGenericDataResponse response = this.client.getProfileGenericDataResponse(request);
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
             * Expected value equals expectedNumberOfCaptureObjects, because the
             * number of ProfileEntryValues in a ProfileEntry should match the
             * number of captured objects from the buffer.
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

    private ObisCodeValues fillObisCode(final Map<String, String> settings) {
        return new ObisCodeValuesBuilder().fromSettings(settings).build();
    }

    private CaptureObjectDefinitions fillSelectedValues(final Map<String, String> settings) {
        return new CaptureObjectDefinitionsBuilder().fromSettings(settings).build();
    }

    private DateTime fillDate(final Map<String, String> settings, final String key) {
        return getDate(settings, key, new DateTime());
    }
}
