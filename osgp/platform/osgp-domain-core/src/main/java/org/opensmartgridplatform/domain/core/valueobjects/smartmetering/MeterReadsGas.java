//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class MeterReadsGas extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717654L;

  private final Date logTime;
  private final Date captureTime;
  private final OsgpMeterValue consumption;

  public MeterReadsGas(
      final Date logTime, final OsgpMeterValue consumption, final Date captureTime) {
    this.logTime = new Date(logTime.getTime());
    this.captureTime = new Date(captureTime.getTime());
    this.consumption = consumption;
  }

  public Date getLogTime() {
    return new Date(this.logTime.getTime());
  }

  public Date getCaptureTime() {
    return new Date(this.captureTime.getTime());
  }

  public OsgpMeterValue getConsumption() {
    return this.consumption;
  }
}
