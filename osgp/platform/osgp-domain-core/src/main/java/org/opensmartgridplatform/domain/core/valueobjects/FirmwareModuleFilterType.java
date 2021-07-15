/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

/** An Enum used to filter on Device.firmwareModuleType. */
public enum FirmwareModuleFilterType {
  COMMUNICATION("communication_module_active_firmware"),
  FUNCTIONAL("functional"),
  MODULE_ACTIVE("module_active_firmware"),
  M_BUS("m_bus"),
  SECURITY("security"),
  ACTIVE_FIRMWARE("active_firmware");

  private String description;

  FirmwareModuleFilterType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public static FirmwareModuleFilterType fromValue(final String v) {
    return valueOf(v);
  }
}
