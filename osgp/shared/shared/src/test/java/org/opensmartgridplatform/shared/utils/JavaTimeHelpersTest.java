package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

class JavaTimeHelpersTest {
  private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  @Test
  void shouldCropNanoToMillis() {
    final ZonedDateTime dateWithNano =
        ZonedDateTime.of(1998, 1, 24, 1, 1, 1, 123999999, ZoneId.systemDefault());
    final long croppedMillis = JavaTimeHelpers.getMillisFrom(dateWithNano);
    assertThat(croppedMillis).isEqualTo(123);
  }

  @Test
  void shouldFormatDatesTheSameAsJoda() {
    final Instant instant = Instant.ofEpochMilli(1000L);
    final Date date = Date.from(instant);

    final String formattedJoda = new DateTime(date).toString(this.DATE_TIME_FORMAT);
    final String formattedJava = JavaTimeHelpers.formatDate(date, this.DATE_TIME_FORMAT);

    assertThat(formattedJoda).isEqualTo(formattedJava);
  }
}
