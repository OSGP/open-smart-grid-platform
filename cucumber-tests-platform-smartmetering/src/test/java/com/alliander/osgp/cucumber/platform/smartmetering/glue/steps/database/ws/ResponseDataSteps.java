/**
 * Copyright 2017 Smart Society Services B.V.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.cucumber.core.DateTimeHelper;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ResponseDataSteps extends BaseDeviceSteps {

    @Autowired
    private ResponseDataRepository responseDataRespository;

    @Given("^a response data record$")
    @Transactional("txMgrRespData")
    public ResponseData aResponseDataRecord(final Map<String, String> settings) throws Throwable {

        ResponseData responseData = new ResponseDataBuilder().fromSettings(settings).build();

        /*
         * For the smart metering tests, as long as it is not possible to
         * capture notifications for the response data, some workaround can be
         * used that gives reasonable confidence the notification is sent. For
         * this the response data can be read from the database, and the number
         * of notifications sent for the response data can be compared to the
         * number stored in the scenario context. If higher it is probably safe
         * to assume a notification has actually been sent.
         */
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, responseData.getCorrelationUid());
        ScenarioContext.current().put(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT,
                responseData.getNumberOfNotificationsSent());

        responseData = this.responseDataRespository.save(responseData);

        // set correct creation time for testing after inserting in the database
        // (as it will be overridden on first save)
        if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
            final Field fld = responseData.getClass().getSuperclass().getDeclaredField("creationTime");
            fld.setAccessible(true);
            fld.set(responseData, DateTimeHelper.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)).toDate());
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
        final ResponseData responseData = this.responseDataRespository.findByCorrelationUid(correlationUid);

        assertEquals(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT,
                settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT),
                responseData.getNumberOfNotificationsSent().toString());
        assertEquals(PlatformKeys.KEY_MESSAGE_TYPE, settings.get(PlatformKeys.KEY_MESSAGE_TYPE),
                responseData.getMessageType());
    }
}
