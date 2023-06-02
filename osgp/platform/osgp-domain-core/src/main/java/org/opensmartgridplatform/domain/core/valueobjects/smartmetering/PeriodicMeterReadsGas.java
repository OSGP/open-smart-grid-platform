//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReadsGas extends MeterReadsGas {

  private static final long serialVersionUID = -7024520180322607341L;

  private final AmrProfileStatusCode amrProfileStatusCode;

  public PeriodicMeterReadsGas(
      final Date logTime, final OsgpMeterValue consumption, final Date captureTime) {
    super(logTime, consumption, captureTime);
    this.amrProfileStatusCode = null;
  }

  public PeriodicMeterReadsGas(
      final Date logTime,
      final OsgpMeterValue consumption,
      final Date captureTime,
      final AmrProfileStatusCode amrProfileStatusCode) {
    super(logTime, consumption, captureTime);
    this.amrProfileStatusCode = amrProfileStatusCode;
  }

  public AmrProfileStatusCode getAmrProfileStatusCode() {
    return this.amrProfileStatusCode;
  }
}
