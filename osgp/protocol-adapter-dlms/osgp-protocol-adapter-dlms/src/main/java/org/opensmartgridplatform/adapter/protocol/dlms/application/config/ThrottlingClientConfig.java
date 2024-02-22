// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.time.Duration;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation.SharedThrottlingServiceCondition;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingClientConfig {

  @Value("${throttling.client.enabled:false}")
  private boolean clientEnabled;

  @Value("${throttling.configuration.name:CDMA}")
  private String configurationName;

  @Value("${throttling.configuration.max.concurrency:1000}")
  private int configurationMaxConcurrency;

  @Value("${throttling.configuration.max.new.connections:-1}")
  private int configurationMaxNewConnections;

  @Value("${throttling.configuration.max.new.connections.reset.time.in.ms:1000}")
  private long configurationMaxNewConnectionsResetTimeInMs;

  @Value("${throttling.configuration.max.new.connections.wait.time.in.ms:1000}")
  private long configurationMaxNewConnectionsWaitTimeInMs;

  @Value("${throttling.service.url}")
  private String throttlingServiceUrl;

  @Value("${throttling.client.max-conn-per-route:20}")
  private int maxConnPerRoute;

  @Value("${throttling.client.max-conn-total:100}")
  private int maxConnTotal;

  @Value("#{T(java.time.Duration).parse('${throttling.service.timeout:PT30S}')}")
  private Duration timeout;

  public boolean clientEnabled() {
    return this.clientEnabled;
  }

  public String configurationName() {
    return this.configurationName;
  }

  @Bean(destroyMethod = "unregister")
  @Conditional(SharedThrottlingServiceCondition.class)
  public ThrottlingClient throttlingClient() {
    return new ThrottlingClient(
        new ThrottlingConfig(
            this.configurationName,
            this.configurationMaxConcurrency,
            this.configurationMaxNewConnections,
            this.configurationMaxNewConnectionsResetTimeInMs,
            this.configurationMaxNewConnectionsWaitTimeInMs),
        this.throttlingServiceUrl,
        this.timeout,
        this.maxConnPerRoute,
        this.maxConnTotal);
  }
}
