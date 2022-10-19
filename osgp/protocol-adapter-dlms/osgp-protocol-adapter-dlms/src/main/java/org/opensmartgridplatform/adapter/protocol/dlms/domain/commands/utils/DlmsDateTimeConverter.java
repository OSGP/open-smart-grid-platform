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

/**
 * Convert DateTime fields to the timezone defined within an e-meter DlmsDevice. So local times can
 * be used to define time ranges. This is a Utility tool that will be used within the
 * CommandExecuters. Meter reads with a timeframe on Kaifa meters use local time to determine the
 * timeframe meter data returned.
 */
public class DlmsDateTimeConverter {
  private DlmsDateTimeConverter() {
    // Static class
  }

  public static final String UTC = "UTC";

  /**
   * Convert a java.util.Date to a java.time.ZonedDateTime with respect of timezone defined within a
   * DlmsDevice. If the timezone is not defined within a device a UTC timezone will be used as
   * fallback.
   *
   * @param utcDateTime a date time in UTC
   * @param device a device which does or does not contain a timezone
   * @return ZonedDateTime within a timezone from a device or when device timezone is not defined
   *     then in UTC timezone
   */
  public static ZonedDateTime toZonedDateTime(final Date utcDateTime, final DlmsDevice device) {

    final ZonedDateTime utcZonedDateTime =
        ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of(UTC));

    return utcZonedDateTime.withZoneSameInstant(ZoneId.of(determineTimeZone(device)));
  }

  /**
   * Convert a java.util.Date to a org.joda.time.DateTime with respect of the timezone defined
   * within a DlmsDevice. This is a temporary convenience method to convert to joda times, because
   * joda times should be refactored to java time.
   *
   * @param utcDateTime a date time in UTC
   * @param device a device which does or does not contain a timezone
   * @return DateTime within a timezone from a device or when device timezone is not defined then in
   *     UTC timezone
   */
  public static DateTime toDateTime(final Date utcDateTime, final DlmsDevice device) {

    final ZonedDateTime convertedZoneDateTime = toZonedDateTime(utcDateTime, device);
    return new DateTime(
        convertedZoneDateTime.toInstant().toEpochMilli(),
        DateTimeZone.forID(determineTimeZone(device)));
  }

  private static String determineTimeZone(final DlmsDevice device) {
    return device.getTimezone() != null ? device.getTimezone() : UTC;
  }
}
