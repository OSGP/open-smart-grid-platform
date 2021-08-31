/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum EventLogCategoryDto {
  STANDARD_EVENT_LOG(2),
  FRAUD_DETECTION_LOG(2),
  COMMUNICATION_SESSION_LOG(3),
  M_BUS_EVENT_LOG(2),
  POWER_QUALITY_EVENT_LOG(2),
  AUXILIARY_EVENT_LOG(2);

  private final int numberOfEventLogElements;

  EventLogCategoryDto(final int numberOfEventLogElements) {
    this.numberOfEventLogElements = numberOfEventLogElements;
  }

  public int getNumberOfEventElements() {
    return this.numberOfEventLogElements;
  }
}
