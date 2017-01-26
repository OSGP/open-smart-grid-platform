/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.ScheduleManagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.TriggerType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.publiclighting.PublicLightingScheduleManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class SetLightScheduleSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private PublicLightingScheduleManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetLightScheduleSteps.class);

    /**
     * Sends a Set Schedule request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set light schedule request$")
    public void receivingASetLightScheduleRequest(final Map<String, String> requestParameters) throws Throwable {

        this.callAddSchedule(requestParameters, 1);
    }

    /**
     * Sends a Set Schedule request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set light schedule request for (\\d+) schedules?$")
    public void receivingASetLightScheduleRequestForSchedules(final Integer countSchedules,
            final Map<String, String> requestParameters) throws Throwable {

        this.callAddSchedule(requestParameters, countSchedules);
    }

    private void callAddSchedule(final Map<String, String> requestParameters, final Integer countSchedules)
            throws Throwable {

        final SetScheduleRequest request = new SetScheduleRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        if (requestParameters.containsKey(Keys.SCHEDULE_SCHEDULEDTIME)) {
            request.setScheduledTime(DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(((requestParameters.get(Keys.SCHEDULE_SCHEDULEDTIME).isEmpty())
                            ? DateTime.now() : getDate(requestParameters, Keys.SCHEDULE_SCHEDULEDTIME))
                                    .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }

        for (int i = 0; i < countSchedules; i++) {
            this.addScheduleForRequest(request, getEnum(requestParameters, Keys.SCHEDULE_WEEKDAY, WeekDayType.class),
                    getString(requestParameters, Keys.SCHEDULE_STARTDAY),
                    getString(requestParameters, Keys.SCHEDULE_ENDDAY),
                    getEnum(requestParameters, Keys.SCHEDULE_ACTIONTIME, ActionTimeType.class),
                    getString(requestParameters, Keys.SCHEDULE_TIME),
                    getString(requestParameters, Keys.SCHEDULE_LIGHTVALUES),
                    getString(requestParameters, Keys.SCHEDULE_TRIGGERTYPE),
                    getString(requestParameters, Keys.SCHEDULE_TRIGGERWINDOW));
        }
        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setSchedule(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    private void addScheduleForRequest(final SetScheduleRequest request, final WeekDayType weekDay,
            final String startDay, final String endDay, final ActionTimeType actionTime, final String time,
            final String scheduleLightValue, final String triggerType, final String triggerWindow)
            throws DatatypeConfigurationException {
        final Schedule schedule = new Schedule();
        schedule.setWeekDay(weekDay);
        if (!startDay.isEmpty()) {
            schedule.setStartDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTime.parse(startDay).toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }
        if (!endDay.isEmpty()) {
            schedule.setEndDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTime.parse(endDay).toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }
        schedule.setActionTime(actionTime);
        schedule.setTime(time);

        for (final String lightValue : scheduleLightValue.split(";")) {
            final LightValue lv = new LightValue();
            final String[] lightValues = lightValue.split(",");
            lv.setIndex(Integer.parseInt(lightValues[0]));
            lv.setOn(Boolean.parseBoolean(lightValues[1]));
            if (lightValues.length > 2) {
                lv.setDimValue(Integer.parseInt(lightValues[2]));
            }

            schedule.getLightValue().add(lv);
        }

        if (!triggerType.isEmpty()) {
            schedule.setTriggerType(TriggerType.valueOf(triggerType));
        }

        final String[] windowTypeValues = triggerWindow.split(",");
        if (windowTypeValues.length == 2) {
            final WindowType windowType = new WindowType();
            windowType.setMinutesBefore(Integer.parseInt(windowTypeValues[0]));
            windowType.setMinutesAfter(Integer.parseInt(windowTypeValues[1]));

            schedule.setTriggerWindow(windowType);
        }

        request.getSchedules().add(schedule);
    }

    @When("^receiving a set light schedule request by an unknown organization$")
    public void receivingASetLightScheduleRequestByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingASetLightScheduleRequest(requestParameters);
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set light schedule async response contains$")
    public void theSetLightScheduleAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {

        final SetScheduleAsyncResponse response = (SetScheduleAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the set light schedule response contains soap fault$")
    public void theSetLightScheduleResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    @Then("^the platform buffers a set light schedule response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetLightScheduleResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetScheduleAsyncRequest request = new SetScheduleAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                throw new TimeoutException();
            }

            count++;
            Thread.sleep(1000);

            final SetScheduleResponse response = this.client.getSetSchedule(request);

            if (getEnum(expectedResult, Keys.KEY_RESULT, OsgpResultType.class) != response.getResult()) {
                continue;
            }

            if (expectedResult.containsKey(Keys.KEY_DESCRIPTION)
                    && !getString(expectedResult, Keys.KEY_DESCRIPTION).equals(response.getDescription())) {
                continue;
            }

            success = true;
        }
    }

    @Then("^the platform buffers a set light schedule response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBuffersASetLightScheduleResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
        try {
            this.thePlatformBuffersASetLightScheduleResponseMessageForDevice(deviceIdentification, expectedResult);
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), ex.getMessage());
        }
    }
}