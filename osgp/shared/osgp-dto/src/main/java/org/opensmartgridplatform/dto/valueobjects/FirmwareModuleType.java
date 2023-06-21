// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.util.Arrays;
import java.util.stream.Stream;

public enum FirmwareModuleType {
  COMMUNICATION("COMMUNICATION_MODULE_ACTIVE_FIRMWARE"),
  FUNCTIONAL("Functional"),
  SECURITY("Security"),
  M_BUS("M-bus"),
  MODULE_ACTIVE("MODULE_ACTIVE_FIRMWARE"),
  ACTIVE_FIRMWARE("ACTIVE_FIRMWARE"),
  M_BUS_DRIVER_ACTIVE("M_BUS_DRIVER_ACTIVE_FIRMWARE"),
  SIMPLE_VERSION_INFO("SIMPLE_VERSION_INFO");

  private final String description;

  FirmwareModuleType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public static FirmwareModuleType forDescription(final String description) {
    final Stream<FirmwareModuleType> stream = Arrays.stream(FirmwareModuleType.values());
    return stream
        .filter(f -> f.getDescription().equalsIgnoreCase(description))
        .findFirst()
        .orElse(null);
  }
}
