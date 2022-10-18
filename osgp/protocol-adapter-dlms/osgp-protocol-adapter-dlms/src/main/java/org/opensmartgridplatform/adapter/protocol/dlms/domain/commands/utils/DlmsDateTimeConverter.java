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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

public class DlmsDateTimeConverter {
  private DlmsDateTimeConverter() {
    // Static class
  }

  public static final String UTC = "UTC";

  public static ZonedDateTime toZonedDateTime(final Date utcDateTime, final DlmsDevice device) {

    final ZonedDateTime utcZonedDateTime =
        ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of(UTC));

    return utcZonedDateTime.withZoneSameInstant(ZoneId.of(toTimeZone(device)));
  }

  public static DateTime toDateTime(final Date utcDateTime, final DlmsDevice device) {

    final ZonedDateTime convertedZoneDateTime = toZonedDateTime(utcDateTime, device);
    return new DateTime(
        convertedZoneDateTime.toInstant().toEpochMilli(), DateTimeZone.forID(toTimeZone(device)));
  }

  private static String toTimeZone(final DlmsDevice device) {
    return device.getTimezone() != null ? device.getTimezone() : UTC;
  }
}
