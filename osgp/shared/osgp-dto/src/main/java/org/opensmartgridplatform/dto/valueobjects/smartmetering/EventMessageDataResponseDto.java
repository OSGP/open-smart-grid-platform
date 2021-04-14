/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class EventMessageDataResponseDto extends ActionResponseDto {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6307300080258613848L;

  private List<EventDto> events;

  public EventMessageDataResponseDto(final List<EventDto> events) {
    this.events = events;
  }

  public List<EventDto> getEvents() {
    return this.events;
  }
}
