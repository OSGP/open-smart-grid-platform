// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
