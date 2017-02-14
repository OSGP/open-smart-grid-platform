/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.tariffswitching.schedulemanagement;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set requests steps
 */
public class SetReverseTariffScheduleSteps {

    @Autowired
    private SetTariffScheduleSteps setTariffScheduleSteps;

    /**
     * Sends a Set Reverse Tariff Schedule request to the platform for a given
     * device identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set reverse tariff schedule request$")
    public void receivingASetReverseTariffScheduleRequest(final Map<String, String> requestParameters)
            throws Throwable {

        this.setTariffScheduleSteps.receivingASetTariffScheduleRequest(requestParameters);
    }

    @When("^receiving a set reverse tariff schedule request by an unknown organization$")
    public void receivingASetReverseTariffScheduleRequestByAnUnknownOrganization(
            final Map<String, String> requestParameters) throws Throwable {

        this.setTariffScheduleSteps.receivingASetTariffScheduleRequestByAnUnknownOrganization(requestParameters);
    }

    /**
     * Sends a Set Reverse Tariff Schedule request to the platform for a given
     * device identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    /**
     *
     * @param countSchedules
     *            The amount of schedules in this request.
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set reverse tariff schedule request for (\\d+) schedules?$")
    public void receivingASetReverseTariffScheduleRequestForSchedules(final Integer countSchedules,
            final Map<String, String> requestParameters) throws Throwable {

        this.setTariffScheduleSteps.receivingASetTariffScheduleRequestForSchedules(countSchedules, requestParameters);
    }

    @Then("^the platform buffers a set reverse tariff schedule response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetReverseTariffScheduleResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        this.setTariffScheduleSteps.thePlatformBuffersASetTariffScheduleResponseMessageForDevice(deviceIdentification,
                expectedResult);
    }

    @Then("^the platform buffers a set reverse tariff schedule response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBuffersASetReverseTariffScheduleResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
        this.setTariffScheduleSteps.thePlatformBuffersASetTariffScheduleResponseMessageForDeviceContainsSoapFault(
                deviceIdentification, expectedResult);
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
    @Then("^the set reverse tariff schedule async response contains$")
    public void theSetReverseTariffScheduleAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
        this.setTariffScheduleSteps.theSetTariffScheduleAsyncResponseContains(expectedResponseData);
    }

    @Then("^the set reverse tariff schedule response contains soap fault$")
    public void theSetReverseTariffScheduleResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        this.setTariffScheduleSteps.theSetTariffScheduleResponseContainsSoapFault(expectedResponseData);
    }
}