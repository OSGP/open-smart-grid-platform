/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThrottlingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingService.class);

    public void setMaxOpenConnections(int maxOpenConnections) {
        this.maxOpenConnections = maxOpenConnections;
    }

    public void setMaxNewConnectionRequests(int maxNewConnectionRequests) {
        this.maxNewConnectionRequests = maxNewConnectionRequests;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    @Value("${throttlingService.maxOpenConnections}")
    private int maxOpenConnections;
    @Value("${throttlingService.maxNewConnectionRequests}")
    private int maxNewConnectionRequests;
    @Value("${throttlingService.resetTime}")
    private int resetTime;

    private Semaphore openConnectionsSemaphore;
    private Semaphore newConnectionRequestsSemaphore;
    private Timer resetTimer;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Initializing ThrottlingService. {}", toString());

        openConnectionsSemaphore = new Semaphore(maxOpenConnections);
        newConnectionRequestsSemaphore = new Semaphore(maxNewConnectionRequests);

        resetTimer = new Timer();
        resetTimer.scheduleAtFixedRate(new ResetTask(), resetTime, resetTime);
    }

    @PreDestroy
    public void preDestroy() {
        if (this.resetTimer != null) {
            this.resetTimer.cancel();
        }
    }

    public synchronized void openConnection() throws InterruptedException {

        LOGGER.info("Requesting openConnection. available = {} ", this.openConnectionsSemaphore.availablePermits());

        this.openConnectionsSemaphore.acquire();

        LOGGER.info("openConnection granted. available = {} ", this.openConnectionsSemaphore.availablePermits());
    }

    public void closeConnection() {

        LOGGER.info("closeConnection available = {} ", this.openConnectionsSemaphore.availablePermits());
        this.openConnectionsSemaphore.release();
    }

    public void newConnectionRequest() throws InterruptedException {

        LOGGER.info("Request newConnection. available = {} ", this.newConnectionRequestsSemaphore.availablePermits());

        this.newConnectionRequestsSemaphore.acquire();

        LOGGER.info("Request newConnection granted. available = {} ",
                this.newConnectionRequestsSemaphore.availablePermits());
    }

    private class ResetTask extends TimerTask {
        @Override
        public void run() {

            newConnectionRequestsSemaphore.release(maxNewConnectionRequests);

            newConnectionRequestsSemaphore = new Semaphore(maxNewConnectionRequests);

            LOGGER.info("ThrottlingService - Timer Reset and Unlocking, max newConnectionRequests available = {}  ",
                    newConnectionRequestsSemaphore.availablePermits());
        }
    }

    @Override
    public String toString() {
        return String.format("ThrottlingService. maxOpenConnections = %d, maxNewConnectionRequests=%d, resetTime=%d",
                maxOpenConnections, maxNewConnectionRequests, resetTime);
    }

}
