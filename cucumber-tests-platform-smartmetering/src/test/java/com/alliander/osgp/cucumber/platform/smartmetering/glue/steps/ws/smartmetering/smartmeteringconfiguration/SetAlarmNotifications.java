/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetAlarmNotificationsRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetAlarmNotifications extends SmartMeteringStepsBase {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotifications.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the set alarm notifications request is received$")
    public void theSetAlarmNotificationsRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetAlarmNotificationsRequest setAlarmNotificationsRequest = SetAlarmNotificationsRequestFactory
                .fromParameterMap(requestData);

        final SetAlarmNotificationsAsyncResponse setAlarmNotificationsAsyncResponse = this.smartMeteringConfigurationClient
                .setAlarmNotifications(setAlarmNotificationsRequest);

        LOGGER.info("Set alarm notifications response is received {}", setAlarmNotificationsAsyncResponse);

        assertNotNull("Set alarm notifications response should not be null", setAlarmNotificationsAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setAlarmNotificationsAsyncResponse.getAsyncResponse().getCorrelationUid());
    }

    @Then("^the specified alarm notifications should be set on the device$")
    public void theSpecifiedAlarmNotificationsShouldBeSetOnTheDevice(final Map<String, String> settings)
            throws Throwable {
        final SetAlarmNotificationsAsyncRequest setAlarmNotificationsAsyncRequest = SetAlarmNotificationsRequestFactory
                .fromScenarioContext();
        final SetAlarmNotificationsResponse setAlarmNotificationsResponse = this.smartMeteringConfigurationClient
                .retrieveSetAlarmNotificationsResponse(setAlarmNotificationsAsyncRequest);

        LOGGER.info("The set alarm notifications result is: {}", setAlarmNotificationsResponse.getResult());

        assertNotNull("The set alarm notifications result is null", setAlarmNotificationsResponse.getResult());
        assertEquals("The set alarm notifications should be OK", OsgpResultType.OK,
                setAlarmNotificationsResponse.getResult());
    }
}
