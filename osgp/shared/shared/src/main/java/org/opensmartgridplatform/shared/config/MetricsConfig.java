// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.config;

import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmInfoMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a Prometheus metrics server with optional default metrics for CPU, memory, database. See
 * {@link PrometheusMetricsServer} for details on how to configure the port/path of the metrics
 * server.
 *
 * <p>The provided {@link PrometheusMeterRegistry} bean can be used to add custom metrics.
 */
@Configuration
// Points Spring to the PrometheusMetricsServer service.
@ComponentScan(basePackages = {"org.opensmartgridplatform.shared.metrics"})
public class MetricsConfig extends AbstractConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsConfig.class);

  @Value("${metrics.prometheus.enabled:false}")
  private boolean metricsEnabled;

  @Value("${metrics.prometheus.enableDefaultMetrics:true}")
  private boolean enableDefaultMetrics;

  @Autowired(required = false)
  private DataSource dataSource;

  /**
   * MeterRegistry to publish custom metrics. This bean is required because custom metrics depend on
   * it.
   *
   * @return registry
   */
  @Bean
  public PrometheusMeterRegistry meterRegistry() {
    LOGGER.info("Enabling Prometheus metrics");
    final PrometheusMeterRegistry registry =
        new PrometheusMeterRegistry(io.micrometer.prometheusmetrics.PrometheusConfig.DEFAULT);

    if (this.metricsEnabled && this.enableDefaultMetrics) {
      this.bindDefaultMetrics(registry);
    }

    return registry;
  }

  // IntelliJ suggests you replace this with a try-with-resources. Don't! It will close the
  // metrics gatherer right after binding it and it will no longer produce metrics
  @SuppressWarnings("java:S2095")
  private void bindDefaultMetrics(final PrometheusMeterRegistry registry) {
    LOGGER.info("Enabling default metrics");
    new ClassLoaderMetrics().bindTo(registry);
    new JvmMemoryMetrics().bindTo(registry);
    new JvmGcMetrics().bindTo(registry); // do not auto-close, no metrics after that
    new JvmThreadMetrics().bindTo(registry); // do not auto-close, no metrics after that
    new JvmHeapPressureMetrics().bindTo(registry); // do not auto-close, no metrics after that
    new JvmInfoMetrics().bindTo(registry); // do not auto-close, no metrics after that
    new ProcessorMetrics().bindTo(registry);
    new LogbackMetrics().bindTo(registry); // do not auto-close, no metrics after that
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
