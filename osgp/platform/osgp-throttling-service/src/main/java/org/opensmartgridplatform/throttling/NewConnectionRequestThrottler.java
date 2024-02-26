// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.throttling;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewConnectionRequestThrottler {
  private final Semaphore semaphore;
  private final int maxNewConnections;
  private final long maxWaitForNewConnectionsInMs;
  private final Timer resetNewConnectionRequestsTimer;

  public NewConnectionRequestThrottler(
      final int maxNewConnections,
      final long resetTimeInMs,
      final long maxWaitForNewConnectionsInMs) {
    this.maxNewConnections = maxNewConnections;
    this.maxWaitForNewConnectionsInMs = maxWaitForNewConnectionsInMs;

    this.semaphore = new Semaphore(this.maxNewConnections);

    this.resetNewConnectionRequestsTimer = new Timer();
    this.resetNewConnectionRequestsTimer.schedule(
        new ResetNewConnectionRequestsTask(), resetTimeInMs, resetTimeInMs);
  }

  public boolean isNewConnectionRequestAllowed(final int priority) {
    log.debug("isNewRequestAllowed: available = {} ", this.semaphore.availablePermits());
    try {
      if (!this.semaphore.tryAcquire(this.maxWaitForNewConnectionsInMs, TimeUnit.MILLISECONDS)) {
        log.debug(
            "isNewRequestAllowed: could not acquire permit for request with priority {} within {} ms",
            priority,
            this.maxWaitForNewConnectionsInMs);
        return false;
      }
      log.debug(
          "isNewRequestAllowed:  granted. available = {} ", this.semaphore.availablePermits());
    } catch (final InterruptedException e) {
      log.warn("isNewRequestAllowed: unable to acquire permit", e);
      Thread.currentThread().interrupt();
      return false;
    }
    return true;
  }

  private class ResetNewConnectionRequestsTask extends TimerTask {
    @Override
    public void run() {
      final int nrOfPermitsToBeReleased =
          NewConnectionRequestThrottler.this.maxNewConnections
              - NewConnectionRequestThrottler.this.semaphore.availablePermits();

      log.debug("releasing {} permits on newConnectionRequests semaphore", nrOfPermitsToBeReleased);

      NewConnectionRequestThrottler.this.semaphore.release(nrOfPermitsToBeReleased);

      log.debug(
          "ThrottlingService - Timer Reset and Unlocking, newConnectionRequests available = {}  ",
          NewConnectionRequestThrottler.this.semaphore.availablePermits());
    }
  }
}
