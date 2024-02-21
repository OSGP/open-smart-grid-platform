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
  private final int maxNewConnectionRequests;
  private final long resetTimeInMs;
  private final long maxWaitForNewConnectionRequestInMs;
  private final Timer resetNewConnectionRequestsTimer;

  public NewConnectionRequestThrottler(
      final int maxNewConnectionRequests,
      final long resetTimeInMs,
      final long maxWaitForNewConnectionRequestInMs) {
    this.maxNewConnectionRequests = maxNewConnectionRequests;
    this.resetTimeInMs = resetTimeInMs;
    this.maxWaitForNewConnectionRequestInMs = maxWaitForNewConnectionRequestInMs;

    this.semaphore = new Semaphore(maxNewConnectionRequests);

    this.resetNewConnectionRequestsTimer = new Timer();
    this.resetNewConnectionRequestsTimer.schedule(
        new ResetNewConnectionRequestsTask(), this.resetTimeInMs, this.resetTimeInMs);
  }

  public boolean isNewConnectionRequestAllowed(final int priority) {
    log.debug("isNewRequestAllowed: available = {} ", this.semaphore.availablePermits());
    try {
      if (!this.semaphore.tryAcquire(
          this.maxWaitForNewConnectionRequestInMs, TimeUnit.MILLISECONDS)) {
        log.debug(
            "isNewRequestAllowed: could not acquire permit for request with priority {} within {} ms",
            priority,
            this.maxWaitForNewConnectionRequestInMs);
        return false;
      }
      log.debug(
          "isNewRequestAllowed:  granted. available = {} ", this.semaphore.availablePermits());
    } catch (final InterruptedException e) {
      log.warn("isNewRequestAllowed: unable to acquire permit", e);
      Thread.currentThread().interrupt();
    }
    return true;
  }

  private class ResetNewConnectionRequestsTask extends TimerTask {
    @Override
    public void run() {
      final int nrOfPermitsToBeReleased =
          NewConnectionRequestThrottler.this.maxNewConnectionRequests
              - NewConnectionRequestThrottler.this.semaphore.availablePermits();

      log.debug("releasing {} permits on newConnectionRequests semaphore", nrOfPermitsToBeReleased);

      NewConnectionRequestThrottler.this.semaphore.release(nrOfPermitsToBeReleased);

      log.debug(
          "ThrottlingService - Timer Reset and Unlocking, newConnectionRequests available = {}  ",
          NewConnectionRequestThrottler.this.semaphore.availablePermits());
    }
  }
}
