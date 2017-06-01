/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.schedulemanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

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
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingScheduleManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class SetLightScheduleSteps {

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
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        if (requestParameters.containsKey(PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME)) {
            request.setScheduledTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    ((requestParameters.get(PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME).isEmpty())
                            ? DateTime.now()
                            : getDate(requestParameters, PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME))
                                    .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }

        for (int i = 0; i < countSchedules; i++) {
            this.addScheduleForRequest(request,
                    getEnum(requestParameters, PlatformPubliclightingKeys.SCHEDULE_WEEKDAY, WeekDayType.class),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_STARTDAY),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_ENDDAY),
                    getEnum(requestParameters, PlatformPubliclightingKeys.SCHEDULE_ACTIONTIME, ActionTimeType.class),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TIME),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_LIGHTVALUES),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TRIGGERTYPE),
                    getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TRIGGERWINDOW));
        }
        try {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, this.client.setSchedule(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
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
        ScenarioContext.current().put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                "unknown-organization");

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

        final SetScheduleAsyncResponse asyncResponse = (SetScheduleAsyncResponse) ScenarioContext.current()
                .get(PlatformPubliclightingKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: ["
                + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID) + "]");
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
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final SetScheduleResponse response = Wait.untilAndReturn(() -> {
            final SetScheduleResponse retval = this.client.getSetSchedule(request);
            Assert.assertNotNull(retval);
            Assert.assertEquals(getEnum(expectedResult, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                    retval.getResult());
            return retval;
        });

        if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_DESCRIPTION)) {
            Assert.assertEquals(
                    getString(expectedResult, PlatformPubliclightingKeys.KEY_DESCRIPTION,
                            PlatformPubliclightingDefaults.DEFAULT_PUBLICLIGHTING_DESCRIPTION),
                    response.getDescription());
        }
    }

    @Then("^the platform buffers a set light schedule response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBuffersASetLightScheduleResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResponseData) throws Throwable {
        final SetScheduleAsyncRequest request = new SetScheduleAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        try {
            this.client.getSetSchedule(request);
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
            GenericResponseSteps.verifySoapFault(expectedResponseData);
        }
    }
}