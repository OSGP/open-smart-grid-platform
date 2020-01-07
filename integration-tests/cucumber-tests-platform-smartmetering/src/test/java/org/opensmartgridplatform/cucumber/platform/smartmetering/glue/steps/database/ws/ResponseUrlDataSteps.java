/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ResponseUrlDataSteps {

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRepository;

    @Given("^a response url data record$")
    public void aResponseUrlDataRecord(final Map<String, String> settings) throws Throwable {
        final ResponseUrlData responseUrlData = this.responseUrlDataRepository
                .save(new ResponseUrlDataBuilder().fromSettings(settings).build());

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, responseUrlData.getCorrelationUid());

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final Field fld = responseUrlData.getClass().getSuperclass().getDeclaredField("creationTime");
            fld.setAccessible(true);
            fld.set(responseUrlData, DateTimeHelper.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)).toDate());
            this.responseUrlDataRepository.saveAndFlush(responseUrlData);
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

        final ResponseUrlData responseUrlData = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        assertThat(responseUrlData.getResponseUrl()).as(PlatformKeys.KEY_RESPONSE_URL).isEqualTo(expectedResponseUrl);
    }

    @Then("^the response url data record with correlation uid \\\"(.*)\\\" should be deleted$")
    public void theResponseUrlDataRecordShouldBeDeleted(final String correlationUid) {
        final ResponseUrlData responseUrlData = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        assertThat(responseUrlData).as("Response url data should be deleted").isNull();
    }

    @Then("^the response url data record with correlation uid \\\"(.*)\\\" should not be deleted$")
    public void theResponseUrlDataRecordShouldNotBeDeleted(final String correlationUid) {
        final ResponseUrlData responseUrlData = this.responseUrlDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        assertThat(responseUrlData).as("Response url data should not be deleted").isNotNull();
    }

}
