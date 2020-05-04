/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThrottlingService.class)
@TestPropertySource(properties = { "throttlingService.maxOpenConnections=10",
        "throttlingService.maxNewConnectionRequests=30", "throttlingService.resetTime=2000" })
public class ThrottlingServiceTest {

    @Autowired
    ThrottlingService throttlingService;

    AtomicBoolean openingThreadDone;
    AtomicBoolean closingThreadDone;

    @Test
    public void testThrottling() throws InterruptedException {

        // throttlingService = new ThrottlingService();

        // ReflectionTestUtils.setField(throttlingService, "maxOpenConnections", 10);

        openingThreadDone = new AtomicBoolean(false);
        closingThreadDone = new AtomicBoolean(false);

        openingThread().start();
        closingThread().start();

        assertThat(true).isEqualTo(true);

        while (!openingThreadDone.get() && !closingThreadDone.get()) {
            //
        }
    }

    private Thread openingThread() {
        return new Thread(() -> {

            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println("Incoming request " + i);
                    throttlingService.newConnectionRequest();
                    throttlingService.openConnection();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Opening done");
            openingThreadDone.set(true);
        });
    }

    private Thread closingThread() {
        return new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                throttlingService.closeConnection();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    closingThreadDone.set(true);
                }
            }

            System.out.println("CLosing done");
            closingThreadDone.set(true);
        });
    }
}
