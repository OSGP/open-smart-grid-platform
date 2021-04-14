/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * This class provides the basic {@link Bean} function to create an {@link OsgpScheduler} instance,
 * which uses a Quartz {@link Scheduler}.
 */
public class AbstractOsgpSchedulerConfig extends AbstractSchedulingConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOsgpSchedulerConfig.class);

  @Value("${quartz.scheduler.start.attempt.max.count:10}")
  private int startAttemptMaxCount;

  @Value("${quartz.scheduler.start.attempt.sleep.time:30000}")
  private long startAttemptSleepTime;

  private int startAttemptCount = 0;

  private final String schedulerName;

  /**
   * Constructor used to specify the scheduler name, which will be set as Quartz {@link Scheduler}
   * property {@code org.quartz.scheduler.instanceName}.
   *
   * @param schedulerName The scheduler name.
   */
  public AbstractOsgpSchedulerConfig(final String schedulerName) {
    this.schedulerName = schedulerName;
  }

  /**
   * Creates an instance of {@link OsgpScheduler} and starts the Quartz {@link Scheduler}.
   *
   * @return An instance of {@link OsgpScheduler},
   */
  @Bean
  public OsgpScheduler osgpScheduler() throws SchedulerException {

    this.testIfQuartzTablesExist();

    final SchedulingConfigProperties schedulingConfigProperties =
        SchedulingConfigProperties.newBuilder()
            .withSchedulerName(this.schedulerName)
            .withThreadCountKey(KEY_QUARTZ_SCHEDULER_THREAD_COUNT)
            .withJobStoreDbUrl(this.getDatabaseUrl())
            .withJobStoreDbUsername(this.databaseUsername)
            .withJobStoreDbPassword(this.databasePassword)
            .withJobStoreDbDriver(this.databaseDriver)
            .build();

    final Scheduler quartzScheduler =
        this.constructAndStartQuartzScheduler(schedulingConfigProperties);
    return new OsgpScheduler(quartzScheduler);
  }

  private void testIfQuartzTablesExist() {
    final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
    singleConnectionDataSource.setDriverClassName(this.databaseDriver);
    singleConnectionDataSource.setUsername(this.databaseUsername);
    singleConnectionDataSource.setPassword(this.databasePassword);
    singleConnectionDataSource.setUrl(this.getDatabaseUrl());
    singleConnectionDataSource.setSuppressClose(false);

    try {
      final Connection connection = singleConnectionDataSource.getConnection();
      final Statement statement = connection.createStatement();
      statement.executeQuery("SELECT * FROM qrtz_locks LIMIT 1");
      connection.close();
    } catch (final SQLException e) {
      LOGGER.debug("SQLException", e);
      if (e.getMessage().contains("ERROR: relation \"qrtz_locks\" does not exist")) {
        this.determineRetry("Table qrtz_lock does not exist");
      }
    } finally {
      singleConnectionDataSource.destroy();
    }
  }

  private void determineRetry(final String reason) {
    this.startAttemptCount++;

    if (this.startAttemptCount < this.startAttemptMaxCount) {
      LOGGER.info(
          "Scheduler failed to start: {}! Sleeping for {} milliseconds before retrying, attempt {} of {}.",
          reason,
          this.startAttemptSleepTime,
          this.startAttemptCount,
          this.startAttemptMaxCount);
      this.sleep();
      this.testIfQuartzTablesExist();
    } else {
      final String message =
          String.format(
              "Unable to construct or start scheduler after %d retries!",
              this.startAttemptMaxCount);
      throw new BeanCreationException(message);
    }
  }

  private void sleep() {
    try {
      Thread.sleep(this.startAttemptSleepTime);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error("InterruptedException", e);
    }
  }
}
