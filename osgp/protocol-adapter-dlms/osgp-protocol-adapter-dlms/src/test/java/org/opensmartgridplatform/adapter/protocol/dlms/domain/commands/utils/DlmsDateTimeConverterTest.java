// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;

@ExtendWith(MockitoExtension.class)
class DlmsDateTimeConverterTest {
  protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  @Mock private DlmsDevice dlmsDevice;

  private final DateTimeFormatter utcSimpleDateFormat =
      DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of("UTC"));

  @ParameterizedTest
  @CsvSource({
    "utc to amsterdam summer time,Europe/Amsterdam,2022-10-18 22:00:00,2022-10-19T00:00:00+02:00[Europe/Amsterdam]",
    "utc to amsterdam winter time,Europe/Amsterdam,2022-10-30 23:00:00, 2022-10-31T00:00:00+01:00[Europe/Amsterdam]",
    "utc to utc winter time,null,2022-10-31 00:00:00, 2022-10-31T00:00:00+00:00[UTC]",
    "utc to utc summer time,null,2022-10-18 00:00:00, 2022-10-18T00:00:00+00:00[UTC]"
  })
  void toZonedDateTime_shouldConvertToDlmsDeviceTimeZone(
      final String description,
      final String deviceTimeZoneString,
      final String utcDateTime,
      final String toDateTimeString)
      throws ParseException {
    final String deviceTimeZone = "null".equals(deviceTimeZoneString) ? null : deviceTimeZoneString;
    final ZonedDateTime date = this.utcSimpleDateFormat.parse(utcDateTime, ZonedDateTime::from);
    final ZonedDateTime toDateTime = DateTimeParserUtil.parseToZonedDateTime(toDateTimeString);

    when(this.dlmsDevice.getTimezone()).thenReturn(deviceTimeZone);

    final ZonedDateTime zonedDateTime =
        DlmsDateTimeConverter.toZonedDateTime(date, this.dlmsDevice.getTimezone());

    Assertions.assertThat(zonedDateTime).as(description).isEqualTo(toDateTime);
  }

  @ParameterizedTest
  @CsvSource({
    "utc to amsterdam summer time,Europe/Amsterdam,2022-10-18 22:00:00,2022-10-19T00:00:00+02:00",
    "utc to amsterdam winter time,Europe/Amsterdam,2022-10-30 23:00:00, 2022-10-31T00:00:00+01:00",
    "utc to utc winter time,null,2022-10-31 00:00:00, 2022-10-31T00:00:00+00:00",
    "utc to utc summer time,null,2022-10-18 00:00:00, 2022-10-18T00:00:00+00:00"
  })
  void toDateTime_shouldConvertToDlmsDeviceTimeZone(
      final String description,
      final String deviceTimeZoneString,
      final String utcDateTime,
      final String toDateTimeString)
      throws ParseException {
    final String inputDeviceTimeZone =
        "null".equals(deviceTimeZoneString) ? null : deviceTimeZoneString;

    when(this.dlmsDevice.getTimezone()).thenReturn(inputDeviceTimeZone);

    final ZonedDateTime date = ZonedDateTime.parse(utcDateTime, this.utcSimpleDateFormat);

    final ZonedDateTime dateTimeUtc = toDateTime(toDateTimeString, inputDeviceTimeZone);

    final ZonedDateTime dateTime =
        DlmsDateTimeConverter.toZonedDateTime(date, this.dlmsDevice.getTimezone());

    Assertions.assertThat(dateTime).as(description).isEqualTo(dateTimeUtc);
  }

  private static ZonedDateTime toDateTime(
      final String toDateTimeString, final String inputDeviceTimeZone) {

    final ZonedDateTime expectedDate = JavaTimeHelpers.parseToZonedDateTime(toDateTimeString);
    return expectedDate.withZoneSameInstant(
        ZoneId.of(inputDeviceTimeZone == null ? "UTC" : inputDeviceTimeZone));
  }
}
