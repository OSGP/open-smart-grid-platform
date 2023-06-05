// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.util;

import java.time.ZoneId;
import java.util.Calendar;
import org.openmuc.jdlms.datatypes.CosemDateTime;

public class CosemDateTimeUtil {

  private CosemDateTimeUtil() {
    // static class
  }

  public static CosemDateTime toCosemDateTime(final Calendar cal) {
    return new CosemDateTime(
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH),
        0xff,
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        cal.get(Calendar.SECOND),
        0,
        calculateDeviation(cal));
  }

  public static int calculateDeviation(final Calendar cal) {
    return ZoneId.of(cal.getTimeZone().getID())
            .getRules()
            .getOffset(cal.toInstant())
            .getTotalSeconds()
        / 60
        * -1;
  }
}
