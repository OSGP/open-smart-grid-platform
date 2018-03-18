/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.glue.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.TimePeriod;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.TriggerType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingDeviceMonitoringClient;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingScheduleManagementClient;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingAdHocManagementClient;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingScheduleManagementClient;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class AuthorizeDeviceFunctionsSteps {

    @Autowired
    private PublicLightingDeviceMonitoringClient publicLightingDeviceMonitoringClient;

    @Autowired
    private PublicLightingAdHocManagementClient publicLightingAdHocManagementClient;

    @Autowired
    private PublicLightingScheduleManagementClient publicLightingScheduleManagementClient;

    @Autowired
    private TariffSwitchingAdHocManagementClient tariffSwitchingAdHocManagementClient;

    @Autowired
    private TariffSwitchingScheduleManagementClient tariffSwitchingScheduleManagementClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctionsSteps.class);

    private DeviceFunction deviceFunction;
    private Throwable throwable;

    @When("receiving a publiclighting device function request")
    public void receivingAPublicLightingDeviceFunctionRequest(final Map<String, String> requestParameters)
            throws OperationNotSupportedException, WebServiceSecurityException, GeneralSecurityException, IOException {
        this.deviceFunction = getEnum(requestParameters, PlatformPubliclightingKeys.DEVICE_FUNCTION,
                DeviceFunction.class);

        try {
            switch (this.deviceFunction) {
            case SET_LIGHT:
                this.setLight(requestParameters);
                break;
            case GET_LIGHT_STATUS:
                this.getLightStatus(requestParameters);
                break;
            case GET_TARIFF_STATUS:
                this.getTariffStatus(requestParameters);
                break;
            case SET_LIGHT_SCHEDULE:
                this.setLightSchedule(requestParameters);
                break;
            case SET_TARIFF_SCHEDULE:
                this.setTariffSchedule(requestParameters);
                break;
            case GET_POWER_USAGE_HISTORY:
                this.getPowerUsageHistory(requestParameters);
                break;
            case RESUME_SCHEDULE:
                this.resumeSchedule(requestParameters);
                break;
            case SET_TRANSITION:
                this.setTransition(requestParameters);
                break;
            default:
                throw new OperationNotSupportedException("DeviceFunction " + this.deviceFunction + " does not exist.");
            }
        } catch (final Throwable t) {
            LOGGER.info("Exception: {}", t.getClass().getSimpleName());
            this.throwable = t;
        }
    }

    @Then("the publiclighting device function response is \"([^\"]*)\"")
    public void thePublicLightingDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
        if (allowed) {
            Wait.until(() -> {
                Object response = null;
                try {
                    response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
                } catch (final Exception ex) {
                    // do nothing
                }
                Assert.assertNotNull(response);
                Assert.assertTrue(!(response instanceof SoapFaultClientException));
            });
        } else {
            Assert.assertNotNull(this.throwable);

            if (!this.throwable.getMessage().equals("METHOD_NOT_ALLOWED_FOR_OWNER")) {
                Assert.assertEquals("UNAUTHORIZED", this.throwable.getMessage());
            }
        }
    }

    private void setLight(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetLightRequest request = new SetLightRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        final LightValue lightValue = new LightValue();
        lightValue.setIndex(0);
        lightValue.setDimValue(100);
        lightValue.setOn(true);
        request.getLightValue().add(lightValue);
        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingAdHocManagementClient.setLight(request));
    }

    private void setLightSchedule(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest request = new com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        final Schedule schedule = new Schedule();
        schedule.setActionTime(ActionTimeType.SUNRISE);
        schedule.setIndex(0);
        schedule.setWeekDay(WeekDayType.ALL);
        schedule.setTime(DateTime.now().toString());
        schedule.setIsEnabled(true);
        schedule.setMinimumLightsOn(10);
        final com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue lightValue = new com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue();
        lightValue.setDimValue(100);
        lightValue.setIndex(1);
        lightValue.setOn(true);
        schedule.getLightValue().add(lightValue);
        schedule.setTriggerType(TriggerType.LIGHT_TRIGGER);
        final WindowType windowType = new WindowType();
        windowType.setMinutesAfter(0);
        windowType.setMinutesBefore(0);
        schedule.setTriggerWindow(windowType);
        request.getSchedules().add(schedule);
        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingScheduleManagementClient.setSchedule(request));
    }

    private void setTariffSchedule(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest request = new com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final TariffSchedule schedule = new TariffSchedule();
        final TariffValue tariffValue = new TariffValue();
        tariffValue.setHigh(true);
        tariffValue.setIndex(1);
        schedule.getTariffValue().add(tariffValue);
        schedule.setIndex(0);
        schedule.setWeekDay(com.alliander.osgp.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType.ALL);
        schedule.setTime(DateTime.now().toString());
        schedule.setIsEnabled(true);
        schedule.setMinimumLightsOn(10);
        request.getSchedules().add(schedule);
        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.tariffSwitchingScheduleManagementClient.setSchedule(request));
    }

    private void getPowerUsageHistory(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException, DatatypeConfigurationException {
        final GetPowerUsageHistoryRequest request = new GetPowerUsageHistoryRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        final TimePeriod timePeriod = new TimePeriod();
        timePeriod.setEndTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTime.now().toGregorianCalendar()));
        timePeriod.setStartTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTime.now().toGregorianCalendar()));
        request.setTimePeriod(timePeriod);
        request.setHistoryTermType(HistoryTermType.LONG);

        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingDeviceMonitoringClient.getPowerUsageHistory(request));
    }

    private void resumeSchedule(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final ResumeScheduleRequest request = new ResumeScheduleRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingAdHocManagementClient.resumeSchedule(request));
    }

    private void setTransition(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException, DatatypeConfigurationException {
        final SetTransitionRequest request = new SetTransitionRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setTransitionType(TransitionType.DAY_NIGHT);

        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingAdHocManagementClient.setTransition(request));
    }

    private void getTariffStatus(final Map<String, String> requestParameters) throws WebServiceSecurityException {
        final com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest request = new com.alliander.osgp.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.tariffSwitchingAdHocManagementClient.getStatus(request));
    }

    private void getLightStatus(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetStatusRequest request = new GetStatusRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                this.publicLightingAdHocManagementClient.getStatus(request));
    }
}