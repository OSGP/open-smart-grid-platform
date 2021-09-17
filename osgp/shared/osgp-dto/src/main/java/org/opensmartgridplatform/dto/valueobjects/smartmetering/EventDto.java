/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

@ToString
@Getter
public class EventDto implements Serializable {
  private static final long serialVersionUID = 5484936946786037616L;

  private final DateTime timestamp;
  private final Integer eventCode;
  private final Integer eventCounter;
  private final String eventLogCategoryName;
  private EventTypeDto eventTypeDto;
  private final List<EventDetailDto> eventDetails = new ArrayList<>();

  public EventDto(
      final DateTime timestamp,
      final Integer eventCode,
      final Integer eventCounter,
      final String eventLogCategoryName) {
    this.timestamp = timestamp;
    this.eventCode = eventCode;
    this.eventCounter = eventCounter;
    this.eventLogCategoryName = eventLogCategoryName;
  }

  public void setEventTypeDto(final EventTypeDto eventTypeDto) {
    this.eventTypeDto = eventTypeDto;
  }

  public void addEventDetail(final EventDetailDto eventDetail) {
    this.eventDetails.add(eventDetail);
  }

  public void addEventDetails(final List<EventDetailDto> eventDetails) {
    this.eventDetails.addAll(eventDetails);
  }

  public String getEventDetailValue(final EventDetailNameTypeDto eventDetailKey) {
    return this.eventDetails.stream()
        .filter(detail -> detail.getName().equals(eventDetailKey.name()))
        .map(EventDetailDto::getValue)
        .findFirst()
        .orElse(null);
  }
}
