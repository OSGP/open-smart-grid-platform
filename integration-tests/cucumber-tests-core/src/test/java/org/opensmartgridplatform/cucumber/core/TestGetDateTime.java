// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class TestGetDateTime {

  private final ZoneId cetTime = DateTimeHelper.getCentralEuropeanTimeZone();

  @Test
  public void testGetDateTime() {
    final ZonedDateTime nowPlus4 = ZonedDateTime.now(this.cetTime).plusMinutes(4);
    final ZonedDateTime nowPlus6 = ZonedDateTime.now(this.cetTime).plusMinutes(6);

    final ZonedDateTime dt = DateTimeHelper.getDateTime("now + 5 minutes");

    assertThat(dt).isStrictlyBetween(nowPlus4, nowPlus6);
  }
}
