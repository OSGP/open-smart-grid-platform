package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.database.core.EventSpecifications;
import org.opensmartgridplatform.cucumber.platform.database.logging.DeviceLogItemSpecifications;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.mocks.OslpDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice.DeviceSimulatorException;
import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LightMeasurementSteps {

    private static final String GI_TERMINATION_MESSAGE = "ASDU Type: 100, C_IC_NA_1, "
            + "Interrogation command\nCause of transmission: ACTIVATION_TERMINATION";

    private static final String EVENT_MESSAGE = "ASDU Type: 30, M_SP_TB_1, "
            + "Single-point information with time tag CP56Time2a\nCause of transmission: SPONTANEOUS";

    private static final String KEY_DEVICE_IDENTIFICATION = PlatformKeys.KEY_DEVICE_IDENTIFICATION;
    private static final String KEY_LIGHTMEASUREMENT_EVENT = "LightMeasurementEvent";
    private static final String KEY_TRANSITION = "TransitionType";

    private static final String PROTOCOL_OSLP_ELSTER = "OSLP_ELSTER";

    @Autowired
    private DeviceLogItemSlicingRepository deviceLogItemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Iec60870MockServer iec60870MockServer;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private OslpDeviceSteps oslpDeviceSteps;

    @Given("an existing connection with the light measurement gateway")
    public void givenAnExistingConnectionWithTheLightMeasurementGateway(final Map<String, String> parameters) {

        final String lmgIdentification = getDeviceIdentification(parameters);

        this.waitForIec60870Connection(lmgIdentification);
    }

    @When("the light measurement gateway sends a light measurement event for the light measurement device")
    public void whenTheLightMeasurementDeviceSendsALightMeasurementEvent(final Map<String, String> parameters) {

        final String lmdIdentification = getDeviceIdentification(parameters);
        final int lmdInformationObjectAddress = this.getInformationObjectAddress(lmdIdentification);
        final boolean lmdEventValue = getEventType(parameters) == EventType.LIGHT_SENSOR_REPORTS_DARK;

        this.iec60870MockServer.getRtuSimulator()
                .updateInformationObject(lmdInformationObjectAddress,
                        Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY, lmdEventValue);
    }

    @Then("the device message for the light measurement event should be logged")
    public void theDeviceMessageForTheLightMeasurementEventShouldBeLogged(final Map<String, String> parameters) {

        final String lmgIdentification = getDeviceIdentification(parameters);

        final Specification<DeviceLogItem> specification = DeviceLogItemSpecifications
                .hasDeviceIdentification(lmgIdentification)
                .and(DeviceLogItemSpecifications.hasDecodedMessageContaining(EVENT_MESSAGE));

        this.verifyDeviceLogItemCreated(specification);
    }

    @Then("the light measurement event should be logged")
    public void theLightMeasurementEventShouldBeLogged(final Map<String, String> parameters) {

        final String lmdIdentification = getDeviceIdentification(parameters);
        final EventType lmdEventType = getEventType(parameters);

        final Specification<Event> specification = EventSpecifications.isFromDevice(lmdIdentification)
                .and(EventSpecifications.hasEventType(lmdEventType));

        this.verifyEventCreated(specification);
    }

    @Then("a set transition message should be sent to the OSLP SSLD")
    public void thenASetTransitionMessageShouldBeSentToTheOslpSsld(final Map<String, String> parameters)
            throws DeviceSimulatorException {
        final String deviceUid = PlatformDefaults.DEVICE_UID;
        this.oslpDeviceSteps.theSpecificDeviceReturnsASetTransitionResponseOverOslp("OK", PROTOCOL_OSLP_ELSTER,
                deviceUid);

        final Map<String, String> map = new HashMap<>();
        map.put("TransitionType", getTransitionType(parameters));
        map.put("Time", null);
        map.put(PlatformKeys.KEY_DEVICE_UID, deviceUid);
        this.oslpDeviceSteps.aSetTransitionOslpMessageIsSentToDevice(PROTOCOL_OSLP_ELSTER, "SSLD-1", map);
    }

    private void waitForIec60870Connection(final String deviceIdentification) {

        final Specification<DeviceLogItem> specification = DeviceLogItemSpecifications
                .hasDeviceIdentification(deviceIdentification)
                .and(DeviceLogItemSpecifications.hasDecodedMessageContaining(GI_TERMINATION_MESSAGE));

        final Runnable task = () -> assertThat(this.deviceLogItemRepository.findAll(specification)).isNotEmpty();
        Wait.until(task, 90, 5000);
    }

    private void verifyDeviceLogItemCreated(final Specification<DeviceLogItem> deviceLogItemSpecification) {

        Wait.until(() -> assertThat(this.deviceLogItemRepository.findAll(deviceLogItemSpecification)).isNotEmpty());
    }

    private void verifyEventCreated(final Specification<Event> eventSpecification) {

        Wait.until(() -> assertThat(this.eventRepository.findAll(eventSpecification)).isNotEmpty());
    }

    private static String getDeviceIdentification(final Map<String, String> parameters) {
        return ReadSettingsHelper.getString(parameters, KEY_DEVICE_IDENTIFICATION);
    }

    private static EventType getEventType(final Map<String, String> parameters) {
        final String event = ReadSettingsHelper.getString(parameters, KEY_LIGHTMEASUREMENT_EVENT).toUpperCase();
        switch (event) {
        case "DARK":
            return EventType.LIGHT_SENSOR_REPORTS_DARK;
        case "LIGHT":
            return EventType.LIGHT_SENSOR_REPORTS_LIGHT;
        default:
            throw new UnsupportedOperationException("Unsupported value for mapping event to EventType: " + event);
        }
    }

    private static String getTransitionType(final Map<String, String> parameters) {
        final String transition = ReadSettingsHelper.getString(parameters, KEY_TRANSITION).toUpperCase();
        if ("DAY_NIGHT".equals(transition) || "NIGHT_DAY".equals(transition)) {
            return transition;
        } else {
            throw new UnsupportedOperationException("Unsupported value for Transition: " + transition);
        }
    }

    private int getInformationObjectAddress(final String deviceIdentification) {
        final Iec60870Device lmd = this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Iec60870 Light Measurement not found for identification: " + deviceIdentification));
        return lmd.getInformationObjectAddress();
    }
}
