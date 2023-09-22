// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EventDto implements Serializable {
  private static final long serialVersionUID = 5484936946786037616L;

  private final ZonedDateTime timestamp;
  private final Integer eventCode;
  private final Integer eventCounter;
  private final String eventLogCategoryName;
  private EventTypeDto eventTypeDto;
  private final List<EventDetailDto> eventDetails = new ArrayList<>();

  public EventDto(
      final ZonedDateTime timestamp,
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
}
