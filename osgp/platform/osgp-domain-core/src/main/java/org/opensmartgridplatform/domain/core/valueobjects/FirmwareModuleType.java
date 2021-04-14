/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

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

  public static FirmwareModuleType forDescription(final String description) {
    for (final FirmwareModuleType firmwareModuleType : values()) {
      if (description.equalsIgnoreCase(firmwareModuleType.getDescription())) {
        return firmwareModuleType;
      }
    }
    throw new IllegalArgumentException(
        "description does not occur in a FirmwareModuleType value: \"" + description + "\"");
  }

  public String getDescription() {
    return this.description;
  }
}
