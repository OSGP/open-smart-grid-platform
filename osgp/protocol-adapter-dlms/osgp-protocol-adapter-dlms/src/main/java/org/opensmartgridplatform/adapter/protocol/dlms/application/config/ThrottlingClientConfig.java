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
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
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
  private Duration permitRejectedDelay;

  @Value("#{T(java.time.Duration).parse('${throttling.register.retry.delay:PT5S}')}")
  private Duration registerRetryDelay;

  public boolean clientEnabled() {
    return this.clientEnabled;
  }

  public String configurationName() {
    return this.configurationName;
  }

  /*
   * This bean should be annotated with @Lazy, because on initialization
   * the client will register itself at the throttling service.
   * Registering is done by a http call, whenever the throttling service is
   * not available yet, the application should start anyway.
   * On the first call with the throttling client, the register call will be executed.
   * The client will keep on trying to register itself with throttling service, after the
   * successful register, the client can communicate with the throttling service
   */
  @Lazy
  @Bean(destroyMethod = "unregister")
  @Conditional(ThrottlingClientEnabledCondition.class)
  public ThrottlingClient throttlingClient() {
    final ThrottlingClient throttlingClient =
        new ThrottlingClient(
            new org.opensmartgridplatform.throttling.api.ThrottlingConfig(
                this.configurationName, this.configurationMaxConcurrency),
            this.throttlingServiceUrl,
            this.timeout);

    this.registerThrottlingClientUntilSuccess(throttlingClient);
    return throttlingClient;
  }

  protected void registerThrottlingClientUntilSuccess(final ThrottlingClient throttlingClient) {
    while (!this.registerThrottlingClient(throttlingClient)) {
      try {
        Thread.sleep(this.registerRetryDelay.toMillis());
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("InterruptedException while registering the throttling client", e);
      }
    }
  }

  private boolean registerThrottlingClient(final ThrottlingClient throttlingClient) {
    try {
      log.info("Try to register the client to the throttling service");
      throttlingClient.register();
      return true;
    } catch (final Exception exception) {
      log.warn(
          "Registering the client at the throttling service was not successful, retry in {} ms",
          this.registerRetryDelay,
          exception);
    }
    return false;
  }

  /**
   * Delay to be applied before retrying some action when a requested permit was not granted.
   *
   * @return delay
   */
  public Duration permitRejectedDelay() {
    return this.permitRejectedDelay;
  }
}
