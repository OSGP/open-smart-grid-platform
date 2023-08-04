// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.time.ZonedDateTime;

public class FindEventsRequestDto implements ActionRequestDto {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8250454329135950249L;

  private final EventLogCategoryDto eventLogCategory;
  private final ZonedDateTime from;
  private final ZonedDateTime until;

  public FindEventsRequestDto(
      final EventLogCategoryDto eventLogCategory,
      final ZonedDateTime from,
      final ZonedDateTime until) {
    this.eventLogCategory = eventLogCategory;
    this.from = from;
    this.until = until;
  }

  public EventLogCategoryDto getEventLogCategory() {
    return this.eventLogCategory;
  }

  public ZonedDateTime getFrom() {
    return this.from;
  }

  public ZonedDateTime getUntil() {
    return this.until;
  }
}
