// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.core.builders.AddressBuilder;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the create organization requests steps */
public class CreateDeviceSteps {

  @Autowired private CoreDeviceInstallationClient client;

  @When("^receiving an add device request$")
  public void receivingAnAddDeviceRequest(final Map<String, String> settings) {
    final AddDeviceRequest request = new AddDeviceRequest();
    final Device device = this.createDevice(settings);
    request.setDevice(device);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.addDevice(request));
    } catch (final Exception ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @When("^receiving an add device request with an unknown organization$")
  public void receivingAnAddDeviceRequestWithAnUnknownOrganization(
      final Map<String, String> settings) {
    ScenarioContext.current()
        .put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");
    this.receivingAnAddDeviceRequest(settings);
  }

  /**
   * Verify the response of a add device request.
   *
   * @throws Throwable
   */
  @Then("^the add device response is successful$")
  public void theAddDeviceResponseIsSuccessful() {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof AddDeviceResponse)
        .isTrue();
  }

  @When("^receiving an update device request")
  public void receivingAnUpdateDeviceRequest(final Map<String, String> settings) {
    final UpdateDeviceRequest request = new UpdateDeviceRequest();

    String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    // Note: The regular expression below matches at spaces between two
    // quotation marks("), this check is used for a test with a
    // DeviceIdentification with only spaces
    if (deviceIdentification.matches("(?!\")\\s*(?=\")")) {
      deviceIdentification = deviceIdentification.replaceAll("\"", " ");
    }
    request.setDeviceIdentification(deviceIdentification);
    final Device device = this.createDevice(settings);
    request.setUpdatedDevice(device);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.updateDevice(request));
    } catch (final WebServiceSecurityException | SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  private Device createDevice(final Map<String, String> settings) {

    final Device device = new Device();
    device.setAlias(getString(settings, PlatformKeys.ALIAS, PlatformCommonDefaults.DEFAULT_ALIAS));
    device.setContainerAddress(new AddressBuilder().withSettings(settings).build());
    device.setDeviceIdentification(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    final DeviceModel deviceModel = new DeviceModel();
    deviceModel.setManufacturer(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_MODEL_MANUFACTURER,
            PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_MANUFACTURER));
    deviceModel.setModelCode(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
            PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
    device.setDeviceModel(deviceModel);
    device.setDeviceUid(
        getString(settings, PlatformKeys.KEY_DEVICE_UID, PlatformCommonDefaults.DEVICE_UID));
    device.setGpsLatitude(
        getFloat(settings, PlatformKeys.KEY_LATITUDE, PlatformCommonDefaults.DEFAULT_LATITUDE));
    device.setGpsLongitude(
        getFloat(settings, PlatformKeys.KEY_LONGITUDE, PlatformCommonDefaults.DEFAULT_LONGITUDE));
    device.setHasSchedule(
        getBoolean(
            settings, PlatformKeys.KEY_HAS_SCHEDULE, PlatformCommonDefaults.DEFAULT_HASSCHEDULE));
    device.setOwner(
        getString(settings, PlatformKeys.KEY_OWNER, PlatformCommonDefaults.DEFAULT_OWNER));
    device.setPublicKeyPresent(
        getBoolean(
            settings,
            PlatformKeys.KEY_PUBLICKEYPRESENT,
            PlatformCommonDefaults.DEFAULT_PUBLICKEYPRESENT));
    device.setActivated(
        getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformCommonDefaults.DEFAULT_ACTIVATED));

    return device;
  }

  /**
   * Verify the response of an update device request.
   *
   * @throws Throwable
   */
  @Then("^the update device response is successful$")
  public void theUpdateDeviceResponseIsSuccessful() {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof UpdateDeviceResponse)
        .isTrue();
  }

  /**
   * Verify that the create organization response contains the fault with the given expectedResult
   * parameters.
   *
   * @throws Throwable
   */
  @Then("^the add device response contains$")
  public void theAddDeviceResponseContains(final Map<String, String> expectedResult) {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof AddDeviceResponse)
        .isTrue();
  }

  @Then("^the add device response contains soap fault$")
  public void theAddDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  @Then("^the update device response contains$")
  public void theUpdateDeviceResponseContains(final Map<String, String> expectedResult) {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof UpdateDeviceResponse)
        .isTrue();
  }

  /**
   * Verify that the update device response contains the fault with the given expectedResult
   * parameters.
   *
   * @throws Throwable
   */
  @Then("^the update device response contains soap fault$")
  public void theUpdateDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
