/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
