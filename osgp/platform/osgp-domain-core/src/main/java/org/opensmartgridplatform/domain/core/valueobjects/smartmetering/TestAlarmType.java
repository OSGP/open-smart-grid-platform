/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum TestAlarmType {
  PARTIAL_POWER_OUTAGE,
  LAST_GASP;

  public String value() {
    return this.name();
  }

  public static TestAlarmType fromValue(final String v) {
    return valueOf(v);
  }
}
