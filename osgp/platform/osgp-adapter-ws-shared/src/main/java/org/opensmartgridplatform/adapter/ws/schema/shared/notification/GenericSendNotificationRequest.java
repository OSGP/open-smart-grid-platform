/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.schema.shared.notification;

public class GenericSendNotificationRequest {

  private final GenericNotification notification;

  public GenericSendNotificationRequest(final GenericNotification notification) {
    this.notification = notification;
  }

  /**
   * @return the notification
   */
  public GenericNotification getNotification() {
    return this.notification;
  }
}
