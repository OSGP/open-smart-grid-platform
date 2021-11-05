/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering;

import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.notification.NotificationService;
import org.opensmartgridplatform.cucumber.platform.support.ws.BaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class SmartMeteringBaseClient extends BaseClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringBaseClient.class);

  @Autowired private NotificationService notificationService;

  @Value("${smartmetering.response.wait.fail.duration:30000}")
  private int waitFailMillis;

  private int customWait = 0;

  public void setWaitFailMillis(final int waitFailMillis) {
    this.customWait = waitFailMillis;
  }

  private int getNextWait() {
    if (this.customWait > 0) {
      final int nextWait = this.customWait;
      this.customWait = 0;
      return nextWait;
    }
    return this.waitFailMillis;
  }

  protected void waitForNotification(final String correlationUid) {
    final int nextWait = this.getNextWait();
    LOGGER.info(
        "Waiting for a notification for correlation UID {} for at most {} milliseconds.",
        correlationUid,
        nextWait);

    final Notification notification =
        this.notificationService.getNotification(correlationUid, nextWait, TimeUnit.MILLISECONDS);

    if (notification == null) {
      throw new AssertionError(
          "Did not receive a notification for correlation UID: "
              + correlationUid
              + " within "
              + nextWait
              + " milliseconds");
    }
  }

  protected Notification waitForNotification(final NotificationType notificationType) {
    final int nextWait = this.getNextWait();
    final Notification notification = this.waitForNotification(notificationType, nextWait);
    if (notification == null) {
      throw new AssertionError(
          "Did not receive a notification for notification type: "
              + notificationType
              + " within "
              + nextWait
              + " milliseconds");
    }
    return notification;
  }

  protected Notification waitForNotification(
      final NotificationType notificationType, final int nextWait) {
    LOGGER.info(
        "Waiting for a notification for notification type {} for at most {} milliseconds.",
        notificationType,
        nextWait);

    final Notification notification =
        this.notificationService.getNotification(notificationType, nextWait, TimeUnit.MILLISECONDS);

    return notification;
  }

  protected Notification waitForNotification(final int nextWait) {
    LOGGER.info("Waiting for a notification for at most {} milliseconds.", nextWait);

    final Notification notification =
        this.notificationService.getNotification(nextWait, TimeUnit.MILLISECONDS);

    if (notification == null) {
      LOGGER.info("Did not receive a notification within " + nextWait + " milliseconds");
    }
    return notification;
  }
}
