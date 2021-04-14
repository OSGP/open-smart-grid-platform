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
