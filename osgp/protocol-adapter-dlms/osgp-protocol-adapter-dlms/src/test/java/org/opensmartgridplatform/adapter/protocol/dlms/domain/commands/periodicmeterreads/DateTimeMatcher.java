//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.mockito.ArgumentMatcher;

public class DateTimeMatcher implements ArgumentMatcher<DateTime> {

  private final long time;

  public DateTimeMatcher(final long time) {
    this.time = time;
  }

  private long getActualTime(final DateTime actual) {
    return actual.getMillis();
  }

  @Override
  public boolean matches(final DateTime argument) {
    return this.getActualTime(argument) == this.time;
  }
}
