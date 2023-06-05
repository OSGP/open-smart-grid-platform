// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.SecurityKey;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.AddDeviceRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class AddDeviceSteps extends AbstractSmartMeteringSteps {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired private SmartMeteringInstallationClient smartMeteringInstallationClient;

  @When("^receiving a smartmetering add device request$")
  public void receivingASmartmeteringAddDeviceRequest(final Map<String, String> settings)
      throws Throwable {
    final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
    this.putKeyInScenarioContext(settings, PlatformKeys.KEY_DEVICE_MASTERKEY);
    this.putKeyInScenarioContext(settings, PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY);
    this.putKeyInScenarioContext(settings, PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY);
    this.putKeyInScenarioContext(settings, PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY);
    this.putKeyInScenarioContext(settings, PlatformSmartmeteringKeys.MBUS_P0_KEY);
    this.putKeyInScenarioContext(
        settings, PlatformSmartmeteringKeys.MBUS_FIRMWARE_UPDATE_AUTHENTICATION_KEY);
    this.putKeyInScenarioContext(settings, PlatformSmartmeteringKeys.MBUS_USER_KEY);

    final AddDeviceRequest request = AddDeviceRequestFactory.fromParameterMap(settings);
    final AddDeviceAsyncResponse asyncResponse =
        this.smartMeteringInstallationClient.addDevice(request);

    this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());

    assertThat(asyncResponse.getDeviceIdentification())
        .as("Device identification in response")
        .isEqualTo(deviceIdentification);
  }

  private void putKeyInScenarioContext(
      final Map<String, String> settings, final String platformKey) {
    final String inputKeyName = settings.get(platformKey);
    if (inputKeyName != null) {
      final SecurityKey securityKey = SecurityKey.valueOf(inputKeyName);
      ScenarioContext.current().put(platformKey, securityKey.getSoapRequestKey());
    }
  }

  @Then("^the add device response should be returned$")
  public void theAddDeviceResponseShouldBeReturned(final Map<String, String> responseParameters)
      throws Throwable {

    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final AddDeviceAsyncRequest addDeviceAsyncRequest =
        AddDeviceRequestFactory.fromParameterMapAsync(extendedParameters);

    final AddDeviceResponse response =
        this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }

  @Then("^retrieving the AddDevice response results in an exception$")
  public void retrievingTheAddDeviceResponseResultsInAnException()
      throws WebServiceSecurityException {

    final AddDeviceAsyncRequest addDeviceAsyncRequest =
        AddDeviceRequestFactory.fromScenarioContext();

    try {
      this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @Then("^a request to the device can be performed after activation$")
  public void aRequestToTheDeviceCanBePerformedAfterActivation() throws Throwable {

    final Device device = this.activateDevice();

    /*
     * Fire any request that causes communication to the device, and check
     * that the actual response is not an error.
     */

    final GetAdministrativeStatusRequest request = new GetAdministrativeStatusRequest();
    request.setDeviceIdentification(device.getDeviceIdentification());
    final GetAdministrativeStatusAsyncResponse getAdministrativeStatusAsyncResponse =
        this.smartMeteringConfigurationClient.getAdministrativeStatus(request);

    final GetAdministrativeStatusAsyncRequest asyncRequest =
        new GetAdministrativeStatusAsyncRequest();
    asyncRequest.setCorrelationUid(getAdministrativeStatusAsyncResponse.getCorrelationUid());
    asyncRequest.setDeviceIdentification(device.getDeviceIdentification());
    final GetAdministrativeStatusResponse getAdministrativeStatusResponse =
        this.smartMeteringConfigurationClient.retrieveGetAdministrativeStatusResponse(asyncRequest);

    assertThat(getAdministrativeStatusResponse.getEnabled())
        .as("Administrative status should contain information if it is enabled")
        .isNotNull();
  }

  private Device activateDevice() {
    final String deviceIdentification = this.getDeviceIdentificationFromContext();
    final Device device = this.findDeviceByDeviceIdentification(deviceIdentification);

    assertThat(device)
        .as("Device should be in the core database for identification " + deviceIdentification)
        .isNotNull();

    /*
     * The default result of adding a device through a service call is that
     * the device is configured to have a dynamic IP address to be obtained
     * from Jasper Wireless.
     */
    this.configureForCommunicationWithDeviceSimulator(device);

    return this.deviceRepository.save(device);
  }

  private void configureForCommunicationWithDeviceSimulator(final Device device) {
    /*
     * This call also sets the device to be IN_USE and activated.
     */
    device.updateRegistrationData(
        PlatformSmartmeteringDefaults.NETWORK_ADDRESS, device.getDeviceType());

    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(device.getDeviceIdentification());

    assertThat(dlmsDevice)
        .as(
            "Device should be in the DLMS protocol database for identification "
                + device.getDeviceIdentification())
        .isNotNull();

    dlmsDevice.setIpAddressIsStatic(true);
    dlmsDevice.setPort(PlatformSmartmeteringDefaults.PORT);
    this.dlmsDeviceRepository.save(dlmsDevice);
  }

  private String getDeviceIdentificationFromContext() {
    final String keyDeviceIdentification = PlatformKeys.KEY_DEVICE_IDENTIFICATION;
    final String deviceIdentification =
        (String) ScenarioContext.current().get(keyDeviceIdentification);

    assertThat(deviceIdentification)
        .as(
            "Device identification must be in the scenario context for key "
                + keyDeviceIdentification)
        .isNotNull();
    return deviceIdentification;
  }

  private Device findDeviceByDeviceIdentification(final String deviceIdentification) {
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    assertThat(device)
        .as("Device must exist for identification " + deviceIdentification)
        .isNotNull();
    return device;
  }
}
