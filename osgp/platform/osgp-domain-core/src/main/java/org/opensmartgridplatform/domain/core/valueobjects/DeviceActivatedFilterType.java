// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

/** An Enum used to filter on Device.isActivated. */
public enum DeviceActivatedFilterType {
  BOTH(null),
  ACTIVE(true),
  INACTIVE(false);

  private Boolean value;

  private DeviceActivatedFilterType(final Boolean value) {
    this.value = value;
  }

  public Boolean getValue() {
    return this.value;
  }
}
