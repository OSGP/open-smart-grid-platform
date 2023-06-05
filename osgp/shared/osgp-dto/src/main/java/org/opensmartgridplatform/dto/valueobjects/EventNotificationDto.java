// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class EventNotificationDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5665838352689024852L;

  private final String deviceUid;
  private final DateTime dateTime;
  private final EventTypeDto eventType;
  private final String description;
  private final Integer index;

  public EventNotificationDto(
      final String deviceUid,
      final DateTime dateTime,
      final EventTypeDto eventType,
      final String description,
      final Integer index) {
    this.deviceUid = deviceUid;
    this.dateTime = dateTime;
    this.eventType = eventType;
    this.description = description;
    this.index = index;
  }

  @Override
  public String toString() {
    return String.format(
        "EventNotificationDto[deviceUid=%s, dateTime=%s, eventType=%s, index=%s, description=%s]",
        this.deviceUid, this.dateTime, this.eventType, this.index, this.description);
  }

  public String getDeviceUid() {
    return this.deviceUid;
  }

  public DateTime getDateTime() {
    return this.dateTime;
  }

  public EventTypeDto getEventType() {
    return this.eventType;
  }

  public String getDescription() {
    return this.description;
  }

  public Integer getIndex() {
    return this.index;
  }
}
