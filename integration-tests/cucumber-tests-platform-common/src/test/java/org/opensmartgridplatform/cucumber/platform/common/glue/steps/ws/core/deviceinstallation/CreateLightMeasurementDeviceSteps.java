// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddLightMeasurementDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddLightMeasurementDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LightMeasurementDevice;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateLightMeasurementDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateLightMeasurementDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.core.builders.AddressBuilder;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateLightMeasurementDeviceSteps {

  @Autowired private CoreDeviceInstallationClient client;

  @When("^receiving an add light measurement device request$")
  public void receivingAnAddLightMeasurementDeviceRequest(final Map<String, String> settings) {
    final AddLightMeasurementDeviceRequest request = new AddLightMeasurementDeviceRequest();
    final LightMeasurementDevice lmd = this.createLightMeasurementDevice(settings);
    request.setLightMeasurementDevice(lmd);

    try {
      ScenarioContext.current()
          .put(PlatformKeys.RESPONSE, this.client.addLightMeasurementDevice(request));
    } catch (final Exception ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @When("^receiving an add light measurement device request with an unknown organization$")
  public void receivingAnAddLightMeasurementDeviceRequestWithAnUnknownOrganization(
      final Map<String, String> settings) {
    ScenarioContext.current()
        .put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");
    this.receivingAnAddLightMeasurementDeviceRequest(settings);
  }

  @When("^receiving an update light measurement device request$")
  public void receivingAnUpdateLightMeasurementDeviceRequest(final Map<String, String> settings) {
    final UpdateLightMeasurementDeviceRequest request = new UpdateLightMeasurementDeviceRequest();
    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    request.setDeviceIdentification(deviceIdentification);

    final LightMeasurementDevice lmd = this.createLightMeasurementDevice(settings);
    request.setUpdatedLightMeasurementDevice(lmd);

    try {
      ScenarioContext.current()
          .put(PlatformKeys.RESPONSE, this.client.updateLightMeasurementDevice(request));
    } catch (final Exception ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("^the add light measurement device response is successful$")
  public void theAddLightMeasurementDeviceResponseIsSuccessful() {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE))
        .isInstanceOf(AddLightMeasurementDeviceResponse.class);
  }

  @Then("^the add light measurement device response contains soap fault$")
  public void theAddLightMeasurementDeviceResponseContainsSoapFault(
      final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  @Then("^the update light measurement device response is successful$")
  public void theUpdateLightMeasurementDeviceResponseIsSuccessful() {
    assertThat(ScenarioContext.current().get(PlatformKeys.RESPONSE))
        .isInstanceOf(UpdateLightMeasurementDeviceResponse.class);
  }

  @Then("^the update light measurement device response contains soap fault$")
  public void theUpdateLightMeasurementDeviceResponseContainsSoapFault(
      final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  private LightMeasurementDevice createLightMeasurementDevice(final Map<String, String> settings) {
    final LightMeasurementDevice lmd = new LightMeasurementDevice();
    lmd.setAlias(getString(settings, PlatformKeys.ALIAS, PlatformCommonDefaults.DEFAULT_ALIAS));
    lmd.setContainerAddress(new AddressBuilder().withSettings(settings).build());
    lmd.setDeviceIdentification(
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
    lmd.setDeviceModel(deviceModel);
    lmd.setGpsLatitude(
        getFloat(settings, PlatformKeys.KEY_LATITUDE, PlatformCommonDefaults.DEFAULT_LATITUDE));
    lmd.setGpsLongitude(
        getFloat(settings, PlatformKeys.KEY_LONGITUDE, PlatformCommonDefaults.DEFAULT_LONGITUDE));
    lmd.setOwner(getString(settings, PlatformKeys.KEY_OWNER, PlatformCommonDefaults.DEFAULT_OWNER));
    lmd.setActivated(
        getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformCommonDefaults.DEFAULT_ACTIVATED));
    lmd.setDescription(
        getString(
            settings,
            PlatformKeys.KEY_LMD_DESCRIPTION,
            PlatformCommonDefaults.DEFAULT_LMD_DESCRIPTION));
    lmd.setCode(
        getString(settings, PlatformKeys.KEY_LMD_CODE, PlatformCommonDefaults.DEFAULT_LMD_CODE));
    lmd.setColor(
        getString(settings, PlatformKeys.KEY_LMD_COLOR, PlatformCommonDefaults.DEFAULT_LMD_COLOR));
    lmd.setDigitalInput(
        getShort(
            settings,
            PlatformKeys.KEY_LMD_DIGITAL_INPUT,
            PlatformCommonDefaults.DEFAULT_LMD_DIGITAL_INPUT));
    return lmd;
  }
}
