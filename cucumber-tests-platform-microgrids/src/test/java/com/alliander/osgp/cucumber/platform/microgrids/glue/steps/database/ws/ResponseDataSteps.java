/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.database.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ResponseDataSteps extends BaseDeviceSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseDataSteps.class);

    @Autowired
    private ResponseDataRepository responseDataRespository;

    @Value("${iec61850.rtu.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;

    @Value("${iec61850.rtu.response.wait.fail.duration:180000}")
    private int waitFailMillis;

    @Given("^a response data record$")
    @Transactional("txMgrWsMicrogrids")
    public ResponseData aResponseDataRecord(final Map<String, String> settings) throws Throwable {

        ResponseData responseData = new ResponseDataBuilder().fromSettings(settings).build();
        responseData = this.responseDataRespository.save(responseData);

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final Field fld = responseData.getClass().getSuperclass().getDeclaredField("creationTime");
            fld.setAccessible(true);
            fld.set(responseData, Helpers.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)).toDate());
            this.responseDataRespository.save(responseData);
        }

        return responseData;
    }

    @Then("^the response data record with correlation uid \\\"(.*)\\\" should be deleted$")
    public void theResponseDataRecordShouldBeDeleted(final String correlationUid) {
        final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

        assertNull("Response data should be deleted", responseData);
    }

    @Then("^the response data record with correlation uid \\\"(.*)\\\" should not be deleted$")
    public void theResponseDataRecordShouldNotBeDeleted(final String correlationUid) {
        final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

        assertNotNull("Response data should not be deleted", responseData);
    }

    @Then("^the response data has values$")
    public void theResponseDataHasValues(final Map<String, String> settings) throws Throwable {
        final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
        ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

        final int maxtime = this.waitFailMillis;
        final int timeout = this.waitCheckIntervalMillis;

        for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
            try {
                Thread.sleep(timeout);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }
            responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);
            if (settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)
                    .equals(responseData.getNumberOfNotificationsSent().toString())) {
                break;
            }
        }

        assertEquals("NumberOfNotificationsSent is not as expected",
                settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT),
                responseData.getNumberOfNotificationsSent().toString());
        assertEquals("MessageType is not as expected", settings.get(PlatformKeys.KEY_MESSAGE_TYPE),
                responseData.getMessageType());
    }
}
