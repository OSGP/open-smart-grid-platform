/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

public enum ReasonType {
  PERIODIC(1),
  BACKGROUND_SCAN(2),
  SPONTANEOUS(3),
  INTERROGATED_BY_STATION(4);

  private int reasonCode;

  private ReasonType(final int reasonCode) {
    this.reasonCode = reasonCode;
  }

  public int getReasonCode() {
    return this.reasonCode;
  }
}
