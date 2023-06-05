// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.datetime;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class DeviationAdjuster extends CosemDateTimeAdjuster {

  public DeviationAdjuster(final byte[] dateTime) {
    super(dateTime);
  }

  @Override
  public Temporal adjustInto(final Temporal temporal) {
    LocalDateTime local = LocalDateTime.from(temporal);

    int deviation = this.dateTime[9] << 8;
    deviation |= this.dateTime[10] & 0xff;
    if ((deviation & 0xFFFF) != 0xFFFF) {
      local = local.plusMinutes(deviation);
    }

    return local;
  }
}
