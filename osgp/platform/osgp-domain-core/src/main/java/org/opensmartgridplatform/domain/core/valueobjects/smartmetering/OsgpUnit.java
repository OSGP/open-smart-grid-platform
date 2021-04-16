/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
