/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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

    openConnectionsSemaphore = new Semaphore(maxOpenConnections);
    newConnectionRequestsSemaphore = new Semaphore(maxNewConnectionRequests);

    resetTimerLock = new ReentrantLock();

    resetTimer = new Timer();
    resetTimer.scheduleAtFixedRate(new ResetTask(), resetTime, resetTime);

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
    } catch (InterruptedException e) {
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

    awaitReset();

    LOGGER.debug(
        "newConnectionRequest(). available = {} ",
        this.newConnectionRequestsSemaphore.availablePermits());

    try {
      this.newConnectionRequestsSemaphore.acquire();
      LOGGER.debug(
          "Request newConnection granted. available = {} ",
          this.newConnectionRequestsSemaphore.availablePermits());
    } catch (InterruptedException e) {
      LOGGER.warn("Unable to acquire New Connection Request", e);
      Thread.currentThread().interrupt();
    }
  }

  private synchronized void awaitReset() {
    while (resetTimerLock.isLocked()) {
      try {
        resetTimerLock.wait(this.resetTime);
      } catch (InterruptedException e) {
        LOGGER.warn("Unable to acquire New Connection Request Lock", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  private class ResetTask extends TimerTask {

    @Override
    public void run() {

      try {
        resetTimerLock.lock();

        int nrOfPermitsToBeReleased =
            maxNewConnectionRequests - newConnectionRequestsSemaphore.availablePermits();

        LOGGER.debug(
            "releasing {} permits on newConnectionRequestsSemaphore", nrOfPermitsToBeReleased);

        newConnectionRequestsSemaphore.release(nrOfPermitsToBeReleased);

        LOGGER.debug(
            "ThrottlingService - Timer Reset and Unlocking, newConnectionRequests available = {}  ",
            newConnectionRequestsSemaphore.availablePermits());
      } finally {
        resetTimerLock.unlock();
      }
    }
  }

  @Override
  public String toString() {
    return String.format(
        "ThrottlingService. maxOpenConnections = %d, maxNewConnectionRequests=%d, resetTime=%d",
        maxOpenConnections, maxNewConnectionRequests, resetTime);
  }
}
