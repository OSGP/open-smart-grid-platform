// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getNullOrNonEmptyString;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareModule;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceFirmwareModuleSteps {

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private FirmwareModuleRepository firmwareModuleRepository;
  @Autowired private DeviceFirmwareModuleRepository deviceFirmwareModuleRepository;

  @Given("^a device firmware version$")
  public void aDeviceWithDeviceFirmwareVersion(final Map<String, String> settings)
      throws Throwable {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final Device device =
        this.deviceRepository.findByDeviceIdentificationWithFirmwareModules(deviceIdentification);

    final Map<FirmwareModule, String> configuredVersionsPerType =
        this.getFirmwareModuleVersions(settings);

    for (final Map.Entry<FirmwareModule, String> configuredVersionsPerTypeEntry :
        configuredVersionsPerType.entrySet()) {
      final DeviceFirmwareModule deviceFirmwareModule =
          new DeviceFirmwareModule(
              device,
              configuredVersionsPerTypeEntry.getKey(),
              configuredVersionsPerTypeEntry.getValue());
      this.deviceFirmwareModuleRepository.save(deviceFirmwareModule);
    }
  }

  @Then("^the database should be updated with the device firmware version$")
  public void theDatabaseShouldBeUpdatedWithTheDeviceFirmwareVersion(
      final Map<String, String> settings) throws Throwable {

    final String deviceIdentification =
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final Device device =
        this.deviceRepository.findByDeviceIdentificationWithFirmwareModules(deviceIdentification);
    assertThat(device).as("Device " + deviceIdentification + " not found.").isNotNull();

    final Map<FirmwareModule, String> expectedVersionsByModule =
        this.getFirmwareModuleVersions(settings);

    final Map<FirmwareModule, String> actualVersionsByModule = device.getFirmwareVersions();

    assertThat(actualVersionsByModule)
        .as("Firmware module versions stored for " + deviceIdentification)
        .isEqualTo(expectedVersionsByModule);
  }

  public Map<FirmwareModule, String> getFirmwareModuleVersions(final Map<String, String> settings) {
    /*
     * The model for storing firmware module versions has changed from
     * firmware table columns to more flexible mappings for potentially more
     * types of firmware modules. In the earlier implementation the 'func'
     * version could refer to different firmware modules depending if smart
     * meters or other devices were involved.
     *
     * A cleaner way to integrate the new model for firmware version modules
     * in the test steps will have to be worked out, but for now a hack is
     * introduced to set "FirmwareIsForSmartMeters" to true in the settings
     * for smart meter firmware (see insertCoreFirmware in
     * org.opensmartgridplatform.cucumber.smartmetering.integration.glue.
     * steps. FirmwareSteps).
     */
    final boolean isForSmartMeters = getBoolean(settings, "FirmwareIsForSmartMeters", false);
    return this.getFirmwareModuleVersions(settings, isForSmartMeters);
  }

  public Map<FirmwareModule, String> getFirmwareModuleVersions(
      final Map<String, String> settings, final boolean isForSmartMeters) {

    final String comm;
    if (isForSmartMeters && settings.containsKey(FirmwareModuleData.MODULE_DESCRIPTION_COMM)) {
      comm = getNullOrNonEmptyString(settings, FirmwareModuleData.MODULE_DESCRIPTION_COMM, null);
    } else {
      comm = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_COMM, null);
    }
    final String func;
    if (isForSmartMeters
        && settings.containsKey(FirmwareModuleData.MODULE_DESCRIPTION_FUNC_SMART_METERING)) {
      func =
          getNullOrNonEmptyString(
              settings, FirmwareModuleData.MODULE_DESCRIPTION_FUNC_SMART_METERING, null);
    } else {
      func = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC, null);
    }
    final String ma;
    if (isForSmartMeters && settings.containsKey(FirmwareModuleData.MODULE_DESCRIPTION_MA)) {
      ma = getNullOrNonEmptyString(settings, FirmwareModuleData.MODULE_DESCRIPTION_MA, null);
    } else {
      ma = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MA, null);
    }
    final String mbus =
        getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS, null);
    final String sec =
        getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_SEC, null);
    final String mBusDriverActive =
        getNullOrNonEmptyString(
            settings, PlatformKeys.FIRMWARE_MODULE_VERSION_M_BUS_DRIVER_ACTIVE, null);
    final String simpleVersionInfo =
        getNullOrNonEmptyString(settings, PlatformKeys.SIMPLE_VERSION_INFO, null);

    final FirmwareModuleData firmwareModuleData =
        new FirmwareModuleData(comm, func, ma, mbus, sec, mBusDriverActive, simpleVersionInfo);
    return firmwareModuleData.getVersionsByModule(this.firmwareModuleRepository, isForSmartMeters);
  }
}
