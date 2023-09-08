// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.mocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import com.google.protobuf.ByteString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.repositories.OslpDeviceRepository;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.config.CoreDeviceConfiguration;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice.DeviceSimulatorException;
import org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice.MockOslpServer;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayMap;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.ActionTime;
import org.opensmartgridplatform.oslp.Oslp.DaliConfiguration;
import org.opensmartgridplatform.oslp.Oslp.DeviceType;
import org.opensmartgridplatform.oslp.Oslp.Event;
import org.opensmartgridplatform.oslp.Oslp.EventNotification;
import org.opensmartgridplatform.oslp.Oslp.EventNotificationRequest;
import org.opensmartgridplatform.oslp.Oslp.EventNotificationResponse;
import org.opensmartgridplatform.oslp.Oslp.IndexAddressMap;
import org.opensmartgridplatform.oslp.Oslp.LightType;
import org.opensmartgridplatform.oslp.Oslp.LightValue;
import org.opensmartgridplatform.oslp.Oslp.LightValue.Builder;
import org.opensmartgridplatform.oslp.Oslp.LinkType;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.Oslp.RegisterDeviceResponse;
import org.opensmartgridplatform.oslp.Oslp.RelayConfiguration;
import org.opensmartgridplatform.oslp.Oslp.ResumeScheduleRequest;
import org.opensmartgridplatform.oslp.Oslp.Schedule;
import org.opensmartgridplatform.oslp.Oslp.SetConfigurationRequest;
import org.opensmartgridplatform.oslp.Oslp.SetConfigurationResponse;
import org.opensmartgridplatform.oslp.Oslp.SetScheduleRequest;
import org.opensmartgridplatform.oslp.Oslp.SetTransitionRequest;
import org.opensmartgridplatform.oslp.Oslp.Status;
import org.opensmartgridplatform.oslp.Oslp.TransitionType;
import org.opensmartgridplatform.oslp.Oslp.TriggerType;
import org.opensmartgridplatform.oslp.Oslp.UpdateFirmwareRequest;
import org.opensmartgridplatform.oslp.Oslp.Weekday;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.OslpUtils;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * Class which holds all the OSLP device mock steps in order to let the device mock behave correctly
 * for the automatic test.
 */
public class OslpDeviceSteps {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Autowired private CoreDeviceConfiguration configuration;

  @Autowired private MockOslpServer oslpMockServer;

  @Autowired private OslpDeviceRepository oslpDeviceRepository;

  /**
   * Verify that a get configuration OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a get configuration \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aGetConfigurationOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aGetConfigurationOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a get configuration OSLP message is sent to the specific device.
   *
   * @param protocol The protocol over which the device communicates. - NOT USED -
   * @param deviceIdentification The device identification expected in the message to the device. -
   *     NOT USED -
   * @param deviceUid The device Uid expected in the message to the device.
   */
  @Then(
      "^a get configuration \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aGetConfigurationOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(deviceUid, MessageType.GET_CONFIGURATION);
    assertThat(message).isNotNull();
    assertThat(message.hasGetConfigurationRequest()).isTrue();
  }

  /**
   * Verify that a get firmware version OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a get firmware version \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aGetFirmwareVersionOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aGetFirmwareVersionOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a get firmware version OSLP message is sent to the specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a get firmware version \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aGetFirmwareVersionOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(deviceUid, MessageType.GET_FIRMWARE_VERSION);
    assertThat(message).isNotNull();
    assertThat(message.hasGetFirmwareVersionRequest()).isTrue();

    message.getGetFirmwareVersionRequest();
  }

  /**
   * Verify that a get status OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a get status \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aGetStatusOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aGetStatusOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a get status OSLP message is sent to the specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a get status \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aGetStatusOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message = this.oslpMockServer.waitForRequest(deviceUid, MessageType.GET_STATUS);
    assertThat(message).isNotNull();
    assertThat(message.hasGetStatusRequest()).isTrue();
  }

  /**
   * Verify that a resume schedule OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a resume schedule \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aResumeScheduleOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedRequest)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(
            this.getDeviceUid(expectedRequest), MessageType.RESUME_SCHEDULE);
    assertThat(message).isNotNull();
    assertThat(message.hasResumeScheduleRequest()).isTrue();

    final ResumeScheduleRequest request = message.getResumeScheduleRequest();
    /*
     * resumeScheduleRequest { index: "\000" immediate: false }
     */
    assertThat(OslpUtils.byteStringToInteger(request.getIndex()))
        .isEqualTo(getInteger(expectedRequest, PlatformPubliclightingKeys.KEY_INDEX));
    assertThat(request.getImmediate())
        .isEqualTo(getBoolean(expectedRequest, PlatformPubliclightingKeys.KEY_ISIMMEDIATE));
  }

  /**
   * Verify that a set configuration OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a set configuration \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aSetConfigurationOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedResponseData)
      throws DeviceSimulatorException {
    final Message receivedMessage =
        this.oslpMockServer.waitForRequest(
            this.getDeviceUid(expectedResponseData), MessageType.SET_CONFIGURATION);
    assertThat(receivedMessage).isNotNull();
    assertThat(receivedMessage.hasSetConfigurationRequest()).isTrue();

    final SetConfigurationRequest receivedConfiguration =
        receivedMessage.getSetConfigurationRequest();

    if (!StringUtils.isEmpty(expectedResponseData.get(PlatformKeys.KEY_LIGHTTYPE))
        && receivedConfiguration.getLightType() != null) {
      final LightType expectedLightType =
          getEnum(expectedResponseData, PlatformKeys.KEY_LIGHTTYPE, LightType.class);
      assertThat(receivedConfiguration.getLightType()).isEqualTo(expectedLightType);

      switch (expectedLightType) {
        case DALI:
          final DaliConfiguration receivedDaliConfiguration =
              receivedConfiguration.getDaliConfiguration();
          if (receivedDaliConfiguration != null) {
            if (expectedResponseData.containsKey(PlatformKeys.DC_LIGHTS)
                && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.DC_LIGHTS))) {
              assertThat(
                      OslpUtils.byteStringToInteger(receivedDaliConfiguration.getNumberOfLights()))
                  .isEqualTo(getInteger(expectedResponseData, PlatformKeys.DC_LIGHTS));
            }

            if (expectedResponseData.containsKey(PlatformKeys.DC_MAP)
                && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.DC_MAP))) {
              assertThat(receivedDaliConfiguration.getAddressMapList()).isNotNull();
              final String[] expectedDcMapArray =
                  getString(expectedResponseData, PlatformKeys.DC_MAP).split(";");
              assertThat(receivedDaliConfiguration.getAddressMapList().size())
                  .isEqualTo(expectedDcMapArray.length);

              final List<IndexAddressMap> receivedIndexAddressMapList =
                  receivedDaliConfiguration.getAddressMapList();
              for (int i = 0; i < expectedDcMapArray.length; i++) {
                final String[] expectedDcMapArrayElements = expectedDcMapArray[i].split(",");
                assertThat(
                        OslpUtils.byteStringToInteger(
                            receivedIndexAddressMapList.get(i).getIndex()))
                    .isEqualTo((Integer) Integer.parseInt(expectedDcMapArrayElements[0]));
                assertThat(
                        OslpUtils.byteStringToInteger(
                            receivedIndexAddressMapList.get(i).getAddress()))
                    .isEqualTo((Integer) Integer.parseInt(expectedDcMapArrayElements[1]));
              }
            }
          }
          break;

        case RELAY:
          final RelayConfiguration receivedRelayConfiguration =
              receivedConfiguration.getRelayConfiguration();
          if (receivedRelayConfiguration != null) {

            if (!StringUtils.isEmpty(expectedResponseData.get(PlatformKeys.RELAY_CONF))
                && receivedRelayConfiguration.getAddressMapList() != null) {

              // Construct sorted list of received relay maps
              final List<RelayMap> receivedRelayMapList =
                  RelayMapConverter.convertIndexAddressMapListToRelayMapList(
                      receivedRelayConfiguration.getAddressMapList());
              Collections.sort(receivedRelayMapList);

              // Construct sorted list of expected relay maps
              final String[] expectedRelayMapArray =
                  getString(expectedResponseData, PlatformKeys.RELAY_CONF).split(";");
              final List<RelayMap> expectedRelayMapList =
                  RelayMapConverter.convertStringsListToRelayMapList(expectedRelayMapArray);
              Collections.sort(expectedRelayMapList);

              assertThat(CollectionUtils.isEmpty(receivedRelayMapList))
                  .as("Either the expected or the received relay maps are empty, but not both")
                  .isEqualTo(CollectionUtils.isEmpty(expectedRelayMapList));

              if (!CollectionUtils.isEmpty(receivedRelayMapList)
                  && !CollectionUtils.isEmpty(expectedRelayMapList)) {
                assertThat(receivedRelayMapList.size())
                    .as("Size of expected and received relay map list differs")
                    .isEqualTo(expectedRelayMapList.size());
              }

              // Compare the contents of each relay map
              for (int i = 0; i < expectedRelayMapList.size(); i++) {
                assertThat(receivedRelayMapList.get(i))
                    .as("Expected and received relay map differs for " + i)
                    .isEqualTo(expectedRelayMapList.get(i));
              }
            }
          }
          break;

        case ONE_TO_TEN_VOLT:
        case ONE_TO_TEN_VOLT_REVERSE:
        case LT_NOT_SET:
        default:
          assertThat(receivedConfiguration.getDaliConfiguration().getAddressMapList().size())
              .isEqualTo(0);

          assertThat(receivedConfiguration.getRelayConfiguration().getAddressMapList().size())
              .isEqualTo(0);
      }
    }

    if (!StringUtils.isEmpty(expectedResponseData.get(PlatformKeys.KEY_PREFERRED_LINKTYPE))
        && receivedConfiguration.getPreferredLinkType() != null) {
      assertThat(receivedConfiguration.getPreferredLinkType())
          .isEqualTo(
              getEnum(expectedResponseData, PlatformKeys.KEY_PREFERRED_LINKTYPE, LinkType.class));
    }

    if (!StringUtils.isEmpty(expectedResponseData.get(PlatformKeys.OSGP_IP_ADDRESS))) {
      assertThat(OslpDeviceSteps.convertIpAddress(receivedConfiguration.getOspgIpAddress()))
          .isEqualTo(expectedResponseData.get(PlatformKeys.OSGP_IP_ADDRESS));
    }

    if (!StringUtils.isEmpty(expectedResponseData.get(PlatformKeys.OSGP_PORT))) {
      assertThat(receivedConfiguration.getOsgpPortNumber())
          .isEqualTo(Integer.parseInt(expectedResponseData.get(PlatformKeys.OSGP_PORT)));
    }

    if (!StringUtils.isEmpty(
        expectedResponseData.get(PlatformKeys.KEY_ASTRONOMICAL_SUNRISE_OFFSET))) {
      assertThat(receivedConfiguration.getAstroGateSunRiseOffset())
          .isEqualTo(
              Integer.parseInt(
                  expectedResponseData.get(PlatformKeys.KEY_ASTRONOMICAL_SUNRISE_OFFSET)));
    }

    if (!StringUtils.isEmpty(
        expectedResponseData.get(PlatformKeys.KEY_ASTRONOMICAL_SUNSET_OFFSET))) {
      assertThat(receivedConfiguration.getAstroGateSunSetOffset())
          .isEqualTo(
              Integer.parseInt(
                  expectedResponseData.get(PlatformKeys.KEY_ASTRONOMICAL_SUNSET_OFFSET)));
    }
  }

  /**
   * Verify that an event notification OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a set event notification \"([^\"]*)\" message is sent to device \"([^\"]*)\"")
  public void aSetEventNotificationOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aSetEventNotificationOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that an event notification OSLP message is sent to specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a set event notification \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"")
  public void aSetEventNotificationOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(deviceUid, MessageType.SET_EVENT_NOTIFICATIONS);
    assertThat(message).isNotNull();
    assertThat(message.hasSetEventNotificationsRequest()).isTrue();
  }

  /**
   * Verify that a set light OSLP message is sent to the device.
   *
   * @param nofLightValues The parameters expected in the message of the device.
   */
  @Then("^a set light \"([^\"]*)\" message with \"([^\"]*)\" lightvalues is sent to the device$")
  public void aSetLightOslpMessageWithLightValuesIsSentToTheDevice(
      final String protocol, final int nofLightValues) throws DeviceSimulatorException {
    this.aSetLightOslpMessageWithLightValuesIsSentToSpecificDevice(
        protocol, nofLightValues, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a set light OSLP message is sent to specific device.
   *
   * @param nofLightValues The parameters expected in the message of the device.
   */
  @Then(
      "^a set light \"([^\"]*)\" message with \"([^\"]*)\" lightvalues is sent to the device with deviceUid \"([^\"]*)\"$")
  public void aSetLightOslpMessageWithLightValuesIsSentToSpecificDevice(
      final String protocol, final int nofLightValues, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message = this.oslpMockServer.waitForRequest(deviceUid, MessageType.SET_LIGHT);
    assertThat(message).isNotNull();
    assertThat(message.hasSetLightRequest()).isTrue();

    assertThat(message.getSetLightRequest().getValuesList().size()).isEqualTo(nofLightValues);
  }

  /**
   * Verify that a set light OSLP message is sent to the device.
   *
   * @param expectedParameters The parameters expected in the message of the device.
   */
  @Then("^a set light \"([^\"]*)\" message with one light value is sent to the device$")
  public void aSetLightOslpMessageWithOneLightvalueIsSentToTheDevice(
      final String protocol, final Map<String, String> expectedParameters)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(
            this.getDeviceUid(expectedParameters), MessageType.SET_LIGHT);
    assertThat(message).isNotNull();
    assertThat(message.hasSetLightRequest()).isTrue();

    final LightValue lightValue = message.getSetLightRequest().getValues(0);

    assertThat(OslpUtils.byteStringToInteger(lightValue.getIndex()))
        .isEqualTo(
            getInteger(
                expectedParameters,
                PlatformPubliclightingKeys.KEY_INDEX,
                PlatformPubliclightingDefaults.DEFAULT_INDEX));

    if (expectedParameters.containsKey(PlatformPubliclightingKeys.KEY_DIMVALUE)
        && !StringUtils.isEmpty(expectedParameters.get(PlatformPubliclightingKeys.KEY_DIMVALUE))) {

      assertThat(OslpUtils.byteStringToInteger(lightValue.getDimValue()))
          .isEqualTo(
              getInteger(
                  expectedParameters,
                  PlatformPubliclightingKeys.KEY_DIMVALUE,
                  PlatformPubliclightingDefaults.DEFAULT_DIMVALUE));
    }

    assertThat(lightValue.getOn())
        .isEqualTo(
            getBoolean(
                expectedParameters,
                PlatformPubliclightingKeys.KEY_ON,
                PlatformPubliclightingDefaults.DEFAULT_ON));
  }

  /**
   * Verify that a set light schedule OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device. @
   */
  @Then("^a set light schedule \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aSetLightScheduleOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedRequest)
      throws DeviceSimulatorException {
    this.checkAndValidateRequest(MessageType.SET_LIGHT_SCHEDULE, expectedRequest);
  }

  /**
   * Verify that a set reboot OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a set reboot \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aSetRebootOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aSetRebootOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a set reboot OSLP message is sent to specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a set reboot \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aSetRebootOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message = this.oslpMockServer.waitForRequest(deviceUid, MessageType.SET_REBOOT);
    assertThat(message).isNotNull();
    assertThat(message.hasSetRebootRequest()).isTrue();
  }

  /**
   * Verify that a set reverse tariff schedule OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   * @param expectedRequest The request parameters expected in the message to the device.
   */
  @Then("^a set reverse tariff schedule \"([^\"]*)\" message is sent to device \"(?:([^\"]*))\"$")
  public void aSetReverseTariffScheduleOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedRequest)
      throws DeviceSimulatorException {
    this.aSetTariffScheduleOslpMessageIsSentToDevice(
        protocol, deviceIdentification, expectedRequest);
  }

  /**
   * Verify that a set tariff schedule OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a set tariff schedule \"([^\"]*)\" message is sent to device \"(?:([^\"]*))\"$")
  public void aSetTariffScheduleOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedRequest)
      throws DeviceSimulatorException {
    this.checkAndValidateRequest(MessageType.SET_TARIFF_SCHEDULE, expectedRequest);
  }

  /**
   * Verify that a set transition OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a set transition \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aSetTransitionOslpMessageIsSentToDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedResult)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(
            this.getDeviceUid(expectedResult), MessageType.SET_TRANSITION);
    assertThat(message).isNotNull();
    assertThat(message.hasSetTransitionRequest()).isTrue();

    final SetTransitionRequest request = message.getSetTransitionRequest();

    assertThat(request.getTransitionType())
        .isEqualTo(
            getEnum(
                expectedResult,
                PlatformPubliclightingKeys.KEY_TRANSITION_TYPE,
                TransitionType.class));

    if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_TIME)) {
      // TODO: How to check the time?
      // Assert.assertEquals(expectedResult.get(Keys.KEY_TIME),
      // request.getTime());
    }
  }

  /**
   * Verify that a start device OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a start device \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aStartDeviceOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aStartDeviceOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a start device OSLP message is sent to specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a start device \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aStartDeviceOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(deviceUid, MessageType.START_SELF_TEST);
    assertThat(message).isNotNull();
    assertThat(message.hasStartSelfTestRequest()).isTrue();
  }

  /**
   * Verify that a stop device OSLP message is sent to the device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then("^a stop device \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void aStopDeviceOslpMessageIsSentToDevice(
      final String protocol, final String deviceIdentification) throws DeviceSimulatorException {
    this.aStopDeviceOslpMessageIsSentToSpecificDevice(
        protocol, deviceIdentification, this.getDeviceUid(new HashMap<>()));
  }

  /**
   * Verify that a stop device OSLP message is sent to specific device.
   *
   * @param deviceIdentification The device identification expected in the message to the device.
   */
  @Then(
      "^a stop device \"([^\"]*)\" message is sent to device \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void aStopDeviceOslpMessageIsSentToSpecificDevice(
      final String protocol, final String deviceIdentification, final String deviceUid)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(deviceUid, MessageType.STOP_SELF_TEST);
    assertThat(message).isNotNull();
    assertThat(message.hasStopSelfTestRequest()).isTrue();
  }

  /** Setup method to get a status which should be returned by the mock. */
  private void callMockSetScheduleResponse(
      final String deviceUid, final String result, final MessageType type) {
    this.oslpMockServer.mockSetScheduleResponse(
        deviceUid, type, Enum.valueOf(Status.class, result));
  }

  private void checkAndValidateRequest(
      final MessageType type, final Map<String, String> expectedRequest)
      throws DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(this.getDeviceUid(expectedRequest), type);
    assertThat(message).isNotNull();
    assertThat(message.hasSetScheduleRequest()).isTrue();

    final SetScheduleRequest request = message.getSetScheduleRequest();

    for (final Schedule schedule : request.getSchedulesList()) {
      if (type == MessageType.SET_LIGHT_SCHEDULE) {

        assertThat(schedule.getWeekday())
            .isEqualTo(
                getEnum(
                    expectedRequest, PlatformPubliclightingKeys.SCHEDULE_WEEKDAY, Weekday.class));
      }
      if (StringUtils.isNotBlank(
          expectedRequest.get(PlatformPubliclightingKeys.SCHEDULE_STARTDAY))) {
        final String startDay =
            JavaTimeHelpers.formatDate(
                getDate(expectedRequest, PlatformPubliclightingKeys.SCHEDULE_STARTDAY)
                    .withZoneSameInstant(ZoneId.of("UTC")),
                FORMATTER);

        assertThat(schedule.getStartDay()).isEqualTo(startDay);
      }
      if (StringUtils.isNotBlank(expectedRequest.get(PlatformPubliclightingKeys.SCHEDULE_ENDDAY))) {
        final String endDay =
            JavaTimeHelpers.formatDate(
                getDate(expectedRequest, PlatformPubliclightingKeys.SCHEDULE_ENDDAY)
                    .withZoneSameInstant(ZoneId.of("UTC")),
                DateTimeFormatter.ofPattern("yyyyMMdd"));

        assertThat(schedule.getEndDay()).isEqualTo(endDay);
      }

      if (type == MessageType.SET_LIGHT_SCHEDULE) {
        assertThat(schedule.getActionTime())
            .isEqualTo(
                getEnum(
                    expectedRequest,
                    PlatformPubliclightingKeys.SCHEDULE_ACTIONTIME,
                    ActionTime.class));
      }
      if (StringUtils.isNotBlank(expectedRequest.get(PlatformPubliclightingKeys.SCHEDULE_TIME))) {
        String expectedTime =
            getString(expectedRequest, PlatformPubliclightingKeys.SCHEDULE_TIME).replace(":", "");
        if (expectedTime.contains(".")) {
          expectedTime = expectedTime.substring(0, expectedTime.indexOf("."));
        }

        assertThat(schedule.getTime()).isEqualTo(expectedTime);
      }
      final String scheduleLightValue =
          getString(
              expectedRequest,
              (type == MessageType.SET_LIGHT_SCHEDULE)
                  ? PlatformPubliclightingKeys.SCHEDULE_LIGHTVALUES
                  : PlatformPubliclightingKeys.SCHEDULE_TARIFFVALUES);
      final String[] scheduleLightValues = scheduleLightValue.split(";");
      assertThat(schedule.getValueCount()).isEqualTo(scheduleLightValues.length);

      for (int i = 0; i < scheduleLightValues.length; i++) {
        final Integer index = OslpUtils.byteStringToInteger(schedule.getValue(i).getIndex()),
            dimValue = OslpUtils.byteStringToInteger(schedule.getValue(i).getDimValue());
        if (type == MessageType.SET_LIGHT_SCHEDULE) {
          assertThat(
                  String.format(
                      "%s,%s,%s",
                      (index != null) ? index : "",
                      schedule.getValue(i).getOn(),
                      (dimValue != null) ? dimValue : ""))
              .isEqualTo(scheduleLightValues[i]);
        } else if (type == MessageType.SET_TARIFF_SCHEDULE) {
          assertThat(
                  String.format(
                      "%s,%s", (index != null) ? index : "", !schedule.getValue(i).getOn()))
              .isEqualTo(scheduleLightValues[i]);
        }
      }

      if (type == MessageType.SET_LIGHT_SCHEDULE) {
        assertThat(schedule.getTriggerType())
            .isEqualTo(
                (!getString(expectedRequest, PlatformPubliclightingKeys.SCHEDULE_TRIGGERTYPE)
                        .isEmpty())
                    ? getEnum(
                        expectedRequest,
                        PlatformPubliclightingKeys.SCHEDULE_TRIGGERTYPE,
                        TriggerType.class)
                    : TriggerType.TT_NOT_SET);

        if (StringUtils.isNotBlank(
            expectedRequest.get(PlatformPubliclightingKeys.SCHEDULE_TRIGGERWINDOW))) {
          final String[] windowTypeValues =
              getString(expectedRequest, PlatformPubliclightingKeys.SCHEDULE_TRIGGERWINDOW)
                  .split(",");
          if (windowTypeValues.length == 2) {
            assertThat(schedule.getWindow().getMinutesBefore())
                .isEqualTo(Integer.parseInt(windowTypeValues[0]));
            assertThat(schedule.getWindow().getMinutesAfter())
                .isEqualTo(Integer.parseInt(windowTypeValues[1]));
          }
        }
      }
    }
  }

  /**
   * Simulates sending an OSLP EventNotification message to the OSLP Protocol adapter.
   *
   * @throws DeviceSimulatorException
   * @throws IOException
   * @throws ParseException
   */
  @When("^receiving an \"([^\"]*)\" event notification message$")
  public void receivingAnOslpEventNotificationMessage(
      final String protocol, final Map<String, String> settings)
      throws DeviceSimulatorException, IOException, ParseException {

    final EventNotification eventNotification =
        EventNotification.newBuilder()
            .setDescription(getString(settings, PlatformPubliclightingKeys.KEY_DESCRIPTION, ""))
            .setEvent(getEnum(settings, PlatformPubliclightingKeys.KEY_EVENT, Event.class))
            .build();

    final Message message =
        Oslp.Message.newBuilder()
            .setEventNotificationRequest(
                EventNotificationRequest.newBuilder().addNotifications(eventNotification))
            .build();

    // Save the OSLP response for later validation.
    ScenarioContext.current()
        .put(
            PlatformPubliclightingKeys.RESPONSE,
            this.oslpMockServer.sendRequest(this.getDeviceUid(settings), message));
  }

  @Given("^the device returns a get configuration status over \"([^\"]*)\"$")
  public void theDeviceReturnsAGetConfigurationStatusOverOslp(
      final String protocol, final Map<String, String> requestParameters)
      throws UnknownHostException {
    this.theDeviceReturnsAGetConfigurationStatusWithResultOverOslp(
        getEnum(requestParameters, PlatformPubliclightingKeys.KEY_STATUS, Status.class, Status.OK)
            .name(),
        protocol,
        requestParameters);
  }

  @Given(
      "^the device returns a get configuration status \"([^\"]*)\" over \"([^\"]*)\" using default values$")
  public void theDeviceReturnsAGetConfigurationStatusWithResultOverOslpUsingDefaultValues(
      final String result, final String protocol) throws UnknownHostException {
    this.theDeviceReturnsAGetConfigurationStatusWithResultOverOslp(
        result, protocol, new HashMap<>());
  }

  /**
   * Setup method to set the configuration status which should be returned by the mock.
   *
   * @throws UnknownHostException
   */
  @Given("^the device returns a get configuration status \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAGetConfigurationStatusWithResultOverOslp(
      final String result, final String protocol, final Map<String, String> requestParameters)
      throws UnknownHostException {

    final String osgpIpAddress =
        getString(requestParameters, PlatformPubliclightingKeys.OSGP_IP_ADDRESS);
    final String osgpIpAddressMock;
    if (StringUtils.isEmpty(osgpIpAddress)) {
      osgpIpAddressMock = null;
    } else {
      osgpIpAddressMock = osgpIpAddress;
    }

    this.oslpMockServer.mockGetConfigurationResponse(
        this.getDeviceUid(requestParameters),
        Enum.valueOf(Status.class, result),
        getEnum(
            requestParameters,
            PlatformPubliclightingKeys.KEY_LIGHTTYPE,
            LightType.class,
            PlatformPubliclightingDefaults.DEFAULT_LIGHTTYPE),
        getString(
            requestParameters,
            PlatformPubliclightingKeys.DC_LIGHTS,
            PlatformPubliclightingDefaults.DC_LIGHTS),
        getString(
            requestParameters,
            PlatformPubliclightingKeys.DC_MAP,
            PlatformPubliclightingDefaults.DEFAULT_DC_MAP),
        getString(
            requestParameters,
            PlatformPubliclightingKeys.RELAY_CONF,
            PlatformPubliclightingDefaults.DEFAULT_RELAY_CONFIGURATION),
        getEnum(
            requestParameters,
            PlatformPubliclightingKeys.KEY_PREFERRED_LINKTYPE,
            LinkType.class,
            PlatformPubliclightingDefaults.DEFAULT_PREFERRED_LINKTYPE),
        osgpIpAddressMock,
        getInteger(
            requestParameters,
            PlatformPubliclightingKeys.OSGP_PORT,
            PlatformPubliclightingDefaults.DEFAULT_OSLP_PORT));
  }

  /** Setup method to set the status which should be returned by the mock. */
  @Given("^the device returns a get status response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAGetStatusResponseWithResultOverOslp(
      final String result, final String protocol, final Map<String, String> requestParameters) {

    int eventNotificationTypes = 0;
    if (getString(
                requestParameters,
                PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONTYPES,
                PlatformPubliclightingDefaults.DEFAULT_EVENTNOTIFICATIONTYPES)
            .trim()
            .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)
            .length
        > 0) {
      for (final String eventNotificationType :
          getString(
                  requestParameters,
                  PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONTYPES,
                  PlatformPubliclightingDefaults.DEFAULT_EVENTNOTIFICATIONTYPES)
              .trim()
              .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)) {
        if (!eventNotificationType.isEmpty()) {
          eventNotificationTypes =
              eventNotificationTypes
                  + Enum.valueOf(EventNotificationType.class, eventNotificationType.trim())
                      .getValue();
        }
      }
    }

    final List<LightValue> lightValues = new ArrayList<>();
    if (!getString(
                requestParameters,
                PlatformPubliclightingKeys.KEY_LIGHTVALUES,
                PlatformPubliclightingDefaults.DEFAULT_LIGHTVALUES)
            .isEmpty()
        && getString(
                    requestParameters,
                    PlatformPubliclightingKeys.KEY_LIGHTVALUES,
                    PlatformPubliclightingDefaults.DEFAULT_LIGHTVALUES)
                .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON)
                .length
            > 0) {

      for (final String lightValueString :
          getString(
                  requestParameters,
                  PlatformPubliclightingKeys.KEY_LIGHTVALUES,
                  PlatformPubliclightingDefaults.DEFAULT_LIGHTVALUES)
              .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON)) {
        final String[] parts = lightValueString.split(PlatformPubliclightingKeys.SEPARATOR_COMMA);

        final Builder lightValueBuilder =
            LightValue.newBuilder()
                .setIndex(OslpUtils.integerToByteString(Integer.parseInt(parts[0])))
                .setOn(parts[1].equalsIgnoreCase("true"));
        if (lightValueBuilder.getOn()) {
          lightValueBuilder.setDimValue(OslpUtils.integerToByteString(Integer.parseInt(parts[2])));
        }

        lightValues.add(lightValueBuilder.build());
      }
    }

    final List<LightValue> tariffValues = new ArrayList<>();
    if (!getString(
                requestParameters,
                PlatformPubliclightingKeys.KEY_TARIFFVALUES,
                PlatformPubliclightingDefaults.DEFAULT_TARIFFVALUES)
            .isEmpty()
        && getString(
                    requestParameters,
                    PlatformPubliclightingKeys.KEY_TARIFFVALUES,
                    PlatformPubliclightingDefaults.DEFAULT_TARIFFVALUES)
                .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON)
                .length
            > 0) {

      for (final String tariffValueString :
          getString(
                  requestParameters,
                  PlatformPubliclightingKeys.KEY_TARIFFVALUES,
                  PlatformPubliclightingDefaults.DEFAULT_TARIFFVALUES)
              .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON)) {
        final String[] parts = tariffValueString.split(PlatformPubliclightingKeys.SEPARATOR_COMMA);

        final LightValue tariffValue =
            LightValue.newBuilder()
                .setIndex(OslpUtils.integerToByteString(Integer.parseInt(parts[0])))
                .setOn(parts[1].toLowerCase().equals("true"))
                .build();

        tariffValues.add(tariffValue);
      }
    }

    this.oslpMockServer.mockGetStatusResponse(
        this.getDeviceUid(requestParameters),
        getEnum(
            requestParameters,
            PlatformPubliclightingKeys.KEY_PREFERRED_LINKTYPE,
            LinkType.class,
            PlatformPubliclightingDefaults.DEFAULT_PREFERRED_LINKTYPE),
        getEnum(
            requestParameters,
            PlatformPubliclightingKeys.KEY_ACTUAL_LINKTYPE,
            LinkType.class,
            PlatformPubliclightingDefaults.DEFAULT_ACTUAL_LINKTYPE),
        getEnum(
            requestParameters,
            PlatformPubliclightingKeys.KEY_LIGHTTYPE,
            LightType.class,
            PlatformPubliclightingDefaults.DEFAULT_LIGHTTYPE),
        eventNotificationTypes,
        Enum.valueOf(Status.class, result),
        lightValues,
        tariffValues);
  }

  /** Setup method to resume a schedule which should be returned by the mock. */
  @Given("^the device returns a resume schedule response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAResumeScheduleResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsAResumeScheduleResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a resume schedule response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsAResumeScheduleResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockResumeScheduleResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  @Given("^the device returns a set configuration status over \"([^\"]*)\"$")
  public void theDeviceReturnsASetConfigurationStatusOverOslp(
      final String protocol, final Map<String, String> requestParameters) {
    this.oslpMockServer.mockSetConfigurationResponse(
        this.getDeviceUid(requestParameters),
        getEnum(requestParameters, PlatformPubliclightingKeys.KEY_STATUS, Status.class));
  }

  /** Setup method to set the configuration status which should be returned by the mock. */
  @Given("^the device returns a set configuration status \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetConfigurationStatusWithStatusOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetConfigurationStatusWithStatusOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set configuration status \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetConfigurationStatusWithStatusOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockSetConfigurationResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  /** Setup method to set the event notification which should be returned by the mock. */
  @Given("^the device returns a set event notification \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetEventNotificationOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetEventNotificationOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set event notification \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetEventNotificationOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockSetEventNotificationResponse(
        deviceUid, Enum.valueOf(Status.class, result));
  }

  /** Setup method to set a light which should be returned by the mock. */
  @Given("^the device returns a set light response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetLightOverOslp(final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetLightOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set light response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetLightOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockSetLightResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  /** Setup method to get a status which should be returned by the mock. */
  @Given("^the device returns a set light schedule response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetLightScheduleResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetLightScheduleResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set light schedule response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetLightScheduleResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.callMockSetScheduleResponse(deviceUid, result, MessageType.SET_LIGHT_SCHEDULE);
  }

  /**
   * Setup method which combines get configuration, set configuration and set schedule mock
   * responses. The protocol adapter component for OSLP executes these 3 steps when a light schedule
   * is pushed to a device. In case of FAILURE response, the protocol adapter will only validate the
   * last of the 3 steps.
   */
  @Given(
      "^the device returns the responses for setting a light schedule with result \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsTheResponsesForSettingLightScheduleWithResultOverProtocol(
      final String result, final String protocol) throws UnknownHostException {
    this.theSpecificDeviceReturnsTheResponsesForSettingLightScheduleWithResultOverProtocol(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns the responses for setting a light schedule with result \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsTheResponsesForSettingLightScheduleWithResultOverProtocol(
      final String result, final String protocol, final String deviceUid)
      throws UnknownHostException {
    this.theDeviceReturnsAGetConfigurationStatusWithResultOverOslp(
        result, protocol, this.setDeviceUid(new HashMap<>(), deviceUid));
    this.theSpecificDeviceReturnsASetConfigurationStatusWithStatusOverOslp(
        result, protocol, deviceUid);
    this.theSpecificDeviceReturnsASetLightScheduleResponseOverOslp(result, protocol, deviceUid);
  }

  /** Setup method to set a reboot which should be returned by the mock. */
  @Given("^the device returns a set reboot response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetRebootResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetRebootResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set reboot response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetRebootResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockSetRebootResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  @Given(
      "^the device returns a set reverse tariff schedule response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetReverseTariffScheduleResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetTariffScheduleResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given("^the device returns a set tariff schedule response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsASetTariffScheduleResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetTariffScheduleResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set tariff schedule response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetTariffScheduleResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.callMockSetScheduleResponse(deviceUid, result, MessageType.SET_TARIFF_SCHEDULE);
  }

  /** Setup method to set a transition which should be returned by the mock. */
  @Given("^the device returns a set transition response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetTransitionResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsASetTransitionResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a set transition response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsASetTransitionResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockSetTransitionResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  /** Setup method to start a device which should be returned by the mock. */
  @Given("^the device returns a start device response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAStartDeviceResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsAStartDeviceResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a start device response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsAStartDeviceResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockStartDeviceResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  /** Setup method to stop a device which should be returned by the mock. */
  @Given("^the device returns a stop device response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAStopDeviceResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsAStopDeviceResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a stop device response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsAStopDeviceResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockStopDeviceResponse(
        deviceUid, ByteString.EMPTY, Enum.valueOf(Status.class, result));
  }

  /**
   * Setup method to set the firmware which should be returned by the mock.
   *
   * @param firmwareVersion The firmware to respond.
   */
  @Given("^the device returns firmware version \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsFirmwareVersionOverOslp(
      final String firmwareVersion, final String protocol) {
    this.theSpecificDeviceReturnsFirmwareVersionOverOslp(
        firmwareVersion, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns firmware version \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsFirmwareVersionOverOslp(
      final String firmwareVersion, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockGetFirmwareVersionResponse(deviceUid, firmwareVersion);
  }

  /** Setup method to set the firmware which should be returned by the mock. */
  @Given("^the device returns update firmware response \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsUpdateFirmwareResponseOverOslp(
      final String result, final String protocol) {
    this.theSpecificDeviceReturnsUpdateFirmwareResponseOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns update firmware response \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsUpdateFirmwareResponseOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockUpdateFirmwareResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  @Then("^the \"([^\"]*)\" event notification response contains$")
  public void theOslpEventNotificationResponseContains(
      final String protocol, final Map<String, String> expectedResponse) {
    final Message responseMessage =
        (Message) ScenarioContext.current().get(PlatformPubliclightingKeys.RESPONSE);

    final EventNotificationResponse response = responseMessage.getEventNotificationResponse();

    assertThat(response.getStatus().name())
        .isEqualTo(getString(expectedResponse, PlatformPubliclightingKeys.KEY_STATUS));
  }

  @Given("^the device sends a register device request to the platform over \"([^\"]*)\"$")
  public void theDeviceSendsARegisterDeviceRequestToThePlatform(
      final String protocol, final Map<String, String> settings) throws DeviceSimulatorException {

    try {
      this.oslpMockServer.incrementSequenceNumber(this.getDeviceUid(settings));
      final OslpEnvelope request =
          this.createEnvelopeBuilder(
                  getString(
                      settings,
                      PlatformPubliclightingKeys.KEY_DEVICE_UID,
                      PlatformPubliclightingDefaults.DEVICE_UID),
                  this.oslpMockServer.getSequenceNumber(this.getDeviceUid(settings)))
              .withPayloadMessage(
                  Message.newBuilder()
                      .setRegisterDeviceRequest(
                          Oslp.RegisterDeviceRequest.newBuilder()
                              .setDeviceIdentification(
                                  getString(
                                      settings,
                                      PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                                      PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION))
                              .setIpAddress(
                                  ByteString.copyFrom(
                                      InetAddress.getByName(
                                              getString(
                                                  settings,
                                                  PlatformPubliclightingKeys.IP_ADDRESS,
                                                  PlatformPubliclightingDefaults.LOCALHOST))
                                          .getAddress()))
                              .setDeviceType(
                                  getEnum(
                                      settings,
                                      PlatformPubliclightingKeys.KEY_DEVICE_TYPE,
                                      DeviceType.class,
                                      DeviceType.SSLD))
                              .setHasSchedule(
                                  getBoolean(
                                      settings,
                                      PlatformPubliclightingKeys.KEY_HAS_SCHEDULE,
                                      PlatformPubliclightingDefaults.DEFAULT_HASSCHEDULE))
                              .setRandomDevice(
                                  getInteger(
                                      settings,
                                      PlatformPubliclightingKeys.RANDOM_DEVICE,
                                      PlatformPubliclightingDefaults.RANDOM_DEVICE)))
                      .build())
              .build();

      this.send(request, settings);
    } catch (final IOException | IllegalArgumentException e) {
      ScenarioContext.current().put("Error", e);
    }
  }

  @Given("^the device sends a confirm register device request to the platform over \"([^\"]*)\"$")
  public void theDeviceSendsAConfirmRegisterDeviceRequestToThePlatform(
      final String protocol, final Map<String, String> settings) throws DeviceSimulatorException {

    try {
      final String deviceIdentification =
          getString(
              settings,
              PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
              PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION);

      final String deviceUid =
          getString(
              settings,
              PlatformPubliclightingKeys.KEY_DEVICE_UID,
              PlatformPubliclightingDefaults.DEVICE_UID);

      final OslpDevice oslpDevice =
          this.oslpDeviceRepository.findByDeviceIdentification(deviceIdentification);
      final int randomDevice = oslpDevice.getRandomDevice();
      final int randomPlatform = oslpDevice.getRandomPlatform();

      final Oslp.ConfirmRegisterDeviceRequest confirmRegisterDeviceRequest =
          Oslp.ConfirmRegisterDeviceRequest.newBuilder()
              .setRandomDevice(randomDevice)
              .setRandomPlatform(randomPlatform)
              .build();

      final Message message =
          Message.newBuilder()
              .setConfirmRegisterDeviceRequest(confirmRegisterDeviceRequest)
              .build();

      this.oslpMockServer.incrementSequenceNumber(this.getDeviceUid(settings));
      final OslpEnvelope request =
          this.createEnvelopeBuilder(
                  deviceUid, this.oslpMockServer.getSequenceNumber(this.getDeviceUid(settings)))
              .withPayloadMessage(message)
              .build();

      this.send(request, settings);
    } catch (final IOException | IllegalArgumentException e) {
      ScenarioContext.current().put("Error", e);
    }
  }

  @Given("^the device sends an event notification request to the platform over \"([^\"]*)\"$")
  public void theDeviceSendsAnEventNotificationRequestToThePlatform(
      final String protocol, final Map<String, String> settings)
      throws IOException, DeviceSimulatorException {

    final Oslp.EventNotification.Builder builder =
        Oslp.EventNotification.newBuilder()
            .setEvent(getEnum(settings, PlatformKeys.KEY_EVENT, Event.class))
            .setDescription(getString(settings, PlatformKeys.KEY_DESCRIPTION));
    final String timeStamp = getString(settings, PlatformKeys.TIMESTAMP);
    if (timeStamp != null) {
      builder.setTimestamp(timeStamp);
    }

    final String indexValue = getString(settings, PlatformPubliclightingKeys.KEY_INDEX);
    final Integer index =
        indexValue == null || "EMPTY".equals(indexValue) ? 0 : Integer.valueOf(indexValue);
    builder.setIndex(ByteString.copyFrom(new byte[] {index.byteValue()}));

    this.oslpMockServer.incrementSequenceNumber(this.getDeviceUid(settings));
    final OslpEnvelope request =
        this.createEnvelopeBuilder(
                getString(
                    settings,
                    PlatformPubliclightingKeys.KEY_DEVICE_UID,
                    PlatformPubliclightingDefaults.DEVICE_UID),
                this.oslpMockServer.getSequenceNumber(this.getDeviceUid(settings)))
            .withPayloadMessage(
                Message.newBuilder()
                    .setEventNotificationRequest(
                        Oslp.EventNotificationRequest.newBuilder()
                            .addNotifications(builder.build()))
                    .build())
            .build();

    this.send(request, settings);
  }

  @When("^the device sends multiple event notifications request to the platform over \"([^\"]*)\"$")
  public void theDeviceSendsMultipleEventNotificationsRequestToThePlatform(
      final String protocol, final Map<String, String> settings)
      throws IOException, DeviceSimulatorException {

    final Oslp.EventNotificationRequest.Builder requestBuilder =
        Oslp.EventNotificationRequest.newBuilder();
    final Oslp.EventNotification.Builder builder = Oslp.EventNotification.newBuilder();

    final String[]
        events =
            getString(settings, PlatformPubliclightingKeys.KEY_EVENTS)
                .split(PlatformPubliclightingKeys.SEPARATOR_COMMA),
        indexes =
            getString(settings, PlatformPubliclightingKeys.KEY_INDEXES)
                .split(PlatformPubliclightingKeys.SEPARATOR_COMMA);

    for (int i = 0; i < events.length; i++) {
      if (!events[i].isEmpty() && !indexes[i].isEmpty()) {
        builder.setEvent(Event.valueOf(events[i].trim()));

        final String indexValue = indexes[i];
        final Integer index =
            indexValue == null || "EMPTY".equals(indexValue) ? 0 : Integer.valueOf(indexValue);
        builder.setIndex(ByteString.copyFrom(new byte[] {index.byteValue()}));

        requestBuilder.addNotifications(builder.build());
      }
    }

    this.oslpMockServer.incrementSequenceNumber(this.getDeviceUid(settings));
    final OslpEnvelope request =
        this.createEnvelopeBuilder(
                getString(
                    settings,
                    PlatformPubliclightingKeys.KEY_DEVICE_UID,
                    PlatformPubliclightingDefaults.DEVICE_UID),
                this.oslpMockServer.getSequenceNumber(this.getDeviceUid(settings)))
            .withPayloadMessage(
                Message.newBuilder().setEventNotificationRequest(requestBuilder.build()).build())
            .build();

    this.send(request, settings);
  }

  /** Verify that we have received an event notification response over OSLP/OSLP ELSTER */
  @Then("^the event notification response contains$")
  public void theEventNotificationResponseContains(final Map<String, String> expectedResponse) {
    final Message responseMessage = this.oslpMockServer.waitForResponse();

    final EventNotificationResponse response = responseMessage.getEventNotificationResponse();

    assertThat(response.getStatus().name())
        .isEqualTo(getString(expectedResponse, PlatformPubliclightingKeys.KEY_STATUS));
  }

  /** Verify that we have received a set configuration response over OSLP/OSLP ELSTER */
  @Then("^the set configuration response contains$")
  public void theSetConfigurationResponseContains(final Map<String, String> expectedResponse) {
    final Message responseMessage = this.oslpMockServer.waitForResponse();

    final SetConfigurationResponse response = responseMessage.getSetConfigurationResponse();

    assertThat(response.getStatus().name())
        .isEqualTo(getString(expectedResponse, PlatformPubliclightingKeys.KEY_STATUS));
  }

  @Given(
      "^the device sends an event notification request with sequencenumber \"([^\"]*)\" to the platform over \"([^\"]*)\"$")
  public void theDeviceSendsAStartDeviceResponseOver(
      final Integer sequenceNumber, final String protocol, final Map<String, String> settings)
      throws IOException, DeviceSimulatorException {

    ScenarioContext.current()
        .put(PlatformPubliclightingKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER, sequenceNumber);

    this.theDeviceSendsAnEventNotificationRequestToThePlatform(protocol, settings);
  }

  /** Verify that we have received a response over OSLP/OSLP ELSTER */
  @Then("^the register device response contains$")
  public void theRegisterDeviceResponseContains(final Map<String, String> expectedResponse) {
    final Exception e = (Exception) ScenarioContext.current().get("Error");
    if (e == null || getString(expectedResponse, PlatformPubliclightingKeys.MESSAGE) == null) {
      final Message responseMessage = this.oslpMockServer.waitForResponse();

      final RegisterDeviceResponse response = responseMessage.getRegisterDeviceResponse();

      assertThat(response.getCurrentTime()).isNotNull();
      assertThat(response.getLocationInfo()).isNotNull();

      assertThat(response.getStatus().name())
          .isEqualTo(getString(expectedResponse, PlatformPubliclightingKeys.KEY_STATUS));
    } else {
      assertThat(e).hasMessage(getString(expectedResponse, PlatformPubliclightingKeys.MESSAGE));
    }
  }

  /** Verify that we have received a confirm register device response over OSLP/OSLP ELSTER */
  @Then("^the confirm register device response contains$")
  public void theConfirmRegisterDeviceResponseContains(final Map<String, String> expectedResponse) {
    final Exception e = (Exception) ScenarioContext.current().get("Error");
    if (e == null || getString(expectedResponse, PlatformPubliclightingKeys.MESSAGE) == null) {
      final Message responseMessage = this.oslpMockServer.waitForResponse();

      final Oslp.ConfirmRegisterDeviceResponse response =
          responseMessage.getConfirmRegisterDeviceResponse();
      Assert.assertNotNull(response);
      assertThat(response.getStatus().name())
          .isEqualTo(getString(expectedResponse, PlatformPubliclightingKeys.KEY_STATUS));
    } else {
      assertThat(e).hasMessage(getString(expectedResponse, PlatformPubliclightingKeys.MESSAGE));
    }
  }

  public OslpEnvelope.Builder createEnvelopeBuilder(
      final String deviceUid, final Integer sequenceNumber) {
    final byte[] sequenceNumberBytes = new byte[2];
    sequenceNumberBytes[0] = (byte) (sequenceNumber >>> 8);
    sequenceNumberBytes[1] = sequenceNumber.byteValue();

    return new OslpEnvelope.Builder()
        .withSignature(this.oslpMockServer.getOslpSignature())
        .withProvider(this.oslpMockServer.getOslpSignatureProvider())
        .withPrimaryKey(this.oslpMockServer.privateKey())
        .withDeviceId(Base64.decodeBase64(deviceUid))
        .withSequenceNumber(sequenceNumberBytes);
  }

  private OslpEnvelope send(final OslpEnvelope request, final Map<String, String> settings)
      throws IOException, DeviceSimulatorException {
    final String deviceIdentification =
        getString(settings, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION);
    final String hostname = this.configuration.getPlatform();

    // See PlatformPubliclightingKeys.KEY_PROTOCOL and
    // PlatformPubliclightingDefaults.DEFAULT_PROTOCOL when using the
    // 'Protocol' key value pair in the settings.

    final InetSocketAddress address =
        new InetSocketAddress(hostname, PlatformPubliclightingDefaults.OSLP_ELSTER_SERVER_PORT);
    return this.oslpMockServer.send(address, request, deviceIdentification);
  }

  /** Setup method to set the update firmware which should be returned by the mock. */
  @Given("^the device returns a update firmware \"([^\"]*)\" over \"([^\"]*)\"$")
  public void theDeviceReturnsAUpdateFirmwareOverOslp(final String result, final String protocol) {
    this.theSpecificDeviceReturnsAUpdateFirmwareOverOslp(
        result, protocol, this.getDeviceUid(new HashMap<>()));
  }

  @Given(
      "^the device returns a update firmware \"([^\"]*)\" over \"([^\"]*)\" with deviceUid \"([^\"]*)\"$")
  public void theSpecificDeviceReturnsAUpdateFirmwareOverOslp(
      final String result, final String protocol, final String deviceUid) {
    this.oslpMockServer.mockUpdateFirmwareResponse(deviceUid, Enum.valueOf(Status.class, result));
  }

  @Then("^an update firmware \"([^\"]*)\" message is sent to device \"([^\"]*)\"$")
  public void anUpdateFirmwareOslpMessageIsSentToTheDevice(
      final String protocol,
      final String deviceIdentification,
      final Map<String, String> expectedParameters)
      throws UnknownHostException, DeviceSimulatorException {
    final Message message =
        this.oslpMockServer.waitForRequest(
            this.getDeviceUid(expectedParameters), MessageType.UPDATE_FIRMWARE);
    assertThat(message).isNotNull();
    assertThat(message.hasUpdateFirmwareRequest()).isTrue();

    final UpdateFirmwareRequest request = message.getUpdateFirmwareRequest();

    // Check if the URL is equal to the file path as given by
    // 'firmware.path' property of OSGP.
    assertThat(request.getFirmwareUrl())
        .isEqualTo(
            getString(
                expectedParameters,
                PlatformPubliclightingKeys.FIRMWARE_URL,
                PlatformPubliclightingDefaults.FIRMWARE_URL));
  }

  private static String convertIpAddress(final ByteString byteString) {
    if (byteString == null || byteString.isEmpty()) {
      return "";
    }

    final StringBuilder stringBuilder = new StringBuilder();
    for (final byte number : byteString.toByteArray()) {
      int convertedNumber = number;
      if (number < 0) {
        convertedNumber = 256 + number;
      }
      final String str = String.valueOf(convertedNumber);
      stringBuilder.append(str).append(".");
    }
    final String ipValue = stringBuilder.toString();
    return ipValue.substring(0, ipValue.length() - 1);
  }

  private Map<String, String> setDeviceUid(
      final Map<String, String> settings, final String deviceUid) {
    settings.put(PlatformPubliclightingKeys.KEY_DEVICE_UID, deviceUid);
    return settings;
  }

  private String getDeviceUid(final Map<String, String> settings) {
    return getString(
        settings,
        PlatformPubliclightingKeys.KEY_DEVICE_UID,
        PlatformPubliclightingDefaults.DEVICE_UID);
  }
}
