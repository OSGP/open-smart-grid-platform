// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParserUtil {

  private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

  private DateTimeParserUtil() {
    // utility class
  }

  public static ZonedDateTime parseToZonedDateTime(final String datetimeString) {
    return ZonedDateTime.parse(datetimeString, DATE_TIME_FORMAT);
  }
}
