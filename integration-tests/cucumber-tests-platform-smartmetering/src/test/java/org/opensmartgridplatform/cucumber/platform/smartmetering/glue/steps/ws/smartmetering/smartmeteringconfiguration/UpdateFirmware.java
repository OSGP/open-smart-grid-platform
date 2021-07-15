/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.DeviceFirmwareModuleSteps;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.UpdateFirmwareRequestFactory;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Transactional(value = "txMgrCore")
public class UpdateFirmware {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private SmartMeteringConfigurationClient client;

  @Autowired private DeviceFirmwareModuleSteps deviceFirmwareModuleSteps;

  @When("^the request for a firmware upgrade is received$")
  public void theRequestForAFirmwareUpgradeIsReceived(final Map<String, String> settings)
      throws Throwable {

    final UpdateFirmwareRequest request = UpdateFirmwareRequestFactory.fromParameterMap(settings);
    final UpdateFirmwareAsyncResponse asyncResponse = this.client.updateFirmware(request);

    assertThat(asyncResponse).as("asyncResponse should not be null").isNotNull();
    ScenarioContextHelper.saveAsyncResponse(asyncResponse);
  }

  @Then("^retrieving the update firmware response results in an exception$")
  public void retrievingTheUpdateFirmwareResponseResultsInAnException() throws Throwable {

    final UpdateFirmwareAsyncRequest asyncRequest =
        UpdateFirmwareRequestFactory.fromScenarioContext();

    try {
      this.client.getUpdateFirmwareResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }

  @Then("^the update firmware result should be returned$")
  public void theUpdateFirmwareResultShouldBeReturned(final Map<String, String> settings)
      throws Throwable {

    final UpdateFirmwareAsyncRequest asyncRequest =
        UpdateFirmwareRequestFactory.fromParameterMapAsync(settings);
    final UpdateFirmwareResponse response = this.client.getUpdateFirmwareResponse(asyncRequest);

    assertThat(response.getResult()).as("result").isEqualTo(OsgpResultType.OK);

    final List<FirmwareVersion> expectedFirmareVersions =
        UpdateFirmwareRequestFactory.firmwareVersionsFromParameters(settings);

    final List<FirmwareVersion> actualFirmwareVersions = response.getFirmwareVersion();

    assertThat(actualFirmwareVersions.size())
        .as("number of firmware versions")
        .isEqualTo(expectedFirmareVersions.size());

    for (final FirmwareVersion expected : expectedFirmareVersions) {
      assertThat(this.firmwareVersionListContains(actualFirmwareVersions, expected))
          .as(
              "Firmware version not returned: "
                  + expected.getFirmwareModuleType()
                  + " => "
                  + expected.getVersion())
          .isTrue();
    }
  }

  private boolean firmwareVersionListContains(
      final List<FirmwareVersion> firmwareVersions, final FirmwareVersion expectedFirmwareVersion) {
    for (final FirmwareVersion firmwareVersion : firmwareVersions) {
      if (this.firmwareVersionEquals(firmwareVersion, expectedFirmwareVersion)) {
        return true;
      }
    }
    return false;
  }

  private boolean firmwareVersionEquals(final FirmwareVersion a, final FirmwareVersion b) {
    if (a == b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.getFirmwareModuleType().equals(b.getFirmwareModuleType())
        && a.getVersion().equals(b.getVersion());
  }

  @Then("^the database should be updated with the new device firmware$")
  public void theDatabaseShouldBeUpdatedWithTheNewDeviceFirmware(final Map<String, String> settings)
      throws Throwable {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
    final Device device =
        this.deviceRepository.findByDeviceIdentificationWithFirmware(deviceIdentification);
    assertThat(device).as("Device " + deviceIdentification + " not found.").isNotNull();

    final FirmwareFile activeFirmware = device.getActiveFirmwareFile();
    assertThat(activeFirmware)
        .as("No active firmware found for device " + deviceIdentification + ".")
        .isNotNull();

    /*
     * Check if the firmware module versions from the latest installed
     * firmware file match the expected versions. It is OK for a firmware
     * module not to be present in the firmware file, as it may have only a
     * subset of all possible modules.
     */
    if (activeFirmware.getModuleVersionComm() != null) {
      final String moduleVersionComm = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM);
      assertThat(activeFirmware.getModuleVersionComm())
          .as(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM)
          .isEqualTo(moduleVersionComm);
    }
    if (activeFirmware.getModuleVersionMa() != null) {
      final String moduleVersionMa = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA);
      assertThat(activeFirmware.getModuleVersionMa())
          .as(PlatformKeys.FIRMWARE_MODULE_VERSION_MA)
          .isEqualTo(moduleVersionMa);
    }
    if (activeFirmware.getModuleVersionFunc() != null) {
      final String moduleVersionFunc = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC);

      assertThat(activeFirmware.getModuleVersionFunc())
          .as(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC)
          .isEqualTo(moduleVersionFunc);
    }

    this.deviceFirmwareModuleSteps.theDatabaseShouldBeUpdatedWithTheDeviceFirmwareVersion(
        SettingsHelper.addDefault(settings, "FirmwareIsForSmartMeters", "true"));
  }

  @Then("^the database should not be updated with the new device firmware$")
  public void theDatabaseShouldNotBeUpdatedWithTheNewDeviceFirmware(
      final Map<String, String> settings) throws Throwable {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    assertThat(device).as("Device " + deviceIdentification + " not found.").isNotNull();

    final FirmwareFile activeFirmwareFile = device.getActiveFirmwareFile();
    if (activeFirmwareFile == null) {
      /*
       * The device has no active firmware in the database, so the
       * firmware from the settings has not been linked to the device.
       */
      return;
    }

    final String moduleVersionComm = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM);
    final String moduleVersionMa = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA);
    final String moduleVersionFunc = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC);

    assertThat(
            Objects.equals(moduleVersionComm, activeFirmwareFile.getModuleVersionComm())
                && Objects.equals(moduleVersionMa, activeFirmwareFile.getModuleVersionMa())
                && Objects.equals(moduleVersionFunc, activeFirmwareFile.getModuleVersionFunc()))
        .as(
            "Device "
                + deviceIdentification
                + " should not have firmware versions from the scenario after an unsuccessful update.")
        .isFalse();
  }
}
