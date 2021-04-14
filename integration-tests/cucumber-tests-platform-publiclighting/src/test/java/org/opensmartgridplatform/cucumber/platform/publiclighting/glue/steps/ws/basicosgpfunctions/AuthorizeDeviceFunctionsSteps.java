/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.basicosgpfunctions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.TriggerType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingScheduleManagementClient;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingAdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingScheduleManagementClient;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the AuthorizeDeviceFunctions steps */
public class AuthorizeDeviceFunctionsSteps {

  @Autowired private PublicLightingAdHocManagementClient publicLightingAdHocManagementClient;

  @Autowired private PublicLightingScheduleManagementClient publicLightingScheduleManagementClient;

  @Autowired private TariffSwitchingAdHocManagementClient tariffSwitchingAdHocManagementClient;

  @Autowired
  private TariffSwitchingScheduleManagementClient tariffSwitchingScheduleManagementClient;

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctionsSteps.class);

  private DeviceFunction deviceFunction;
  private Throwable throwable;

  @When("receiving a publiclighting device function request")
  public void receivingAPublicLightingDeviceFunctionRequest(
      final Map<String, String> requestParameters)
      throws OperationNotSupportedException, WebServiceSecurityException, GeneralSecurityException,
          IOException {
    this.deviceFunction =
        getEnum(
            requestParameters, PlatformPubliclightingKeys.DEVICE_FUNCTION, DeviceFunction.class);

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
        case RESUME_SCHEDULE:
          this.resumeSchedule(requestParameters);
          break;
        case SET_TRANSITION:
          this.setTransition(requestParameters);
          break;
        default:
          throw new OperationNotSupportedException(
              "DeviceFunction " + this.deviceFunction + " does not exist.");
      }
    } catch (final Throwable t) {
      LOGGER.info("Exception: {}", t.getClass().getSimpleName());
      this.throwable = t;
    }
  }

  @Then("the publiclighting device function response is \"{}\"")
  public void thePublicLightingDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
    if (allowed) {
      Wait.until(
          () -> {
            Object response = null;
            try {
              response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
            } catch (final Exception ex) {
              // do nothing
            }
            assertThat(response).isNotNull();
            assertThat(response instanceof SoapFaultClientException).isFalse();
          });
    } else {
      assertThat(this.throwable).isNotNull();

      if (!this.throwable.getMessage().equals("METHOD_NOT_ALLOWED_FOR_OWNER")) {
        assertThat(this.throwable.getMessage()).isEqualTo("UNAUTHORIZED");
      }
    }
  }

  private void setLight(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final SetLightRequest request = new SetLightRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    final LightValue lightValue = new LightValue();
    lightValue.setIndex(0);
    lightValue.setDimValue(100);
    lightValue.setOn(true);
    request.getLightValue().add(lightValue);
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.publicLightingAdHocManagementClient.setLight(request));
  }

  private void setLightSchedule(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement
            .SetScheduleRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement
                .SetScheduleRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    final Schedule schedule = new Schedule();
    schedule.setActionTime(ActionTimeType.SUNRISE);
    schedule.setIndex(0);
    schedule.setWeekDay(WeekDayType.ALL);
    schedule.setTime(DateTime.now().toString());
    schedule.setIsEnabled(true);
    schedule.setMinimumLightsOn(10);
    final org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.LightValue
        lightValue =
            new org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement
                .LightValue();
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
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.publicLightingScheduleManagementClient.setSchedule(request));
  }

  private void setTariffSchedule(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement
            .SetScheduleRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement
                .SetScheduleRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    final TariffSchedule schedule = new TariffSchedule();
    final TariffValue tariffValue = new TariffValue();
    tariffValue.setHigh(true);
    tariffValue.setIndex(1);
    schedule.getTariffValue().add(tariffValue);
    schedule.setIndex(0);
    schedule.setWeekDay(
        org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType
            .ALL);
    schedule.setTime(DateTime.now().toString());
    schedule.setIsEnabled(true);
    schedule.setMinimumLightsOn(10);
    request.getSchedules().add(schedule);
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.tariffSwitchingScheduleManagementClient.setSchedule(request));
  }

  private void resumeSchedule(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final ResumeScheduleRequest request = new ResumeScheduleRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.publicLightingAdHocManagementClient.resumeSchedule(request));
  }

  private void setTransition(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException,
          DatatypeConfigurationException {
    final SetTransitionRequest request = new SetTransitionRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setTransitionType(TransitionType.DAY_NIGHT);

    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.publicLightingAdHocManagementClient.setTransition(request));
  }

  private void getTariffStatus(final Map<String, String> requestParameters)
      throws WebServiceSecurityException {
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                .GetStatusRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.tariffSwitchingAdHocManagementClient.getStatus(request));
  }

  private void getLightStatus(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final GetStatusRequest request = new GetStatusRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.publicLightingAdHocManagementClient.getStatus(request));
  }
}
