/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
