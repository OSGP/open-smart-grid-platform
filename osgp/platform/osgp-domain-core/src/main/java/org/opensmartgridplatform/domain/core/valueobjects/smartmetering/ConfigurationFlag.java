/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
