// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class FirmwareVersionDto implements Serializable {
  private static final long serialVersionUID = 4842058824665590962L;

  private final FirmwareModuleType firmwareModuleType;
  private final String version;

  public FirmwareVersionDto(final FirmwareModuleType firmwareModuleType, final String version) {
    this.firmwareModuleType = firmwareModuleType;
    this.version = version;
  }

  @Override
  public String toString() {
    return String.format("FirmwareVersionDto[%s => %s]", this.firmwareModuleType, this.version);
  }

  public FirmwareModuleType getFirmwareModuleType() {
    return this.firmwareModuleType;
  }

  public String getVersion() {
    return this.version;
  }
}
