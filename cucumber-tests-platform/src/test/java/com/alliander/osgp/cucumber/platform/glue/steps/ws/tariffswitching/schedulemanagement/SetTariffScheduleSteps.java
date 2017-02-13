/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.tariffswitching.schedulemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getDate;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

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

import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.common.Page;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.tariffswitching.TariffSwitchingScheduleManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set requests steps
 */
public class SetTariffScheduleSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private TariffSwitchingScheduleManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetTariffScheduleSteps.class);

    /**
     * Sends a Set Tariff Schedule request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set tariff schedule request$")
    public void receivingASetTariffScheduleRequest(final Map<String, String> requestParameters) throws Throwable {

        this.callAddSchedule(requestParameters, 1);
    }

    /**
     * Sends a Set Tariff Schedule request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set tariff schedule request for (\\d+) schedules?$")
    public void receivingASetTariffScheduleRequestForSchedules(final Integer countSchedules,
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
                    getString(requestParameters, Keys.SCHEDULE_TIME),
                    getString(requestParameters, Keys.SCHEDULE_TARIFFVALUES));
        }

        if (requestParameters.containsKey(Keys.SCHEDULE_CURRENTPAGE)
                && requestParameters.containsKey(Keys.SCHEDULE_PAGESIZE)
                && requestParameters.containsKey(Keys.SCHEDULE_TOTALPAGES)) {
            final Page page = new Page();
            page.setCurrentPage(getInteger(requestParameters, Keys.SCHEDULE_CURRENTPAGE));
            page.setPageSize(getInteger(requestParameters, Keys.SCHEDULE_PAGESIZE));
            page.setTotalPages(getInteger(requestParameters, Keys.SCHEDULE_TOTALPAGES));
            request.setPage(page);
        }

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setSchedule(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    private void addScheduleForRequest(final SetScheduleRequest request, final WeekDayType weekDay,
            final String startDay, final String endDay, final String time, final String scheduleTariffValue)
            throws DatatypeConfigurationException {
        final TariffSchedule schedule = new TariffSchedule();
        schedule.setWeekDay(weekDay);
        if (!startDay.isEmpty()) {
            schedule.setStartDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTime.parse(startDay).toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }
        if (!endDay.isEmpty()) {
            schedule.setEndDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    DateTime.parse(endDay).toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        }
        schedule.setTime(time);

        for (final String tariffValue : scheduleTariffValue.split(";")) {
            final TariffValue lv = new TariffValue();
            final String[] tariffValues = tariffValue.split(",");
            lv.setIndex(Integer.parseInt(tariffValues[0]));
            lv.setHigh(Boolean.parseBoolean(tariffValues[1]));

            schedule.getTariffValue().add(lv);
        }

        request.getSchedules().add(schedule);
    }

    @When("^receiving a set tariff schedule request by an unknown organization$")
    public void receivingASetTariffScheduleRequestByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingASetTariffScheduleRequest(requestParameters);
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
    @Then("^the set tariff schedule async response contains$")
    public void theSetTariffScheduleAsyncResponseContains(final Map<String, String> expectedResponseData)
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

    @Then("^the set tariff schedule response contains soap fault$")
    public void theSetTariffScheduleResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    @Then("^the platform buffers a set tariff schedule response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetTariffScheduleResponseMessageForDevice(final String deviceIdentification,
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

    @Then("^the platform buffers a set tariff schedule response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBuffersASetTariffScheduleResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
        try {
            this.thePlatformBuffersASetTariffScheduleResponseMessageForDevice(deviceIdentification, expectedResult);
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), ex.getMessage());
        }
    }
}