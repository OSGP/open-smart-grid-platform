// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Convert DateTime fields to the timezone defined within an e-meter DlmsDevice. So local times can
 * be used to define time ranges. This is a Utility tool that will be used within the
 * CommandExecutors. Meter reads with a timeframe on Kaifa meters use local time to determine the
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
   * @param timezone null or contains a timezone
   * @return ZonedDateTime within a timezone from a device or when device timezone is not defined
   *     then in UTC timezone
   */
  public static ZonedDateTime toZonedDateTime(final Date utcDateTime, final String timezone) {

    final ZonedDateTime utcZonedDateTime =
        ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of(UTC));

    return utcZonedDateTime.withZoneSameInstant(ZoneId.of(determineTimeZone(timezone)));
  }

  public static DateTime toDateTime(final DateTime utcDateTime, final String timezone) {
    return toDateTime(utcDateTime.toDate(), timezone);
  }

  /**
   * Convert a java.util.Date to a org.joda.time.DateTime with respect of the timezone. This is a
   * temporary convenience method to convert to joda times, because joda times should be refactored
   * to java time.
   *
   * @param utcDateTime a date time in UTC
   * @param timezone null or contains a timezone
   * @return DateTime within a timezone from a device or when device timezone is not defined then in
   *     UTC timezone
   */
  public static DateTime toDateTime(final Date utcDateTime, final String timezone) {

    final ZonedDateTime convertedZoneDateTime = toZonedDateTime(utcDateTime, timezone);
    return new DateTime(
        convertedZoneDateTime.toInstant().toEpochMilli(),
        DateTimeZone.forID(determineTimeZone(timezone)));
  }

  private static String determineTimeZone(final String timezone) {
    return timezone != null ? timezone : UTC;
  }

  public static ZonedDateTime now(final String timezone) {

    final ZoneId zoneId = ZoneId.of(determineTimeZone(timezone));

    return ZonedDateTime.now(ZoneOffset.UTC).withZoneSameInstant(zoneId);
  }
}
