/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.config;

import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.metrics.PrometheusMetricsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a Prometheus metrics server with optional default metrics for CPU, memory, database. See
 * {@link PrometheusMetricsServer} for details on how to configure the port/path of the metrics
 * server.
 *
 * <p>The provided {@link PrometheusMeterRegistry} bean can be used to add custom metrics.
 */
@Configuration
public class PrometheusConfig extends AbstractConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusConfig.class);

  @Autowired(required = false)
  private DataSource dataSource;

  @Value("${metrics.prometheus.enabledDefaultMetrics:true}")
  private boolean enableDefaultMetrics;

  /**
   * MeterRegistry to publish custom metrics. This bean is required because custom metrics depend on
   * it.
   *
   * @return registry
   */
  @Bean
  public PrometheusMeterRegistry getMeterRegistry() {
    LOGGER.info("Enabling Prometheus metrics");
    final PrometheusMeterRegistry registry =
        new PrometheusMeterRegistry(io.micrometer.prometheus.PrometheusConfig.DEFAULT);

    if (this.enableDefaultMetrics) {
      this.bindDefaultMetrics(registry);
    }

    return registry;
  }

  /**
   * The HTTP server providing Prometheus with metrics. Only started when configured to do so.
   *
   * @param meterRegistry The registry to publish
   * @return Prometheus HTTP server (runs automatically)
   */
  @Bean
  @Conditional(PrometheusEnabledCondition.class)
  public PrometheusMetricsServer prometheusMetricsServer(
      final PrometheusMeterRegistry meterRegistry) {
    return new PrometheusMetricsServer(meterRegistry);
  }

  private void bindDefaultMetrics(final PrometheusMeterRegistry registry) {
    LOGGER.info("Enabling default metrics");
    new ClassLoaderMetrics().bindTo(registry);
    new JvmMemoryMetrics().bindTo(registry);
    try (final JvmGcMetrics jvmGcMetrics = new JvmGcMetrics()) {
      jvmGcMetrics.bindTo(registry);
    }
    new ProcessorMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
    try (final LogbackMetrics logbackMetrics = new LogbackMetrics()) {
      logbackMetrics.bindTo(registry);
    }
    new DiskSpaceMetrics(new File("/")).bindTo(registry);
    if (this.dataSource != null) {
      try (final Connection connection = this.dataSource.getConnection()) {
        final String databaseName = connection.getCatalog();
        new PostgreSQLDatabaseMetrics(this.dataSource, databaseName).bindTo(registry);
      } catch (final SQLException e) {
        LOGGER.warn(
            "Prometheus database monitoring disabled: Datasource found, but can't get the name of the database.",
            e);
      }
    }
    LOGGER.info("Default metrics enabled");
  }
}
