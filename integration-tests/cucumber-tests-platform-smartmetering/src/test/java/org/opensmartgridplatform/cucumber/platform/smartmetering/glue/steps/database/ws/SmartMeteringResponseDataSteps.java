/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws.ResponseDataBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class SmartMeteringResponseDataSteps extends BaseDeviceSteps {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SmartMeteringResponseDataSteps.class);

  @Autowired private ResponseDataRepository responseDataRepository;

  @Given("^a response data record$")
  @Transactional("txMgrRespData")
  public ResponseData aResponseDataRecord(final Map<String, String> settings) {

    final ResponseData responseData =
        this.responseDataRepository.save(new ResponseDataBuilder().fromSettings(settings).build());

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, responseData.getCorrelationUid());

    try {
      // set correct creation time for testing after inserting in the
      // database
      // (as it will be overridden on first save)
      if (settings.containsKey(PlatformKeys.KEY_CREATION_TIME)) {
        final Field fld = responseData.getClass().getSuperclass().getDeclaredField("creationTime");
        fld.setAccessible(true);
        fld.set(
            responseData,
            DateTimeHelper.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)).toDate());
        this.responseDataRepository.saveAndFlush(responseData);
      }
    } catch (final Exception e) {
      LOGGER.error("Exception", e);
      Assertions.fail("Failed to create response data record.");
    }

    return responseData;
  }

  @Then("^the response data record should be deleted$")
  public void theResponseDataRecordShouldBeDeleted() {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformSmartmeteringKeys.KEY_CORRELATION_UID);

    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should be deleted").isNull();
  }

  @Then("^the response data record should not be deleted$")
  public void theResponseDataRecordShouldNotBeDeleted() {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformSmartmeteringKeys.KEY_CORRELATION_UID);

    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should not be deleted").isNotNull();
  }

  @Then("^the response data record with correlation uid \\\"(.*)\\\" should be deleted$")
  public void theResponseDataRecordShouldBeDeleted(final String correlationUid) {
    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should be deleted").isNull();
  }

  @Then("^the response data record with correlation uid \\\"(.*)\\\" should not be deleted$")
  public void theResponseDataRecordShouldNotBeDeleted(final String correlationUid) {
    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should not be deleted").isNotNull();
  }

  @Then("^the response data has values$")
  public void theResponseDataHasValues(final Map<String, String> settings) {
    final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
    final short expectedNumberOfNotificationsSent =
        Short.parseShort(settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT));
    final String expectedMessageType = settings.get(PlatformKeys.KEY_MESSAGE_TYPE);

    RetryableAssert.assertWithRetries(
        () ->
            SmartMeteringResponseDataSteps.this.assertResponseDataHasNotificationsAndMessageType(
                correlationUid, expectedNumberOfNotificationsSent, expectedMessageType),
        3,
        200,
        TimeUnit.MILLISECONDS);
  }

  private void assertResponseDataHasNotificationsAndMessageType(
      final String correlationUid,
      final Short expectedNumberOfNotificationsSent,
      final String expectedMessageType) {

    final ResponseData responseData =
        this.responseDataRepository.findByCorrelationUid(correlationUid);

    assertThat(responseData.getNumberOfNotificationsSent())
        .as(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)
        .isEqualTo(expectedNumberOfNotificationsSent);

    assertThat(responseData.getMessageType())
        .as(PlatformKeys.KEY_MESSAGE_TYPE)
        .isEqualTo(expectedMessageType);
  }
}
