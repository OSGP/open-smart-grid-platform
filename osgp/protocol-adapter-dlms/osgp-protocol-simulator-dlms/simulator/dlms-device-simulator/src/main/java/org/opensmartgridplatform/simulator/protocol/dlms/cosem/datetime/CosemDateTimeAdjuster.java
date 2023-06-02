//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.temporal.TemporalAdjuster;

public abstract class CosemDateTimeAdjuster implements TemporalAdjuster {
  protected final byte[] dateTime;

  public CosemDateTimeAdjuster(final byte[] dateTime) {
    if (dateTime.length != 12) {
      throw new IllegalArgumentException("The Cosem Date-Time byte array must contain 12 bytes.");
    }
    this.dateTime = dateTime;
  }
}
