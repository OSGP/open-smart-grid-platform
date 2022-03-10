/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.support.ws.core.notification;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CoreNotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CoreNotificationService.class);

  private final BlockingQueue<Notification> queue = new LinkedBlockingQueue<>();
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public void handleNotification(
      final Notification notification, final String organisationIdentification) {
    LOGGER.info(
        "Notification received: {} for type {} with result {}",
        notification.getCorrelationUid(),
        notification.getNotificationType(),
        notification.getResult());
    this.queue.add(notification);
  }

  public boolean receivedNotification() {
    return !this.queue.isEmpty();
  }

  public Notification getNotification() {
    return this.queue.poll();
  }

  public void clearAllNotifications() {
    this.queue.clear();
  }

  public Notification getNotification(final long timeout, final TimeUnit unit) {
    try {
      return this.queue.poll(timeout, unit);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.trace("getNotification was interrupted", e);
    }
    return null;
  }

  public Notification getNotification(
      final String correlationUid, final long timeout, final TimeUnit unit) {
    final long maxTimeout = unit.toMillis(timeout);
    try {
      return CompletableFuture.supplyAsync(
              () -> {
                final Predicate<Notification> correlationUidEquals =
                    notification -> correlationUid.equals(notification.getCorrelationUid());
                return this.getNotification(correlationUidEquals, maxTimeout);
              })
          .get(maxTimeout, TimeUnit.MILLISECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.trace("getNotification for correlation UID {} was interrupted", correlationUid, e);
    } catch (final ExecutionException e) {
      LOGGER.error(
          "An exception occurred getting a notification for correlation UID {}", correlationUid, e);
    } catch (final TimeoutException e) {
      LOGGER.trace("getNotification for correlation UID {} timed out", correlationUid, e);
    }
    return null;
  }

  public Notification getNotification(
      final NotificationType notificationType, final long timeout, final TimeUnit unit) {
    final long maxTimeout = unit.toMillis(timeout);
    try {
      return CompletableFuture.supplyAsync(
              () -> {
                final Predicate<Notification> notificationTypeEquals =
                    notification -> notification.getNotificationType() == notificationType;
                return this.getNotification(notificationTypeEquals, maxTimeout);
              })
          .get(maxTimeout, TimeUnit.MILLISECONDS);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.trace("getNotification for notification type {} was interrupted", notificationType, e);
    } catch (final ExecutionException e) {
      LOGGER.error(
          "An exception occurred getting a notification for notification type {}",
          notificationType,
          e);
    } catch (final TimeoutException e) {
      LOGGER.trace("getNotification for notification type {} timed out", notificationType, e);
    }
    return null;
  }

  private Notification getNotification(
      final Predicate<Notification> predicate, final long maxTimeout) {
    final long startTime = System.currentTimeMillis();
    long remaining = maxTimeout;
    while (remaining > 0) {
      try {
        final Notification notification = this.queue.poll(remaining, TimeUnit.MILLISECONDS);
        if (notification != null) {
          if (predicate.test(notification)) {
            return notification;
          } else {
            this.putBackOnQueue(notification);
          }
        }
        final long elapsed = System.currentTimeMillis() - startTime;
        remaining = maxTimeout - elapsed;
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new CompletionException(e);
      }
    }
    return null;
  }

  private void putBackOnQueue(final Notification notification) {
    this.executor.schedule(() -> this.queue.add(notification), 1, TimeUnit.SECONDS);
  }
}
