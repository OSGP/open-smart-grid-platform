// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.ws.microgrids.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.assertj.core.data.Offset;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataSystemIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Measurement;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.Profile;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.AdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement.GetDataRequestBuilder;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;

public class GetDataSteps {

  /**
   * Delta value for which two measurement values are considered equal if their difference does not
   * exceed it.
   */
  private static final Offset<Double> DELTA_FOR_MEASUREMENT_VALUE = Offset.offset(0.0001);

  private static final SimpleDateFormat TIME_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  @Autowired private AdHocManagementClient client;

  @When("^a get data request is received$")
  public void aGetDataRequestIsReceived(final Map<String, String> requestParameters)
      throws Throwable {

    final GetDataRequest getDataRequest = GetDataRequestBuilder.fromParameterMap(requestParameters);
    final GetDataAsyncResponse response = this.client.getDataAsync(getDataRequest);

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, response.getAsyncResponse().getCorrelationUid());
    ScenarioContext.current()
        .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, response.getAsyncResponse().getDeviceId());
  }

  @Then("^the get data response should be returned$")
  public void theGetDataResponseShouldBeReturned(final Map<String, String> responseParameters)
      throws Throwable {

    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final GetDataAsyncRequest getDataAsyncRequest =
        GetDataRequestBuilder.fromParameterMapAsync(extendedParameters);
    final GetDataResponse response = this.client.getData(getDataAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);

    if (!responseParameters.containsKey(PlatformKeys.KEY_NUMBER_OF_SYSTEMS)) {
      throw new AssertionError(
          "The Step DataTable must contain the expected number of systems with key \""
              + PlatformKeys.KEY_NUMBER_OF_SYSTEMS
              + "\" when confirming a returned get data response.");
    }
    final int expectedNumberOfSystems =
        Integer.parseInt(responseParameters.get(PlatformKeys.KEY_NUMBER_OF_SYSTEMS));

    final List<GetDataSystemIdentifier> systemIdentifiers = response.getSystem();
    assertThat(systemIdentifiers.size()).as("Number of Systems").isEqualTo(expectedNumberOfSystems);

    for (int i = 0; i < expectedNumberOfSystems; i++) {
      this.assertSystemResponse(responseParameters, systemIdentifiers, i);
    }
  }

  private void assertSystemResponse(
      final Map<String, String> responseParameters,
      final List<GetDataSystemIdentifier> systemIdentifiers,
      final int systemIndex) {

    final int numberOfSystems = systemIdentifiers.size();
    final String indexPostfix = "_" + (systemIndex + 1);
    final String systemDescription = "System[" + (systemIndex + 1) + "/" + numberOfSystems + "]";

    final GetDataSystemIdentifier systemIdentifier = systemIdentifiers.get(systemIndex);

    if (responseParameters.containsKey(PlatformKeys.KEY_SYSTEM_TYPE.concat(indexPostfix))) {
      final String expectedType =
          responseParameters.get(PlatformKeys.KEY_SYSTEM_TYPE.concat(indexPostfix));

      try {
        assertThat(systemIdentifier.getType())
            .as(systemDescription + " type")
            .isEqualTo(expectedType);
      } catch (final AssertionFailedError e) {
        // Work around for OSGP's restore connection scheduled task. If
        // the type is not as expected it can be equal to RTU.
        assertThat(systemIdentifier.getType()).as(systemDescription + " type").isEqualTo("RTU");
      }
    }

    if (responseParameters.containsKey(
        PlatformKeys.KEY_NUMBER_OF_MEASUREMENTS.concat(indexPostfix))) {
      this.assertMeasurements(
          responseParameters,
          systemIdentifier.getMeasurement(),
          numberOfSystems,
          systemIndex,
          systemDescription,
          indexPostfix);
    }

    if (responseParameters.containsKey(PlatformKeys.KEY_NUMBER_OF_PROFILES.concat(indexPostfix))) {
      this.assertProfiles(
          responseParameters,
          systemIdentifier.getProfile(),
          numberOfSystems,
          systemIndex,
          systemDescription,
          indexPostfix);
    }
  }

  private void assertMeasurements(
      final Map<String, String> responseParameters,
      final List<Measurement> measurements,
      final int numberOfSystems,
      final int systemIndex,
      final String systemDescription,
      final String indexPostfix) {

    final int expectedNumberOfMeasurements =
        Integer.parseInt(
            responseParameters.get(PlatformKeys.KEY_NUMBER_OF_MEASUREMENTS.concat(indexPostfix)));
    assertThat(measurements.size())
        .as(systemDescription + " number of Measurements")
        .isEqualTo(expectedNumberOfMeasurements);
    for (int i = 0; i < expectedNumberOfMeasurements; i++) {
      this.assertMeasurementResponse(
          responseParameters, numberOfSystems, systemIndex, systemDescription, measurements, i);
    }
  }

  private void assertMeasurementResponse(
      final Map<String, String> responseParameters,
      final int numberOfSystems,
      final int systemIndex,
      final String systemDescription,
      final List<Measurement> measurements,
      final int measurementIndex) {

    final int numberOfMeasurements = measurements.size();
    final String indexPostfix = "_" + (systemIndex + 1) + "_" + (measurementIndex + 1);
    final String measurementDescription =
        systemDescription
            + " Measurement["
            + (measurementIndex + 1)
            + "/"
            + numberOfMeasurements
            + "]";

    final Measurement measurement = measurements.get(measurementIndex);

    final Integer expectedId =
        Integer.valueOf(
            responseParameters.get(PlatformKeys.KEY_MEASUREMENT_ID.concat(indexPostfix)));
    assertThat(measurement.getId()).as(measurementDescription + " id").isEqualTo(expectedId);

    final String expectedNode =
        responseParameters.get(PlatformKeys.KEY_MEASUREMENT_NODE.concat(indexPostfix));
    assertThat(measurement.getNode()).as(measurementDescription + " node").isEqualTo(expectedNode);

    if (responseParameters.containsKey(PlatformKeys.KEY_MEASUREMENT_QUALIFIER)) {
      final int expectedQualifier =
          Integer.parseInt(
              responseParameters.get(PlatformKeys.KEY_MEASUREMENT_QUALIFIER.concat(indexPostfix)));
      assertThat(measurement.getQualifier())
          .as(measurementDescription + " Qualifier")
          .isEqualTo(expectedQualifier);
    }

    assertThat(measurement.getTime()).as(measurementDescription + " Time").isNotNull();
    assertTimeFormat(measurement.getTime());

    if (responseParameters.containsKey(PlatformKeys.KEY_MEASUREMENT_VALUE)) {
      final double expectedValue =
          Double.parseDouble(
              responseParameters.get(PlatformKeys.KEY_MEASUREMENT_VALUE.concat(indexPostfix)));
      assertThat(measurement.getValue())
          .as(measurementDescription + " Value")
          .isCloseTo(expectedValue, DELTA_FOR_MEASUREMENT_VALUE);
    }
  }

  private void assertProfiles(
      final Map<String, String> responseParameters,
      final List<Profile> profiles,
      final int numberOfSystems,
      final int systemIndex,
      final String systemDescription,
      final String indexPostfix) {

    final int expectedNumberOfProfiles =
        Integer.parseInt(
            responseParameters.get(PlatformKeys.KEY_NUMBER_OF_PROFILES.concat(indexPostfix)));
    assertThat(profiles.size())
        .as(systemDescription + " number of Profiles")
        .isEqualTo(expectedNumberOfProfiles);
    for (int i = 0; i < expectedNumberOfProfiles; i++) {
      this.assertProfileResponse(
          responseParameters, numberOfSystems, systemIndex, systemDescription, profiles, i);
    }
  }

  private void assertProfileResponse(
      final Map<String, String> responseParameters,
      final int numberOfSystems,
      final int systemIndex,
      final String systemDescription,
      final List<Profile> profiles,
      final int profileIndex) {
    throw new PendingException();
  }

  public static void assertTimeFormat(final XMLGregorianCalendar value) {
    try {
      TIME_FORMAT.parse(value.toString());
    } catch (final ParseException ex) {
      throw new AssertionError("Incorrect date/time format: " + value);
    }
  }
}
