// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ConfigurationFlag implements Serializable {

  private static final long serialVersionUID = -7943594696973940504L;

  private final ConfigurationFlagType configurationFlagType;

  private final boolean enabled;

  public ConfigurationFlag(
      final ConfigurationFlagType configurationFlagType, final boolean enabled) {
    this.configurationFlagType = configurationFlagType;
    this.enabled = enabled;
  }

  public ConfigurationFlagType getConfigurationFlagType() {
    return this.configurationFlagType;
  }

  public boolean isEnabled() {
    return this.enabled;
  }
}
