// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationFlagDto implements Serializable {

  private static final long serialVersionUID = -7943594696973940504L;

  private final ConfigurationFlagTypeDto configurationFlagType;

  private final boolean enabled;

  public ConfigurationFlagDto(
      final ConfigurationFlagTypeDto configurationFlagType, final boolean enabled) {
    this.configurationFlagType = configurationFlagType;
    this.enabled = enabled;
  }

  @Override
  public String toString() {
    return String.format(
        "Flag[%s %s]", this.configurationFlagType, this.enabled ? "enabled" : "disabled");
  }

  public ConfigurationFlagTypeDto getConfigurationFlagType() {
    return this.configurationFlagType;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}
