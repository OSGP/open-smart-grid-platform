/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

/** An Enum used to filter on Device.isActivated. */
public enum DeviceInMaintenanceFilterType {
  BOTH(null),
  ACTIVE(false),
  IN_MAINTENANCE(true);

  private Boolean value;

  private DeviceInMaintenanceFilterType(final Boolean value) {
    this.value = value;
  }

  public Boolean getValue() {
    return this.value;
  }
}
