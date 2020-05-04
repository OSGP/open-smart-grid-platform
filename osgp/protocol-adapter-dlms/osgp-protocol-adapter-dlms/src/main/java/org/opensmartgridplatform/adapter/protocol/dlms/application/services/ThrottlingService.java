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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThrottlingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingService.class);

    private final int maxOpenConnections;
    private final int maxNewConnectionRequests;
    private final int resetTime;

    private final Semaphore openConnectionsSemaphore;
    private Semaphore newConnectionRequestsSemaphore;

    public ThrottlingService(@Value("${throttling.max.open.connections:30}") int maxOpenConnections,
            @Value("${throttling.new.connection.requests:10}") int maxNewConnectionRequests,
            @Value("${throttling.new.connection.requests.reset.time:2000}") int resetTime) {

        this.maxOpenConnections = maxOpenConnections;
        this.maxNewConnectionRequests = maxNewConnectionRequests;
        this.resetTime = resetTime;

        LOGGER.info("Initializing ThrottlingService. {}", toString());

        openConnectionsSemaphore = new Semaphore(maxOpenConnections);
        newConnectionRequestsSemaphore = new Semaphore(maxNewConnectionRequests);

        new Timer().scheduleAtFixedRate(new ResetTask(), resetTime, resetTime);
    }

    public synchronized void openConnection() throws InterruptedException {

        this.openConnectionsSemaphore.acquire();

        LOGGER.info("acquired openConnection. available = {} ", this.openConnectionsSemaphore.availablePermits());
    }

    public void closeConnection() {

        LOGGER.info("closeConnection available = {} ", this.openConnectionsSemaphore.availablePermits());
        this.openConnectionsSemaphore.release();
    }

    public void newConnectionRequest() throws InterruptedException {

        this.newConnectionRequestsSemaphore.acquire();

        LOGGER.info("acquired newConnection. available = {} ", this.newConnectionRequestsSemaphore.availablePermits());
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
