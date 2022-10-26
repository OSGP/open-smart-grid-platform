/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
