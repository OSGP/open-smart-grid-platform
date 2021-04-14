/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Resource;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.SetTransitionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
