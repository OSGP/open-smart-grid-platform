package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.metrics.DatabaseHealthCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DropwizardConfig {

  @Bean
  public MetricRegistry metricRegistry() {
    return new MetricRegistry();
  }

  @Bean
  public HealthCheckRegistry healthCheckRegistry(final DatabaseHealthCheck databaseHealthCheck) {
    final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
    healthCheckRegistry.register("database", databaseHealthCheck);
    return healthCheckRegistry;
  }
}
