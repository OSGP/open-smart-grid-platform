// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime2;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceFirmwareFileSteps {

  @Autowired private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  /**
   * Generic method which adds a device firmware using the settings.
   *
   * @param settings The settings for the device to be used.
   */
  @Given("^a device firmware$")
  public void aDeviceFirmware(final Map<String, String> settings) {

    // Get the device
    final Device device =
        this.deviceRepository.findByDeviceIdentification(
            getString(
                settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    // Get the firmware file
    final FirmwareFile firmwareFile =
        this.getFirmwareFile(getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME));

    final Instant installationDate =
        getDateTime2(
                getString(settings, PlatformKeys.FIRMWARE_INSTALLATION_DATE), ZonedDateTime.now())
            .toInstant();
    final String installedBy =
        getString(
            settings, PlatformKeys.FIRMWARE_INSTALLED_BY, PlatformDefaults.FIRMWARE_INSTALLED_BY);
    final DeviceFirmwareFile deviceFirmwareFile =
        new DeviceFirmwareFile(device, firmwareFile, installationDate, installedBy);

    this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
  }

  private FirmwareFile getFirmwareFile(final String firmwareFileName) {
    final FirmwareFile firmwareFile;
    if (StringUtils.isEmpty(firmwareFileName)) {
      final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository.findAll();
      firmwareFile = firmwareFiles.get(firmwareFiles.size() - 1);
    } else {
      final List<FirmwareFile> firmwareFiles =
          this.firmwareFileRepository.findByFilename(firmwareFileName);
      firmwareFile = firmwareFiles.get(0);
    }
    return firmwareFile;
  }

  @Then("^the device firmware file exists$")
  public void theDeviceFirmwareFileExists(final Map<String, String> settings) {
    final String deviceIdentification = settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    final FirmwareFile firmwareFile =
        this.getFirmwareFile(getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME));
    final DeviceFirmwareFile deviceFirmwareFile =
        Wait.untilAndReturn(
            () -> {
              final DeviceFirmwareFile entity =
                  this.deviceFirmwareFileRepository.findByDeviceAndFirmwareFile(
                      device, firmwareFile);
              if (entity == null) {
                throw new Exception("Device with identification [" + deviceIdentification + "]");
              }

              return entity;
            });

    assertThat(deviceFirmwareFile.getDevice().getDeviceIdentification())
        .isEqualTo(deviceIdentification);
  }
}
