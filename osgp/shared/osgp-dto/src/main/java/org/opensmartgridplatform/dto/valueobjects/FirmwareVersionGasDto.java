// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class FirmwareVersionGasDto implements Serializable {

  private static final long serialVersionUID = -8072852452663009458L;

  private final FirmwareModuleType firmwareModuleType;
  private final String version;
  private final String mbusDeviceIdentification;

  public FirmwareVersionGasDto(
      final FirmwareModuleType firmwareModuleType,
      final String version,
      final String mbusDeviceIdentification) {
    this.firmwareModuleType = firmwareModuleType;
    this.version = version;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  @Override
  public String toString() {
    return String.format(
        "FirmwareVersionDto[%s => %s, device %s]",
        this.firmwareModuleType, this.version, this.mbusDeviceIdentification);
  }
}
