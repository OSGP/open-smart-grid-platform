// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

/** An Enum used to filter on Device managed externally or internally. */
public enum DeviceExternalManagedFilterType {
  BOTH(null),
  EXTERNAL_MANAGEMENT(true),
  INTERNAL_MANAGEMENT(false);

  private Boolean value;

  private DeviceExternalManagedFilterType(final Boolean value) {
    this.value = value;
  }

  public Boolean getValue() {
    return this.value;
  }
}
