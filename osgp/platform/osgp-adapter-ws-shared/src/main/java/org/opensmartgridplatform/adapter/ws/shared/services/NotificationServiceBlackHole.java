// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.services;

/** Notification service to be used when no notifications should be sent. */
public class NotificationServiceBlackHole implements NotificationService {

  @Override
  public void sendNotification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String result,
      final String correlationUid,
      final String message,
      final Object notificationType) {
    // This notification service does not send notifications, so this method
    // is empty.
  }
}
