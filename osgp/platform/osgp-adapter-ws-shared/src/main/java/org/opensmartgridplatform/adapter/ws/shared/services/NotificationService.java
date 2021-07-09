/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;

public interface NotificationService {

  void sendNotification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String result,
      final String correlationUid,
      final String message,
      final Object notificationType);

  default String getCustomTargetUri(
      final ApplicationDataLookupKey endpointLookupKey, final GenericNotification notification) {

    return null;
  }

  default void sendNotification(
      final ApplicationDataLookupKey endpointLookupKey, final GenericNotification notification) {

    /*
     * The sendNotification method with an application name has been added
     * to the notification service to be able to send notifications to one
     * of possibly multiple applications with an organization.
     *
     * For notification service implementations that have not transitioned
     * to accept the application name as input, stick with the earlier
     * notification behavior that is application agnostic.
     */
    this.sendNotification(
        endpointLookupKey.getOrganisationIdentification(),
        notification.getDeviceIdentification(),
        notification.getResult(),
        notification.getCorrelationUid(),
        notification.getMessage(),
        notification.getNotificationType());
  }
}
