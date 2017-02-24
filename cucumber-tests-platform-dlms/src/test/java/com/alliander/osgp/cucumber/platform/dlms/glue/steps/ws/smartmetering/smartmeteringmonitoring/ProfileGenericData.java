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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.dlms.builders.ObisCodeValuesBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.ProfileGenericDataAsyncRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.ProfileGenericDataRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.SmartMeteringMonitoringManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ProfileGenericData extends SmartMeteringStepsBase {

    @Autowired
    private SmartMeteringMonitoringManagementClient client;

    @When("^the get profile generic data request is received$")
    public void theGetProfileGenericDataRequestIsReceived(final Map<String, String> settings) throws Throwable {
        ProfileGenericDataRequest request = new ProfileGenericDataRequestBuilder()
                .withDeviceidentification(getString(settings, "DeviceIdentification", "TEST1024000000001"))
                .withObisCode(this.fillObisCode(settings)).withBeginDate(this.fillDate(settings, "beginDate"))
                .withEndDate(this.fillDate(settings, "endDate")).build();

        ProfileGenericDataAsyncResponse asyncResponse = this.client.requestProfileGenericData(request);
        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    @Then("^the profile generic data result should be returned$")
    public void theProfileGenericDataResultShouldBeReturned(final Map<String, String> settings) throws Throwable {
        ProfileGenericDataAsyncRequest request = (ProfileGenericDataAsyncRequest) new ProfileGenericDataAsyncRequestBuilder()
                .fromContext().build();

        ProfileGenericDataResponse response = this.client.getProfileGenericDataResponse(request);
        assertNotNull("ProfileGenericDataResponse should not be null", response);
        assertTrue("ProfileGenericDataResponse should contain capture objects",
                response.getCaptureObjects().getCaptureObject().size() > 0);
        assertTrue("ProfileGenericDataResponse should contain profile entries",
                response.getProfileEntries().getProfileEntry().size() > 0);
    }

    private ObisCodeValues fillObisCode(final Map<String, String> settings) {
        return new ObisCodeValuesBuilder().fromSettings(settings).build();
    }

    private DateTime fillDate(final Map<String, String> settings, final String key) {
        return getDate(settings, key, new DateTime());
    }
}
