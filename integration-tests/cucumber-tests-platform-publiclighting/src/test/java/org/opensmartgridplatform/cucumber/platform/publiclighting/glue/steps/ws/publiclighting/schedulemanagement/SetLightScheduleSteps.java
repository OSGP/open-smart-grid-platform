// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.schedulemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.SetScheduleResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.TriggerType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingScheduleManagementClient;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set light requests steps */
public class SetLightScheduleSteps {

  @Autowired private PublicLightingScheduleManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetLightScheduleSteps.class);

  /**
   * Sends a Set Schedule request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set light schedule request$")
  public void receivingASetLightScheduleRequest(final Map<String, String> requestParameters)
      throws Throwable {

    this.callAddSchedule(requestParameters, 1);
  }

  /**
   * Sends a Set Schedule request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set light schedule request with astronomical offsets$")
  public void receivingASetLightScheduleRequestWithAstronomicalOffsets(
      final Map<String, String> requestParameters) throws Throwable {
    final SetScheduleRequest request = new SetScheduleRequest();

    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setAstronomicalSunriseOffset(
        getShort(
            requestParameters,
            PlatformPubliclightingKeys.KEY_ASTRONOMICAL_SUNRISE_OFFSET,
            PlatformPubliclightingDefaults.DEFAULT_ASTRONOMICAL_SUNRISE_OFFSET));
    request.setAstronomicalSunsetOffset(
        getShort(
            requestParameters,
            PlatformPubliclightingKeys.KEY_ASTRONOMICAL_SUNSET_OFFSET,
            PlatformPubliclightingDefaults.DEFAULT_ASTRONOMICAL_SUNSET_OFFSET));

    this.addScheduleForRequest(
        request,
        WeekDayType.ALL,
        null,
        null,
        ActionTimeType.SUNRISE,
        null,
        "0,false",
        TriggerType.ASTRONOMICAL.name(),
        null);

    try {
      ScenarioContext.current()
          .put(PlatformPubliclightingKeys.RESPONSE, this.client.setSchedule(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
    }
  }

  /**
   * Sends a Set Schedule request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set light schedule request for (\\d+) schedules?$")
  public void receivingASetLightScheduleRequestForSchedules(
      final Integer countSchedules, final Map<String, String> requestParameters) throws Throwable {

    this.callAddSchedule(requestParameters, countSchedules);
  }

  private void callAddSchedule(
      final Map<String, String> requestParameters, final Integer countSchedules) throws Throwable {

    final SetScheduleRequest request = new SetScheduleRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    if (requestParameters.containsKey(PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME)) {
      if (requestParameters.get(PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME).isEmpty()) {
        request.setScheduledTime(
            DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                    GregorianCalendar.from(ZonedDateTime.now(ZoneId.of("UTC")))));
      } else {
        request.setScheduledTime(
            DatatypeFactory.newDefaultInstance()
                .newXMLGregorianCalendar(
                    GregorianCalendar.from(
                        getDate(
                                requestParameters,
                                PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME)
                            .withZoneSameInstant(ZoneId.of("UTC")))));
      }
    }

    for (int i = 0; i < countSchedules; i++) {
      this.addScheduleForRequest(
          request,
          getEnum(
              requestParameters, PlatformPubliclightingKeys.SCHEDULE_WEEKDAY, WeekDayType.class),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_STARTDAY),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_ENDDAY),
          getEnum(
              requestParameters,
              PlatformPubliclightingKeys.SCHEDULE_ACTIONTIME,
              ActionTimeType.class),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TIME),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_LIGHTVALUES),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TRIGGERTYPE),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TRIGGERWINDOW));
    }
    try {
      ScenarioContext.current()
          .put(PlatformPubliclightingKeys.RESPONSE, this.client.setSchedule(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
    }
  }

  private void addScheduleForRequest(
      final SetScheduleRequest request,
      final WeekDayType weekDay,
      final String startDay,
      final String endDay,
      final ActionTimeType actionTime,
      final String time,
      final String scheduleLightValue,
      final String triggerType,
      final String triggerWindow)
      throws DatatypeConfigurationException {
    final Schedule schedule = new Schedule();
    schedule.setWeekDay(weekDay);
    if (StringUtils.isNotBlank(startDay)) {
      schedule.setStartDay(
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(
                  GregorianCalendar.from(
                      JavaTimeHelpers.parseToZonedDateTime(startDay)
                          .withZoneSameInstant(ZoneId.of("UTC")))));
    }
    if (StringUtils.isNotBlank(endDay)) {
      schedule.setEndDay(
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(
                  GregorianCalendar.from(
                      JavaTimeHelpers.parseToZonedDateTime(endDay)
                          .withZoneSameInstant(ZoneId.of("UTC")))));
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

    if (StringUtils.isNotBlank(triggerWindow)) {
      final String[] windowTypeValues = triggerWindow.split(",");
      if (windowTypeValues.length == 2) {
        final WindowType windowType = new WindowType();
        windowType.setMinutesBefore(Integer.parseInt(windowTypeValues[0]));
        windowType.setMinutesAfter(Integer.parseInt(windowTypeValues[1]));

        schedule.setTriggerWindow(windowType);
      }
    }

    request.getSchedules().add(schedule);
  }

  @When("^receiving a set light schedule request by an unknown organization$")
  public void receivingASetLightScheduleRequestByAnUnknownOrganization(
      final Map<String, String> requestParameters) throws Throwable {
    // Force the request being send to the platform as a given organization.
    ScenarioContext.current()
        .put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingASetLightScheduleRequest(requestParameters);
  }

  /**
   * The check for the response from the Platform.
   *
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   * @throws Throwable
   */
  @Then("^the set light schedule async response contains$")
  public void theSetLightScheduleAsyncResponseContains(
      final Map<String, String> expectedResponseData) throws Throwable {

    final SetScheduleAsyncResponse asyncResponse =
        (SetScheduleAsyncResponse)
            ScenarioContext.current().get(PlatformPubliclightingKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(
            getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION));

    // Save the returned CorrelationUid in the Scenario related context for
    // further use.
    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    LOGGER.info(
        "Got CorrelationUid: ["
            + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID)
            + "]");
  }

  @Then("^the set light schedule response contains soap fault$")
  public void theSetLightScheduleResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }

  @Then("^the platform buffers a set light schedule response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersASetLightScheduleResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws Throwable {
    final SetScheduleAsyncRequest request = new SetScheduleAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    final SetScheduleResponse response =
        Wait.untilAndReturn(
            () -> {
              try {
                return this.client.getSetSchedule(request);
              } catch (final SoapFaultClientException ex) {
                LOGGER.info("Received a SOAP fault on setSchedule");
                if ("CorrelationUid is unknown.".equals(ex.getFaultStringOrReason())) {
                  throw new Exception(
                      "Received a SOAP fault on setSchedule that could be rejected because the CorrelationUid is unknown");
                }
                return null;
              }
            });

    assertThat(response).isNotNull();
    assertThat(response.getResult())
        .isEqualTo(getEnum(expectedResult, PlatformKeys.KEY_RESULT, OsgpResultType.class));

    if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_DESCRIPTION)) {
      assertThat(response.getDescription())
          .isEqualTo(
              getString(
                  expectedResult,
                  PlatformPubliclightingKeys.KEY_DESCRIPTION,
                  PlatformPubliclightingDefaults.DEFAULT_PUBLICLIGHTING_DESCRIPTION));
    }
  }

  @Then(
      "^the platform buffers a set light schedule response message for device \"([^\"]*)\" that contains a soap fault$")
  public void thePlatformBuffersASetLightScheduleResponseMessageForDeviceContainsSoapFault(
      final String deviceIdentification, final Map<String, String> expectedResponseData)
      throws Throwable {
    final SetScheduleAsyncRequest request = new SetScheduleAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.untilAndReturn(
        () -> {
          try {
            this.client.getSetSchedule(request);
          } catch (final SoapFaultClientException ex) {
            LOGGER.info("Received a SOAP fault on setSchedule");
            final String faultString = ex.getFaultStringOrReason();
            if ("CorrelationUid is unknown.".equals(faultString)) {
              throw new Exception(
                  "Received a SOAP fault on setSchedule that could be rejected because the CorrelationUid is unknown");
            }
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
            return null;
          }
          throw new Exception("Received a setSchedule message without a SOAP fault");
        });

    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }
}
