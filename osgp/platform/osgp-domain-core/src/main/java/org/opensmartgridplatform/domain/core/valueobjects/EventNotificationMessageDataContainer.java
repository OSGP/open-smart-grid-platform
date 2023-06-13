// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;

public class EventNotificationMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1860103408324944679L;

  private List<EventNotificationType> eventNotifications;

  public EventNotificationMessageDataContainer(
      final List<EventNotificationType> eventNotifications) {
    this.eventNotifications = eventNotifications;
  }

  public List<EventNotificationType> getEventNotifications() {
    return this.eventNotifications;
  }
}
