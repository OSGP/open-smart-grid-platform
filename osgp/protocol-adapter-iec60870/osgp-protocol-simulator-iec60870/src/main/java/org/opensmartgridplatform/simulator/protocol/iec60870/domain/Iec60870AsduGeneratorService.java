/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.io.IOException;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

public class Iec60870AsduGeneratorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870AsduGeneratorService.class);

  /**
   * Creates a generator, which generates a value exactly once. It generates the value without a
   * delay.
   *
   * @param connectionRegistry Contains the connections to which the generated value needs to be
   *     sent.
   * @param asduGenerator Generator for the value.
   */
  public Iec60870AsduGeneratorService(
      final Iec60870ConnectionRegistry connectionRegistry,
      final Iec60870AsduGenerator asduGenerator) {

    LOGGER.info("Creating a 'generate once' generator");
    new RunnableTask(connectionRegistry, asduGenerator).run();
  }

  /**
   * Creates a generator, which periodically generates a value.
   *
   * @param connectionRegistry Contains the connections to which the generated value needs to be
   *     sent.
   * @param cronTrigger The schedule when to generate values.
   * @param asduGenerator Generator for the values.
   */
  public Iec60870AsduGeneratorService(
      final Iec60870ConnectionRegistry connectionRegistry,
      final CronTrigger cronTrigger,
      final Iec60870AsduGenerator asduGenerator) {

    LOGGER.info("Creating a 'periodic' generator");
    this.getThreadPoolTaskScheduler()
        .schedule(new RunnableTask(connectionRegistry, asduGenerator), cronTrigger);
  }

  private ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
    final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(1);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    threadPoolTaskScheduler.initialize();

    return threadPoolTaskScheduler;
  }

  private class RunnableTask implements Runnable {

    private Iec60870ConnectionRegistry connectionRegistry;
    private Iec60870AsduGenerator asduFactory;

    public RunnableTask(
        final Iec60870ConnectionRegistry connectionRegistry,
        final Iec60870AsduGenerator asduFactory) {
      this.connectionRegistry = connectionRegistry;
      this.asduFactory = asduFactory;
    }

    @Override
    public void run() {

      try {
        final ASdu asdu = this.asduFactory.getNextAsdu();
        LOGGER.info("Generated ASDU: {}", asdu);
        for (final Connection connection : this.connectionRegistry.getAllConnections()) {
          connection.send(asdu);
        }
      } catch (final IOException e) {
        LOGGER.error("Exception occurred while generating an ASDU", e);
      }
    }
  }
}
