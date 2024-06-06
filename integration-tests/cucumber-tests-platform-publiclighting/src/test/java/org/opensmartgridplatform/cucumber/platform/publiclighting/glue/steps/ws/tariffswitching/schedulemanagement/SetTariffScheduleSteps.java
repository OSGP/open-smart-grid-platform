// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.tariffswitching.schedulemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
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
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.Page;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.SetScheduleResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.TariffSchedule;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.TariffValue;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.schedulemanagement.WeekDayType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingScheduleManagementClient;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set requests steps */
public class SetTariffScheduleSteps {

  @Autowired private TariffSwitchingScheduleManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetTariffScheduleSteps.class);

  /**
   * Sends a Set Tariff Schedule request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set tariff schedule request$")
  public void receivingASetTariffScheduleRequest(final Map<String, String> requestParameters)
      throws Throwable {

    this.callAddSchedule(requestParameters, 1);
  }

  /**
   * Sends a Set Tariff Schedule request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set tariff schedule request for (\\d+) schedules?$")
  public void receivingASetTariffScheduleRequestForSchedules(
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
      request.setScheduledTime(
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(
                  GregorianCalendar.from(
                      (requestParameters
                              .get(PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME)
                              .isEmpty())
                          ? ZonedDateTime.now(ZoneId.of("UTC"))
                          : getDate(
                                  requestParameters,
                                  PlatformPubliclightingKeys.SCHEDULE_SCHEDULEDTIME)
                              .withZoneSameInstant(ZoneId.of("UTC")))));
    }

    for (int i = 0; i < countSchedules; i++) {
      this.addScheduleForRequest(
          request,
          getEnum(
              requestParameters, PlatformPubliclightingKeys.SCHEDULE_WEEKDAY, WeekDayType.class),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_STARTDAY),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_ENDDAY),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TIME),
          getString(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TARIFFVALUES));
    }

    if (requestParameters.containsKey(PlatformPubliclightingKeys.SCHEDULE_CURRENTPAGE)
        && requestParameters.containsKey(PlatformPubliclightingKeys.SCHEDULE_PAGESIZE)
        && requestParameters.containsKey(PlatformPubliclightingKeys.SCHEDULE_TOTALPAGES)) {
      final Page page = new Page();
      page.setCurrentPage(
          getInteger(requestParameters, PlatformPubliclightingKeys.SCHEDULE_CURRENTPAGE));
      page.setPageSize(getInteger(requestParameters, PlatformPubliclightingKeys.SCHEDULE_PAGESIZE));
      page.setTotalPages(
          getInteger(requestParameters, PlatformPubliclightingKeys.SCHEDULE_TOTALPAGES));
      request.setPage(page);
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
      final String time,
      final String scheduleTariffValue)
      throws DatatypeConfigurationException {
    final TariffSchedule schedule = new TariffSchedule();
    schedule.setWeekDay(weekDay);
    if (!startDay.isEmpty()) {
      schedule.setStartDay(
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(
                  GregorianCalendar.from(
                      JavaTimeHelpers.parseToZonedDateTime(startDay)
                          .withZoneSameInstant(ZoneId.of("UTC")))));
    }
    if (!endDay.isEmpty()) {
      schedule.setEndDay(
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(
                  GregorianCalendar.from(
                      JavaTimeHelpers.parseToZonedDateTime(endDay)
                          .withZoneSameInstant(ZoneId.of("UTC")))));
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
  public void receivingASetTariffScheduleRequestByAnUnknownOrganization(
      final Map<String, String> requestParameters) throws Throwable {
    // Force the request being send to the platform as a given organization.
    ScenarioContext.current()
        .put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingASetTariffScheduleRequest(requestParameters);
  }

  /**
   * The check for the response from the Platform.
   *
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   * @throws Throwable
   */
  @Then("^the set tariff schedule async response contains$")
  public void theSetTariffScheduleAsyncResponseContains(
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

  @Then("^the set tariff schedule response contains soap fault$")
  public void theSetTariffScheduleResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }

  @Then("^the platform buffers a set tariff schedule response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersASetTariffScheduleResponseMessageForDevice(
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
              final SetScheduleResponse retval = this.client.getSetSchedule(request);
              assertThat(retval).isNotNull();
              assertThat(retval.getResult())
                  .isEqualTo(
                      getEnum(expectedResult, PlatformKeys.KEY_RESULT, OsgpResultType.class));

              return retval;
            });

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
      "^the platform buffers a set tariff schedule response message for device \"([^\"]*)\" that contains a soap fault$")
  public void thePlatformBuffersASetTariffScheduleResponseMessageForDeviceContainsSoapFault(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws Throwable {
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
      GenericResponseSteps.verifySoapFault(expectedResult);
    }
  }
}
