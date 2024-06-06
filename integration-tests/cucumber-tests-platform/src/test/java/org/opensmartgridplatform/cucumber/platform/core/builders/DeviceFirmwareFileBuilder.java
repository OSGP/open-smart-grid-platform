// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.core.builders;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;

public class DeviceFirmwareFileBuilder implements CucumberBuilder<DeviceFirmwareFile> {

  private Instant installationDate = ZonedDateTime.now(ZoneId.of("UTC")).toInstant();
  private String installedBy = "test-org";
  private FirmwareFile firmwareFile;
  private Device device;

  public DeviceFirmwareFileBuilder withInstallationDate(final Instant installationDate) {
    this.installationDate = installationDate;
    return this;
  }

  public DeviceFirmwareFileBuilder withInstalledBy(final String installedBy) {
    this.installedBy = installedBy;
    return this;
  }

  public DeviceFirmwareFileBuilder withFirmwareFile(final FirmwareFile firmwareFile) {
    this.firmwareFile = firmwareFile;
    return this;
  }

  public DeviceFirmwareFileBuilder withDevice(final Device device) {
    this.device = device;
    return this;
  }

  @Override
  public DeviceFirmwareFile build() {
    return new DeviceFirmwareFile(
        this.device, this.firmwareFile, this.installationDate, this.installedBy);
  }

  @Override
  public DeviceFirmwareFileBuilder withSettings(final Map<String, String> inputSettings) {
    if (inputSettings.containsKey(PlatformKeys.DEVICEFIRMWARE_INSTALLATIONDATE)) {
      this.withInstallationDate(
          JavaTimeHelpers.parseToZonedDateTime(
                  inputSettings.get(PlatformKeys.DEVICEFIRMWARE_INSTALLATIONDATE))
              .toInstant());
    }

    if (inputSettings.containsKey(PlatformKeys.DEVICEFIRMWARE_INSTALLED_BY)) {
      this.withInstalledBy(inputSettings.get(PlatformKeys.DEVICEFIRMWARE_INSTALLED_BY));
    }

    return this;
  }
}
