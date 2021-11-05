/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.time.Duration;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingClientConfig {

  @Value("${throttling.client.enabled:false}")
  private boolean clientEnabled;

  @Value("${throttling.configuration.name:'CDMA'}")
  private String configurationName;

  @Value("${throttling.configuration.max.concurrency:1000}")
  private int configurationMaxConcurrency;

  @Value("${throttling.service.url:http://localhost:9090}")
  private String throttlingServiceUrl;

  @Value("#{T(java.time.Duration).parse('${throttling.service.timeout:PT30S}')}")
  private Duration timeout;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.delay:PT10S}')}")
  private Duration delay;

  public boolean clientEnabled() {
    return this.clientEnabled;
  }

  public String configurationName() {
    return this.configurationName;
  }

  @Bean(destroyMethod = "unregister")
  @Conditional(ThrottlingClientEnabledCondition.class)
  public ThrottlingClient throttlingClient() {
    final ThrottlingClient throttlingClient =
        new ThrottlingClient(
            new org.opensmartgridplatform.throttling.api.ThrottlingConfig(
                this.configurationName, this.configurationMaxConcurrency),
            this.throttlingServiceUrl,
            this.timeout);
    throttlingClient.register();
    return throttlingClient;
  }

  /**
   * Delay to be applied before retrying some action when a requested permit was not granted.
   *
   * @return delay
   */
  public Duration delay() {
    return this.delay;
  }
}