// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class HundredthsAdjuster extends CosemDateTimeAdjuster {

  private static final int HUNDREDTHS_TO_NANO_CONVERSION = 10000000;

  public HundredthsAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    if (this.dateTime[8] != (byte) 0xFF) {
      local = local.withNano(this.dateTime[8] / HUNDREDTHS_TO_NANO_CONVERSION);
    }

    return local;
  }
}
