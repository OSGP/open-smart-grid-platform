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
import java.util.ArrayList;
import java.util.List;

public class ConfigurationFlagsDto implements Serializable {

  private static final long serialVersionUID = 8360475869038077578L;

  private final List<ConfigurationFlagDto> flags;

  public ConfigurationFlagsDto(final List<ConfigurationFlagDto> flags) {
    this.flags = new ArrayList<>(flags);
  }

  @Override
  public String toString() {
    return String.format("Flags %s", this.flags);
  }

  public List<ConfigurationFlagDto> getFlags() {
    return new ArrayList<>(this.flags);
  }
}
