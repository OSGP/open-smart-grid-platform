// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws.ResponseDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class WsPublicLightingResponseDataSteps {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WsPublicLightingResponseDataSteps.class);

  @Autowired private WsPublicLightingResponseDataRepository responseDataRepository;

  @Given("^a public lighting response data record$")
  @Transactional("txMgrWsPublicLighting")
  public ResponseData aPublicLightingResponseDataRecord(final Map<String, String> settings) {
    return this.createResponseDataRecord(settings, this.responseDataRepository);
  }

  private ResponseData createResponseDataRecord(
      final Map<String, String> settings, final ResponseDataRepository responseDataRepository) {
    ResponseData responseData = new ResponseDataBuilder().fromSettings(settings).build();
    responseData = responseDataRepository.save(responseData);
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
            Date.from(
                Objects.requireNonNull(
                        DateTimeHelper.getDateTime(settings.get(PlatformKeys.KEY_CREATION_TIME)))
                    .toInstant()));
        responseDataRepository.saveAndFlush(responseData);
      }
    } catch (final Exception e) {
      LOGGER.error("Exception", e);
      Assertions.fail("Failed to create response data record.");
    }

    return responseData;
  }

  @Then(
      "^the public lighting response data record with correlation uid \\\"(.*)\\\" should be deleted$")
  @Transactional("txMgrWsPublicLighting")
  public void thePublicLightingResponseDataRecordShouldBeDeleted(final String correlationUid) {
    this.theResponseDataRecordShouldBeDeleted(correlationUid, this.responseDataRepository);
  }

  public void theResponseDataRecordShouldBeDeleted(
      final String correlationUid, final ResponseDataRepository responseDataRespository) {
    final ResponseData responseData = responseDataRespository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should be deleted").isNull();
  }

  @Then(
      "^the public lighting response data record with correlation uid \\\"(.*)\\\" should not be deleted$")
  @Transactional("txMgrWsPublicLighting")
  public void thePublicLightingResponseDataRecordShouldNotBeDeleted(final String correlationUid) {
    this.theResponseDataRecordShouldNotBeDeleted(correlationUid, this.responseDataRepository);
  }

  public void theResponseDataRecordShouldNotBeDeleted(
      final String correlationUid, final ResponseDataRepository responseDataRespository) {
    final ResponseData responseData = responseDataRespository.findByCorrelationUid(correlationUid);

    assertThat(responseData).as("Response data should not be deleted").isNotNull();
  }

  @Then("^the public lighting response data has values$")
  @Transactional("txMgrWsPublicLighting")
  public void thePublicLightingResponseDataHasValues(final Map<String, String> settings) {
    this.theResponseDataHasValues(settings, this.responseDataRepository);
  }

  private void theResponseDataHasValues(
      final Map<String, String> settings, final ResponseDataRepository responseDataRespository) {
    final String correlationUid = settings.get(PlatformKeys.KEY_CORRELATION_UID);
    final short expectedNumberOfNotificationsSent =
        Short.parseShort(settings.get(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT));
    final String expectedMessageType = settings.get(PlatformKeys.KEY_MESSAGE_TYPE);

    RetryableAssert.assertWithRetries(
        () ->
            WsPublicLightingResponseDataSteps.this.assertResponseDataHasNotificationsAndMessageType(
                correlationUid,
                expectedNumberOfNotificationsSent,
                expectedMessageType,
                responseDataRespository),
        3,
        200,
        TimeUnit.MILLISECONDS);
  }

  private void assertResponseDataHasNotificationsAndMessageType(
      final String correlationUid,
      final Short expectedNumberOfNotificationsSent,
      final String expectedMessageType,
      final ResponseDataRepository responseDataRespository) {

    final ResponseData responseData = responseDataRespository.findByCorrelationUid(correlationUid);

    assertThat(responseData.getNumberOfNotificationsSent())
        .as(PlatformKeys.KEY_NUMBER_OF_NOTIFICATIONS_SENT)
        .isEqualTo(expectedNumberOfNotificationsSent);
    assertThat(responseData.getMessageType())
        .as(PlatformKeys.KEY_MESSAGE_TYPE)
        .isEqualTo(expectedMessageType);
  }
}
