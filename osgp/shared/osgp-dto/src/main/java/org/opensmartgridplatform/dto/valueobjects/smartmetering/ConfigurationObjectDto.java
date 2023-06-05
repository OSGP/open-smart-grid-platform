// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationObjectDto implements Serializable {

  private static final long serialVersionUID = 2955060885937669868L;

  private GprsOperationModeTypeDto gprsOperationMode;

  private final ConfigurationFlagsDto configurationFlags;

  public ConfigurationObjectDto(
      final GprsOperationModeTypeDto gprsOperationMode,
      final ConfigurationFlagsDto configurationFlags) {
    this.gprsOperationMode = gprsOperationMode;
    this.configurationFlags = configurationFlags;
  }

  public ConfigurationObjectDto(final ConfigurationFlagsDto configurationFlags) {
    this.configurationFlags = configurationFlags;
  }

  @Override
  public String toString() {
    return String.format(
        "ConfigurationObjectDto[gprsOperationMode=%s, flags=%s]",
        this.gprsOperationMode, this.configurationFlags);
  }

  public GprsOperationModeTypeDto getGprsOperationMode() {
    return this.gprsOperationMode;
  }

  public ConfigurationFlagsDto getConfigurationFlags() {
    return this.configurationFlags;
  }
}
