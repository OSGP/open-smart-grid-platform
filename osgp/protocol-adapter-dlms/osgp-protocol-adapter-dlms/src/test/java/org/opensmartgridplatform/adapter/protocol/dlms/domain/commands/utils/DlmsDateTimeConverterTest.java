// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

@ExtendWith(MockitoExtension.class)
class DlmsDateTimeConverterTest {
  protected static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

  @Mock private DlmsDevice dlmsDevice;

  private final SimpleDateFormat utcSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

  @BeforeEach
  void setup() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    this.utcSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

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
    final Date date = this.utcSimpleDateFormat.parse(utcDateTime);
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

    final Date date = this.utcSimpleDateFormat.parse(utcDateTime);

    final DateTime dateTimeUtc = toDateTime(toDateTimeString, inputDeviceTimeZone);

    final DateTime dateTime = DlmsDateTimeConverter.toDateTime(date, this.dlmsDevice.getTimezone());

    Assertions.assertThat(dateTime).as(description).isEqualTo(dateTimeUtc);
  }

  private static DateTime toDateTime(
      final String toDateTimeString, final String inputDeviceTimeZone) {

    final Date expectedDate = new DateTime(toDateTimeString).toDate();
    return new DateTime(
        expectedDate,
        DateTimeZone.forID(inputDeviceTimeZone == null ? "UTC" : inputDeviceTimeZone));
  }
}
