/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
