// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.utils.datehelpers;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

public class DateHelperTest {

  @Test
  public void getDateFromStringEuropeParis() throws ParseException {
    final Date expected =
        this.getDate(
            "2023-02-15 04:13:38.123+0100",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            TimeZone.getTimeZone("Europe/Paris"));

    final String dateText = "2023-02-15T04:13:38.123+01:00[Europe/Paris]";
    final Date actual = DateHelper.dateFromIsoString(dateText);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void getDateFromStringUTC() throws ParseException {
    final Date expected =
        this.getDate(
            "2023-06-21 05:03:50.456Z", "yyyy-MM-dd HH:mm:ss.SSS", TimeZone.getTimeZone("UTC"));

    final String dateText = "2023-06-21T05:03:50.456Z";
    final Date actual = DateHelper.dateFromIsoString(dateText);

    assertThat(actual).isEqualTo(expected);
  }

  private Date getDate(final String dateString, final String pattern, final TimeZone timeZone)
      throws ParseException {
    final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    sdf.setTimeZone(timeZone);

    return sdf.parse(dateString);
  }
}
