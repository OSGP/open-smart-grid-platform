/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cucumber.java.en.When;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class NotificationSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSteps.class);

  @Autowired private NotificationService notificationService;

  @Value("${core.response.wait.fail.duration:30000}")
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

  @When("^the notification is received$")
  public void theNotificationIsReceived(final Map<String, String> settings) throws Throwable {
    final NotificationType notificationType =
        NotificationType.valueOf(settings.get(PlatformKeys.KEY_NOTIFICATION_TYPE));

    assertThat(notificationType)
        .as(PlatformKeys.KEY_NOTIFICATION_TYPE + " should not be null")
        .isNotNull();
    this.waitForNotification(notificationType);
  }
}
