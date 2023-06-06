// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class EventNotificationMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4707772459625804068L;

  private List<EventNotificationTypeDto> eventNotifications;

  public EventNotificationMessageDataContainerDto(
      final List<EventNotificationTypeDto> eventNotifications) {
    this.eventNotifications = eventNotifications;
  }

  public List<EventNotificationTypeDto> getEventNotifications() {
    return this.eventNotifications;
  }
}
