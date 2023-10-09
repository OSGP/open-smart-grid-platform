// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
  public static ZonedDateTime toZonedDateTime(
      final ZonedDateTime utcDateTime, final String timezone) {

    final ZonedDateTime utcZonedDateTime =
        ZonedDateTime.ofInstant(utcDateTime.toInstant(), ZoneId.of(UTC));

    return utcZonedDateTime.withZoneSameInstant(ZoneId.of(determineTimeZone(timezone)));
  }

  public static ZonedDateTime toZonedDateTime(final Instant utcDateTime, final String timezone) {

    final ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(utcDateTime, ZoneId.of(UTC));

    return utcZonedDateTime.withZoneSameInstant(ZoneId.of(determineTimeZone(timezone)));
  }

  private static String determineTimeZone(final String timezone) {
    return timezone != null ? timezone : UTC;
  }

  public static ZonedDateTime now(final String timezone) {

    final ZoneId zoneId = ZoneId.of(determineTimeZone(timezone));

    return ZonedDateTime.now(ZoneOffset.UTC).withZoneSameInstant(zoneId);
  }
}
