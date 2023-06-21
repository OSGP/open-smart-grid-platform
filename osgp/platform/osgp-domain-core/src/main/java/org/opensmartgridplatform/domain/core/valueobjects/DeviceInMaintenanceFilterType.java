// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
