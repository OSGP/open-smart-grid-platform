//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

/** standardized units of measurement values */
public enum OsgpUnit {

  /** unit is not defined */
  UNDEFINED,
  /** cubic meter */
  M3,
  /** kilo watt hour */
  KWH;

  public static OsgpUnit forEnergyConsumption() {
    return KWH;
  }

  public static OsgpUnit forVolumeConsumption() {
    return M3;
  }
}
