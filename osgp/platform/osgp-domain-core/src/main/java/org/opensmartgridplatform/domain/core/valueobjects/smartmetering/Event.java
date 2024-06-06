// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

@Getter
@ToString
public class Event implements Serializable {

  private static final long serialVersionUID = 4482313912422705642L;
  private final DateTime timestamp;
  private final EventType eventType;
  private final Integer eventCounter;
  private final EventLogCategory eventLogCategory;
  private final List<EventDetail> eventDetails;

  public Event(
      final DateTime timestamp,
      final EventType eventType,
      final Integer eventCounter,
      final EventLogCategory eventLogCategory,
      final List<EventDetail> eventDetails) {
    this.timestamp = timestamp;
    this.eventType = eventType;
    this.eventCounter = eventCounter;
    this.eventLogCategory = eventLogCategory;
    this.eventDetails = eventDetails;
  }

  public Integer getEventCode() {
    return this.eventType.getEventCode();
  }
}
