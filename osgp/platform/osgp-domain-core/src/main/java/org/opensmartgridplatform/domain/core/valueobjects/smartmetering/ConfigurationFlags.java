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
import java.util.ArrayList;
import java.util.List;

public class ConfigurationFlags implements Serializable {

  private static final long serialVersionUID = 8360475869038077578L;

  private final List<ConfigurationFlag> flags;

  public ConfigurationFlags(final List<ConfigurationFlag> flags) {
    this.flags = new ArrayList<>(flags);
  }

  public List<ConfigurationFlag> getFlags() {
    return new ArrayList<>(this.flags);
  }
}
