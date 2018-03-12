/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.database.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.ResponseUrlData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.ResponseUrlDataRepository;
import com.alliander.osgp.cucumber.core.DateTimeHelper;
import com.alliander.osgp.cucumber.core.RetryableAssert;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ResponseUrlDataSteps {

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRespository;

    @Given("^a response url data record$")
    public void aResponseUrlDataRecord(final Map<String, String> settings) throws Throwable {
        final ResponseUrlData responseUrlData = this.responseUrlDataRespository
                .save(new ResponseUrlDataBuilder().fromSettings(settings).build());

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, responseUrlData.getCorrelationUid());

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final Field fld = responseUrlData.getClass().getSuperclass().getDeclaredField("creationTime");
            fld.setAccessible(true);
            fld.set(responseUrlData, DateTimeHelper.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)).toDate());
            this.responseUrlDataRespository.saveAndFlush(responseUrlData);
        }

    }

    @Then("^the response url data has values$")
    public void theResponseUrlDataHasValues(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        final String expectedResponseUrl = settings.get(PlatformKeys.KEY_RESPONSE_URL);

        RetryableAssert.assertWithRetries(
                () -> ResponseUrlDataSteps.this.assertResponseUrlData(correlationUid, expectedResponseUrl), 3, 200,
                TimeUnit.MILLISECONDS);

    }

    private void assertResponseUrlData(final String correlationUid, final String expectedResponseUrl) {

        final ResponseUrlData responseUrlData = this.responseUrlDataRespository
                .findSingleResultByCorrelationUid(correlationUid);

        assertEquals(PlatformKeys.KEY_RESPONSE_URL, expectedResponseUrl, responseUrlData.getResponseUrl());

    }

    @Then("^the response url data record with correlation uid \\\"(.*)\\\" should be deleted$")
    public void theResponseUrlDataRecordShouldBeDeleted(final String correlationUid) {
        final ResponseUrlData responseUrlData = this.responseUrlDataRespository
                .findSingleResultByCorrelationUid(correlationUid);

        assertNull("Response url data should be deleted", responseUrlData);
    }

    @Then("^the response url data record with correlation uid \\\"(.*)\\\" should not be deleted$")
    public void theResponseUrlDataRecordShouldNotBeDeleted(final String correlationUid) {
        final ResponseUrlData responseUrlData = this.responseUrlDataRespository
                .findSingleResultByCorrelationUid(correlationUid);

        assertNotNull("Response url data should not be deleted", responseUrlData);
    }

}
