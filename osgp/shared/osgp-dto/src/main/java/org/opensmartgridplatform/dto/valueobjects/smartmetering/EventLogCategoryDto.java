/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import static org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto.EventLogDetailsType.COUNTER;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto.EventLogDetailsType.MAGNITUDE_AND_DURATION;
import static org.opensmartgridplatform.dto.valueobjects.smartmetering.EventLogCategoryDto.EventLogDetailsType.NONE;

public enum EventLogCategoryDto {
  STANDARD_EVENT_LOG(NONE),
  FRAUD_DETECTION_LOG(NONE),
  COMMUNICATION_SESSION_LOG(COUNTER),
  M_BUS_EVENT_LOG(NONE),
  POWER_QUALITY_EVENT_LOG(NONE),
  AUXILIARY_EVENT_LOG(NONE),
  POWER_QUALITY_EXTENDED_EVENT_LOG(MAGNITUDE_AND_DURATION);

  private final EventLogDetailsType detailsType;

  EventLogCategoryDto(final EventLogDetailsType detailsType) {
    this.detailsType = detailsType;
  }

  public EventLogDetailsType getDetailsType() {
    return this.detailsType;
  }

  public enum EventLogDetailsType {
    NONE(2),
    COUNTER(3),
    MAGNITUDE_AND_DURATION(4);

    private final int numberOfEventLogElements;

    EventLogDetailsType(final int numberOfEventLogElements) {
      this.numberOfEventLogElements = numberOfEventLogElements;
    }

    public int getNumberOfEventElements() {
      return this.numberOfEventLogElements;
    }
  }
}
