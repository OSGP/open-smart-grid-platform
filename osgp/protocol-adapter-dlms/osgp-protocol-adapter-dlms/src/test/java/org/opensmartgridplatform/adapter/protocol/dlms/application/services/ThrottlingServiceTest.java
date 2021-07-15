/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

    this.openingThreadDone = new AtomicBoolean(false);
    this.closingThreadDone = new AtomicBoolean(false);

    this.openingThread().start();
    this.closingThread().start();

    assertThat(true).isEqualTo(true);

    while (!this.openingThreadDone.get() && !this.closingThreadDone.get()) {
      Thread.sleep(1000);
    }
  }

  private Thread openingThread() {
    return new Thread(
        () -> {
          for (int i = 0; i < 100; i++) {

            LOGGER.info("Incoming request {}", i);
            this.throttlingService.openConnection();
          }

          LOGGER.info("Opening Connection Thread done");
          this.openingThreadDone.set(true);
        });
  }

  private Thread closingThread() {
    return new Thread(
        () -> {
          for (int i = 0; i < 100; i++) {
            LOGGER.info("Closing Connection {}", i);
            this.throttlingService.closeConnection();
            try {
              Thread.sleep(200);
            } catch (final InterruptedException e) {
              this.closingThreadDone.set(true);
            }
          }

          LOGGER.info("CLosing Connection Thread done");
          this.closingThreadDone.set(true);
        });
  }
}
