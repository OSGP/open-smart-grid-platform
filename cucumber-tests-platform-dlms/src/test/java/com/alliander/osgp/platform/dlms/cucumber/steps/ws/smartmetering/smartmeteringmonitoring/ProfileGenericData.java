/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringmonitoring;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.platform.dlms.cucumber.builders.ProfileGenericDataRequestBuilder;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering.SmartMeteringMonitoringManagementClient;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ProfileGenericData extends SmartMeteringStepsBase {

    @Autowired
    private SmartMeteringMonitoringManagementClient client;

    @When("^the get profile generic data request is received$")
    public void theGetProfileGenericDataRequestIsReceived(DataTable settings) throws Throwable {
        ProfileGenericDataRequest request = new ProfileGenericDataRequestBuilder().build();// TODO
                                                                                           // .with
        this.client.requestProfileGenericData(request);
    }

    @Then("^the profile generic data result should be returned$")
    public void theProfileGenericDataResultShouldBeReturned(DataTable arg1) throws Throwable {
    }

}
