// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
