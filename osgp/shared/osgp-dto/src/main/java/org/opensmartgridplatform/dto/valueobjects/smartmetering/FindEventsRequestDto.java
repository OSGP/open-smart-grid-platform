// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import org.joda.time.DateTime;

public class FindEventsRequestDto implements ActionRequestDto {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8250454329135950249L;

  private final EventLogCategoryDto eventLogCategory;
  private final DateTime from;
  private final DateTime until;

  public FindEventsRequestDto(
      final EventLogCategoryDto eventLogCategory, final DateTime from, final DateTime until) {
    this.eventLogCategory = eventLogCategory;
    this.from = from;
    this.until = until;
  }

  public EventLogCategoryDto getEventLogCategory() {
    return this.eventLogCategory;
  }

  public DateTime getFrom() {
    return this.from;
  }

  public DateTime getUntil() {
    return this.until;
  }
}
