//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingServiceEnabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(ThrottlingServiceEnabledCondition.class)
public class ThrottlingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingService.class);

  @Value("${throttling.max.open.connections}")
  private int maxOpenConnections;

  @Value("${throttling.max.new.connection.requests}")
  private int maxNewConnectionRequests;

  @Value("${throttling.reset.time}")
  private int resetTime;

  private Semaphore openConnectionsSemaphore;
  private Semaphore newConnectionRequestsSemaphore;
  private Timer resetTimer;
  private ReentrantLock resetTimerLock;

  @PostConstruct
  public void postConstruct() {

    this.openConnectionsSemaphore = new Semaphore(this.maxOpenConnections);
    this.newConnectionRequestsSemaphore = new Semaphore(this.maxNewConnectionRequests);

    this.resetTimerLock = new ReentrantLock();

    this.resetTimer = new Timer();
    this.resetTimer.scheduleAtFixedRate(new ResetTask(), this.resetTime, this.resetTime);

    LOGGER.info("Initialized ThrottlingService. {}", this);
  }

  @PreDestroy
  public void preDestroy() {
    if (this.resetTimer != null) {
      this.resetTimer.cancel();
    }
  }

  public void openConnection() {

    this.newConnectionRequest();

    LOGGER.debug(
        "Requesting openConnection. available = {} ",
        this.openConnectionsSemaphore.availablePermits());

    try {
      this.openConnectionsSemaphore.acquire();
      LOGGER.debug(
          "openConnection granted. available = {} ",
          this.openConnectionsSemaphore.availablePermits());
    } catch (final InterruptedException e) {
      LOGGER.warn("Unable to acquire Open Connection", e);
      Thread.currentThread().interrupt();
    }
  }

  public void closeConnection() {

    LOGGER.debug(
        "closeConnection(). available = {} ", this.openConnectionsSemaphore.availablePermits());
    this.openConnectionsSemaphore.release();
  }

  private void newConnectionRequest() {

    this.awaitReset();

    LOGGER.debug(
        "newConnectionRequest(). available = {} ",
        this.newConnectionRequestsSemaphore.availablePermits());

    try {
      this.newConnectionRequestsSemaphore.acquire();
      LOGGER.debug(
          "Request newConnection granted. available = {} ",
          this.newConnectionRequestsSemaphore.availablePermits());
    } catch (final InterruptedException e) {
      LOGGER.warn("Unable to acquire New Connection Request", e);
      Thread.currentThread().interrupt();
    }
  }

  private synchronized void awaitReset() {
    while (this.resetTimerLock.isLocked()) {
      try {
        this.resetTimerLock.wait(this.resetTime);
      } catch (final InterruptedException e) {
        LOGGER.warn("Unable to acquire New Connection Request Lock", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  private class ResetTask extends TimerTask {

    @Override
    public void run() {

      try {
        ThrottlingService.this.resetTimerLock.lock();

        final int nrOfPermitsToBeReleased =
            ThrottlingService.this.maxNewConnectionRequests
                - ThrottlingService.this.newConnectionRequestsSemaphore.availablePermits();

        LOGGER.debug(
            "releasing {} permits on newConnectionRequestsSemaphore", nrOfPermitsToBeReleased);

        ThrottlingService.this.newConnectionRequestsSemaphore.release(nrOfPermitsToBeReleased);

        LOGGER.debug(
            "ThrottlingService - Timer Reset and Unlocking, newConnectionRequests available = {}  ",
            ThrottlingService.this.newConnectionRequestsSemaphore.availablePermits());
      } finally {
        ThrottlingService.this.resetTimerLock.unlock();
      }
    }
  }

  @Override
  public String toString() {
    return String.format(
        "ThrottlingService. maxOpenConnections = %d, maxNewConnectionRequests=%d, resetTime=%d",
        this.maxOpenConnections, this.maxNewConnectionRequests, this.resetTime);
  }
}
