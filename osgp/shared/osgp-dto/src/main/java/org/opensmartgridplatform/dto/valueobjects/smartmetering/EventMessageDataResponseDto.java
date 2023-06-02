//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
