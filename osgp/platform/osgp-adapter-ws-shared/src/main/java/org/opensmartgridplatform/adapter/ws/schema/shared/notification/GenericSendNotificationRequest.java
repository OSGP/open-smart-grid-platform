// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
