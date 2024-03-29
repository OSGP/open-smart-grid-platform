// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PublicLightingNotificationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingNotificationService.class);

  private final BlockingQueue<Notification> queue = new LinkedBlockingQueue<>();

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
                final long startTime = System.currentTimeMillis();
                long remaining = maxTimeout;
                while (remaining > 0) {
                  try {
                    final Notification notification =
                        this.queue.poll(remaining, TimeUnit.MILLISECONDS);
                    if (notification != null
                        && correlationUid.equals(notification.getCorrelationUid())) {
                      return notification;
                    }
                    final long elapsed = System.currentTimeMillis() - startTime;
                    remaining = maxTimeout - elapsed;
                  } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CompletionException(e);
                  }
                }
                return null;
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
}
