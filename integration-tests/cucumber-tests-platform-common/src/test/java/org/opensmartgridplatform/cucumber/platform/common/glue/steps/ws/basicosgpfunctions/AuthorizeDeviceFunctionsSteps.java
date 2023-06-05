// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.basicosgpfunctions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.DeviceFunctionGroup;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreAdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the AuthorizeDeviceFunctions steps */
public class AuthorizeDeviceFunctionsSteps {

  @Autowired private AdminDeviceManagementClient adminDeviceManagementClient;

  @Autowired private CoreDeviceInstallationClient coreDeviceInstallationClient;

  @Autowired private CoreDeviceManagementClient coreDeviceManagementClient;

  @Autowired private CoreConfigurationManagementClient coreConfigurationManagementClient;

  @Autowired private CoreAdHocManagementClient coreAdHocManagementClient;

  @Autowired private CoreFirmwareManagementClient coreFirmwareManagementClient;

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctionsSteps.class);

  private DeviceFunction deviceFunction;
  private Exception exception;

  @When("receiving a set device authorization request")
  public void receivingADeviceAuthorizationRequest(final Map<String, String> requestParameters) {
    try {
      this.setDeviceAuthorization(requestParameters);
    } catch (final Exception e) {
      LOGGER.info("Exception: {}, message {}", e.getClass().getSimpleName(), e.getMessage());
      this.exception = e;
    }
  }

  @When("receiving a device function request")
  public void receivingADeviceFunctionRequest(final Map<String, String> requestParameters) {
    this.deviceFunction =
        getEnum(requestParameters, PlatformCommonKeys.DEVICE_FUNCTION, DeviceFunction.class);

    try {
      if (requestParameters.containsKey(PlatformCommonKeys.DELEGATE_FUNCTION_GROUP)) {
        this.findDeviceAuthorisations(requestParameters);
      } else {
        switch (this.deviceFunction) {
          case START_SELF_TEST:
            this.startSelfTest(requestParameters);
            break;
          case STOP_SELF_TEST:
            this.stopSelfTest(requestParameters);
            break;
          case GET_STATUS:
            this.getStatus(requestParameters);
            break;
          case GET_DEVICE_AUTHORIZATION:
            this.getDeviceAuthorization(requestParameters);
            break;
          case SET_DEVICE_AUTHORIZATION:
            this.setDeviceAuthorization(requestParameters);
            break;
          case SET_EVENT_NOTIFICATIONS:
            this.setEventNotifications(requestParameters);
            break;
          case GET_EVENT_NOTIFICATIONS:
            this.getEventNotifications(requestParameters);
            break;
          case UPDATE_FIRMWARE:
            this.updateFirmware(requestParameters);
            break;
          case GET_FIRMWARE_VERSION:
            this.getFirmwareVersion(requestParameters);
            break;
          case SET_CONFIGURATION:
            this.setConfiguration(requestParameters);
            break;
          case GET_CONFIGURATION:
            this.getConfiguration(requestParameters);
            break;
          case REMOVE_DEVICE:
            this.removeDevice(requestParameters);
            break;
          case SET_REBOOT:
            this.setReboot(requestParameters);
            break;
          case SET_COMMUNICATION_NETWORK_INFORMATION:
            this.setCommunicationNetworkInformation(requestParameters);
            break;
          default:
            throw new OperationNotSupportedException(
                "DeviceFunction " + this.deviceFunction + " does not exist.");
        }
      }
    } catch (final Exception e) {
      LOGGER.info("Exception: {}, message {}", e.getClass().getSimpleName(), e.getMessage());
      this.exception = e;
    }
  }

  @Then("the device function response is \"{}\"")
  public void theDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
    if (allowed) {
      final Object response = ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);
      assertThat(response)
          .as("Response is null, which indicates an exception occurred")
          .isNotNull();
      assertThat(response).isNotInstanceOf(SoapFaultClientException.class);
    } else {
      assertThat(this.exception).isNotNull();

      if (!this.exception.getMessage().equals("METHOD_NOT_ALLOWED_FOR_OWNER")) {
        assertThat(this.exception.getMessage()).isEqualTo("UNAUTHORIZED");
      }
    }
  }

  @Then("^device \"([^\"]*)\" has (\\d++) device authorizations$")
  public void deviceHasDeviceAuthorizations(
      final String deviceIdentification, final int expectedCountDeviceAuthorizations) {
    final FindDeviceAuthorisationsRequest findDeviceAuthorisationsRequest =
        new FindDeviceAuthorisationsRequest();
    findDeviceAuthorisationsRequest.setDeviceIdentification(deviceIdentification);

    Wait.until(
        () -> {
          FindDeviceAuthorisationsResponse response;
          try {
            response =
                this.adminDeviceManagementClient.findDeviceAuthorisations(
                    findDeviceAuthorisationsRequest);

            assertThat(response.getDeviceAuthorisations().size())
                .isEqualTo(expectedCountDeviceAuthorizations);
          } catch (final Exception e) {
            final String message =
                String.format(
                    "An exception occurred while retrieving the authorizations for %s",
                    deviceIdentification);
            LOGGER.warn(message, e);
            Assertions.fail(message);
          }
        });
  }

  private void findDeviceAuthorisations(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final FindDeviceAuthorisationsRequest findDeviceAuthorisationsRequest =
        new FindDeviceAuthorisationsRequest();
    findDeviceAuthorisationsRequest.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    final FindDeviceAuthorisationsResponse response =
        this.adminDeviceManagementClient.findDeviceAuthorisations(findDeviceAuthorisationsRequest);

    final UpdateDeviceAuthorisationsRequest updateDeviceAuthorisationsRequest =
        new UpdateDeviceAuthorisationsRequest();

    final DeviceAuthorisation deviceAuthorisation = response.getDeviceAuthorisations().get(0);
    deviceAuthorisation.setFunctionGroup(
        getEnum(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_FUNCTION_GROUP,
            DeviceFunctionGroup.class));

    updateDeviceAuthorisationsRequest.getDeviceAuthorisations().add(deviceAuthorisation);

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.adminDeviceManagementClient.updateDeviceAuthorisations(
                updateDeviceAuthorisationsRequest));
  }

  private void startSelfTest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final StartDeviceTestRequest request = new StartDeviceTestRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreDeviceInstallationClient.startDeviceTest(request));
  }

  private void stopSelfTest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final StopDeviceTestRequest request = new StopDeviceTestRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE, this.coreDeviceInstallationClient.stopDeviceTest(request));
  }

  private void getStatus(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation
                .GetStatusRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(PlatformCommonKeys.RESPONSE, this.coreDeviceInstallationClient.getStatus(request));
  }

  private void getDeviceAuthorization(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final FindDeviceAuthorisationsRequest request = new FindDeviceAuthorisationsRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.adminDeviceManagementClient.findDeviceAuthorisations(request));
  }

  private void setEventNotifications(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final SetEventNotificationsRequest request = new SetEventNotificationsRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreDeviceManagementClient.setEventNotifications(request));
  }

  private void getEventNotifications(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final FindEventsRequest request = new FindEventsRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreDeviceManagementClient.findEventsResponse(request));
  }

  private void updateFirmware(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final UpdateFirmwareRequest request = new UpdateFirmwareRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setFirmwareIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_FIRMWARE_IDENTIFICATION,
            PlatformCommonDefaults.FIRMWARE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE, this.coreFirmwareManagementClient.updateFirmware(request));
  }

  private void getFirmwareVersion(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final GetFirmwareVersionRequest request = new GetFirmwareVersionRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreFirmwareManagementClient.getFirmwareVersion(request));
  }

  private void setConfiguration(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final SetConfigurationRequest request = new SetConfigurationRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    final Configuration config = new Configuration();

    config.setLightType(PlatformCommonDefaults.CONFIGURATION_LIGHTTYPE);
    config.setPreferredLinkType(PlatformCommonDefaults.CONFIGURATION_PREFERRED_LINKTYPE);

    request.setConfiguration(config);

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreConfigurationManagementClient.setConfiguration(request));
  }

  private void getConfiguration(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final GetConfigurationRequest request = new GetConfigurationRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.coreConfigurationManagementClient.getConfiguration(request));
  }

  private void removeDevice(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final RemoveDeviceRequest request = new RemoveDeviceRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(PlatformCommonKeys.RESPONSE, this.adminDeviceManagementClient.removeDevice(request));
  }

  private void setReboot(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final SetRebootRequest request = new SetRebootRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    ScenarioContext.current()
        .put(PlatformCommonKeys.RESPONSE, this.coreAdHocManagementClient.setReboot(request));
  }

  private void setDeviceAuthorization(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final UpdateDeviceAuthorisationsRequest request = new UpdateDeviceAuthorisationsRequest();
    final DeviceAuthorisation deviceAuthorisation = new DeviceAuthorisation();
    deviceAuthorisation.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    deviceAuthorisation.setOrganisationIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.DELEGATE_ORGANIZATION_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DELEGATE_ORGANIZATION_IDENTIFICATION));

    final String functionGroupString =
        getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_FUNCTION_GROUP);
    final DeviceFunctionGroup deviceFunctionGroup =
        DeviceFunctionGroup.fromValue(functionGroupString);
    deviceAuthorisation.setFunctionGroup(deviceFunctionGroup);

    deviceAuthorisation.setRevoked(
        getBoolean(
            requestParameters, PlatformCommonKeys.KEY_REVOKED, PlatformCommonDefaults.REVOKED));
    request.getDeviceAuthorisations().add(deviceAuthorisation);
    ScenarioContext.current()
        .put(
            PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
            getString(
                requestParameters,
                PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.adminDeviceManagementClient.updateDeviceAuthorisations(request));
  }

  private void setCommunicationNetworkInformation(final Map<String, String> requestParameters)
      throws WebServiceSecurityException {
    final SetCommunicationNetworkInformationRequest request =
        new SetCommunicationNetworkInformationRequest();
    request.setDeviceIdentification(PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    request.setBtsId(PlatformDefaults.DEFAULT_BTS_ID);
    request.setCellId(PlatformDefaults.DEFAULT_CELL_ID);
    ScenarioContext.current()
        .put(
            PlatformCommonKeys.RESPONSE,
            this.adminDeviceManagementClient.setCommunicationNetworkInformation(request));
  }
}
