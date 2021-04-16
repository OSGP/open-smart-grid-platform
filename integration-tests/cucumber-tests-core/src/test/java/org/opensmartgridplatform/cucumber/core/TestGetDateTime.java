/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class TestGetDateTime {

  private DateTimeZone cetTime = DateTimeHelper.getCentralEuropeanTimeZone();

  @Test
  public void testGetDateTime() {
    final DateTime nowPlus4 = new DateTime(this.cetTime).plusMinutes(4);
    final DateTime nowPlus6 = new DateTime(this.cetTime).plusMinutes(6);

    final DateTime dt = DateTimeHelper.getDateTime("now + 5 minutes");

    assertThat(dt).isStrictlyBetween(nowPlus4, nowPlus6);
  }
}
