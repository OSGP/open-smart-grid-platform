// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Resource;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.SetTransitionService;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java configuration requires Spring
 * Framework 3.0
 */
@Configuration
@ComponentScan(
    basePackages = {
      "org.opensmartgridplatform.domain.core",
      "org.opensmartgridplatform.adapter.domain.publiclighting"
    })
@Import({MetricsConfig.class})
@EnableTransactionManagement
public class ApplicationContext {

  @Resource private Environment environment;

  private static final String PROPERTY_NAME_SET_TRANSITION_LOGS_RESPONSE =
      "public.lighting.set.transition.logs.response";
  private static final String PROPERTY_NAME_SET_TRANSITION_DELAY_BETWEEN_BATCH_SECONDS =
      "public.lighting.set.transition.delay.between.batch.seconds";
  private static final String PROPERTY_NAME_SET_TRANSITION_POOL_SIZE =
      "public.lighting.set.transition.pool.size";

  @Bean
  public Boolean isSetTransitionResponseLoggingEnabled() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(PROPERTY_NAME_SET_TRANSITION_LOGS_RESPONSE));
  }

  @Bean
  public SetTransitionService setTransitionService() {
    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(this.poolSize());
    return new SetTransitionService(executor, this.delayBetweenBatchSeconds());
  }

  private Integer poolSize() {
    return Integer.valueOf(
        this.environment.getRequiredProperty(PROPERTY_NAME_SET_TRANSITION_POOL_SIZE));
  }

  private Integer delayBetweenBatchSeconds() {
    return Integer.valueOf(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_SET_TRANSITION_DELAY_BETWEEN_BATCH_SECONDS));
  }
}
