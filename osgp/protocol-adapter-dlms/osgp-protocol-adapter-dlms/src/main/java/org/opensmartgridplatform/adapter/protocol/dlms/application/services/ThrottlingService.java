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

    @Value("${throttling.max.open.connections:30}")
    private int maxOpenConnections = 10;

    @Value("${throttling.new.connections:10}")
    private int maxNewConnections = 30;

    private final Semaphore openConnectionsSemaphore;
    private Semaphore newConnectionsSemaphore;

    public ThrottlingService() {

        LOGGER.info("Initializing ThrottlingService");

        openConnectionsSemaphore = new Semaphore(maxOpenConnections);
        newConnectionsSemaphore = new Semaphore(maxNewConnections);

        new Timer().scheduleAtFixedRate(new ResetTask(), 2000, 2000);
    }

    public synchronized void openConnection() throws InterruptedException {

        this.openConnectionsSemaphore.acquire();

        LOGGER.info("acquired openConnection. available = {} ", this.openConnectionsSemaphore.availablePermits());
    }

    public void closeConnection() {

        LOGGER.info("closeConnection available = {} ", this.openConnectionsSemaphore.availablePermits());
        this.openConnectionsSemaphore.release();
    }

    public void newConnection() throws InterruptedException {

        this.newConnectionsSemaphore.acquire();

        LOGGER.info("acquired newConnection. available = {} ", this.newConnectionsSemaphore.availablePermits());
    }

    private class ResetTask extends TimerTask {
        @Override
        public void run() {

            newConnectionsSemaphore.release(maxNewConnections);

            newConnectionsSemaphore = new Semaphore(maxNewConnections);

            LOGGER.info("ThrottlingService - Timer Reset and Unlocking, newConnections available = {}  ",
                    newConnectionsSemaphore.availablePermits());
        }
    }

}
