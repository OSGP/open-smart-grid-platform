// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import java.time.ZonedDateTime;
import org.mockito.ArgumentMatcher;

public class DateTimeMatcher implements ArgumentMatcher<ZonedDateTime> {

  private final long time;

  public DateTimeMatcher(final long time) {
    this.time = time;
  }

  private long getActualTime(final ZonedDateTime actual) {
    return actual.toInstant().toEpochMilli();
  }

  @Override
  public boolean matches(final ZonedDateTime argument) {
    return this.getActualTime(argument) == this.time;
  }
}
