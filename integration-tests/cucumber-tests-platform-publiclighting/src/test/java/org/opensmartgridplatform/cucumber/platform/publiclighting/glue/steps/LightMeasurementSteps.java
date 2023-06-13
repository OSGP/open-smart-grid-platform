// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.database.core.EventSpecifications;
import org.opensmartgridplatform.cucumber.platform.database.logging.DeviceLogItemSpecifications;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class LightMeasurementSteps {

  private static final String GI_TERMINATION_MESSAGE =
      "ASDU Type: 100, C_IC_NA_1, "
          + "Interrogation command\nCause of transmission: ACTIVATION_TERMINATION";

  private static final String EVENT_MESSAGE =
      "ASDU Type: 30, M_SP_TB_1, "
          + "Single-point information with time tag CP56Time2a\nCause of transmission: SPONTANEOUS";

  private static final String KEY_DEVICE_IDENTIFICATION = PlatformKeys.KEY_DEVICE_IDENTIFICATION;
  private static final String KEY_INFORMATION_OBJECT_ADDRESS = "InformationObjectAddress";
  private static final String KEY_SINGLE_POINT_INFORMATION = "SinglePointInformation";
  private static final String KEY_LIGHTMEASUREMENT_EVENT = "LightMeasurementEvent";

  @Autowired private DeviceLogItemSlicingRepository deviceLogItemRepository;

  @Autowired private EventRepository eventRepository;

  @Autowired private Iec60870MockServer iec60870MockServer;

  @Given("an existing connection with the RTU")
  public void givenAnExistingConnectionWithTheRTU(final Map<String, String> parameters) {

    final String deviceIdentification = getDeviceIdentification(parameters);

    this.waitForIec60870Connection(deviceIdentification);
  }

  @When("the RTU sends a light measurement event")
  public void whenTheRtuSendsALightMeasurementEvent(final Map<String, String> parameters) {

    final int informationObjectAddress = getInformationObjectAddress(parameters);
    final boolean singlePointInformation = getSinglePointInformation(parameters);

    this.iec60870MockServer
        .getRtuSimulator()
        .updateInformationObject(
            informationObjectAddress,
            Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY,
            singlePointInformation);
  }

  @Then("the device message for the light measurement event should be logged")
  public void theDeviceMessageForTheLightMeasurementEventShouldBeLogged(
      final Map<String, String> parameters) {

    final String deviceIdentification = getDeviceIdentification(parameters);

    final Specification<DeviceLogItem> specification =
        DeviceLogItemSpecifications.hasDeviceIdentification(deviceIdentification)
            .and(DeviceLogItemSpecifications.hasDecodedMessageContaining(EVENT_MESSAGE));

    this.verifyDeviceLogItemCreated(specification);
  }

  @Then("the light measurement event should be logged")
  public void theLightMeasurementEventShouldBeLogged(final Map<String, String> parameters) {

    final String deviceIdentification = getDeviceIdentification(parameters);
    final EventType eventType = getEventType(parameters);

    final Specification<Event> specification =
        EventSpecifications.isFromDevice(deviceIdentification)
            .and(EventSpecifications.hasEventType(eventType));

    this.verifyEventCreated(specification);
  }

  private void waitForIec60870Connection(final String deviceIdentification) {

    final Specification<DeviceLogItem> specification =
        DeviceLogItemSpecifications.hasDeviceIdentification(deviceIdentification)
            .and(DeviceLogItemSpecifications.hasDecodedMessageContaining(GI_TERMINATION_MESSAGE));

    final Runnable task =
        () -> assertThat(this.deviceLogItemRepository.findAll(specification)).isNotEmpty();
    Wait.until(task, 90, 5000);
  }

  private void verifyDeviceLogItemCreated(
      final Specification<DeviceLogItem> deviceLogItemSpecification) {

    Wait.until(
        () ->
            assertThat(this.deviceLogItemRepository.findAll(deviceLogItemSpecification))
                .isNotEmpty());
  }

  private void verifyEventCreated(final Specification<Event> eventSpecification) {

    Wait.until(() -> assertThat(this.eventRepository.findAll(eventSpecification)).isNotEmpty());
  }

  private static String getDeviceIdentification(final Map<String, String> parameters) {
    return ReadSettingsHelper.getString(parameters, KEY_DEVICE_IDENTIFICATION);
  }

  private static int getInformationObjectAddress(final Map<String, String> parameters) {
    return ReadSettingsHelper.getInteger(parameters, KEY_INFORMATION_OBJECT_ADDRESS);
  }

  private static boolean getSinglePointInformation(final Map<String, String> parameters) {
    return ReadSettingsHelper.getBoolean(parameters, KEY_SINGLE_POINT_INFORMATION);
  }

  private static EventType getEventType(final Map<String, String> parameters) {
    final String event =
        ReadSettingsHelper.getString(parameters, KEY_LIGHTMEASUREMENT_EVENT).toUpperCase();
    switch (event) {
      case "DARK":
        return EventType.LIGHT_SENSOR_REPORTS_DARK;
      case "LIGHT":
        return EventType.LIGHT_SENSOR_REPORTS_LIGHT;
      default:
        throw new UnsupportedOperationException(
            "Unsupported value for mapping event to EventType: " + event);
    }
  }
}
