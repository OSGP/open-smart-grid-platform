//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class YearAdjuster extends CosemDateTimeAdjuster {

  public YearAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    int year = this.dateTime[0] << 8;
    year |= this.dateTime[1] & 0xff;
    if ((year & 0xFFFF) != 0xFFFF) {
      local = local.withYear(year);
    }

    return local;
  }
}
