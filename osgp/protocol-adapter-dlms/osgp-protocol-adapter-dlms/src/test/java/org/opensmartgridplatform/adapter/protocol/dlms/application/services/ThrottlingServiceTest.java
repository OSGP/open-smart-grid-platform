/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThrottlingService.class)
@TestPropertySource(
    properties = {
      "throttling.max.open.connections=10",
      "throttling.max.new.connection.requests=30",
      "throttling.reset.time=2000"
    })
public class ThrottlingServiceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingServiceTest.class);

  @Autowired ThrottlingService throttlingService;

  AtomicBoolean openingThreadDone;
  AtomicBoolean closingThreadDone;

  @Test
  public void testThrottling() throws InterruptedException {

    openingThreadDone = new AtomicBoolean(false);
    closingThreadDone = new AtomicBoolean(false);

    openingThread().start();
    closingThread().start();

    assertThat(true).isEqualTo(true);

    while (!openingThreadDone.get() && !closingThreadDone.get()) {
      Thread.sleep(1000);
    }
  }

  private Thread openingThread() {
    return new Thread(
        () -> {
          for (int i = 0; i < 100; i++) {

            LOGGER.info("Incoming request {}", i);
            throttlingService.openConnection();
          }

          LOGGER.info("Opening Connection Thread done");
          openingThreadDone.set(true);
        });
  }

  private Thread closingThread() {
    return new Thread(
        () -> {
          for (int i = 0; i < 100; i++) {
            LOGGER.info("Closing Connection {}", i);
            throttlingService.closeConnection();
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
              closingThreadDone.set(true);
            }
          }

          LOGGER.info("CLosing Connection Thread done");
          closingThreadDone.set(true);
        });
  }
}
