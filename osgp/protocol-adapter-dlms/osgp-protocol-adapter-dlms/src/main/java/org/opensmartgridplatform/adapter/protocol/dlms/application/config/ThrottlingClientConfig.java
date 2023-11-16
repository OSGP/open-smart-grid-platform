// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.security.SecureRandom;
import java.time.Duration;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingClientConfig {
  private static final SecureRandom random = new SecureRandom();

  @Value("${throttling.client.enabled:false}")
  private boolean clientEnabled;

  @Value("${throttling.configuration.name:'CDMA'}")
  private String configurationName;

  @Value("${throttling.configuration.max.concurrency:1000}")
  private int configurationMaxConcurrency;

  @Value("${throttling.service.url:http://localhost:9090}")
  private String throttlingServiceUrl;

  @Value("${throttling.client.max-conn-per-route:20}")
  private int maxConnPerRoute;

  @Value("${throttling.client.max-conn-total:100}")
  private int maxConnTotal;

  @Value("#{T(java.time.Duration).parse('${throttling.service.timeout:PT30S}')}")
  private Duration timeout;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.min.delay:PT50S}')}")
  private Duration permitRejectedMinDelay;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.max.delay:PT70S}')}")
  private Duration permitRejectedMaxDelay;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.high.prio.delay:PT2S}')}")
  private Duration permitRejectedHighPrioDelay;

  public boolean clientEnabled() {
    return this.clientEnabled;
  }

  public String configurationName() {
    return this.configurationName;
  }

  @Bean(destroyMethod = "unregister")
  @Conditional(ThrottlingClientEnabledCondition.class)
  public ThrottlingClient throttlingClient() {
    return new ThrottlingClient(
        new ThrottlingConfig(this.configurationName, this.configurationMaxConcurrency),
        this.throttlingServiceUrl,
        this.timeout,
        this.maxConnPerRoute,
        this.maxConnTotal);
  }

  /**
   * Delay to be applied before retrying some action when a requested permit was not granted.
   *
   * @return delay
   */
  public Duration permitRejectedDelay(final int messagePriority) {
    if (messagePriority > MessagePriorityEnum.DEFAULT.getPriority()) {
      return this.permitRejectedHighPrioDelay;
    }
    final long minMillis =
        Math.min(this.permitRejectedMinDelay.toMillis(), this.permitRejectedMaxDelay.toMillis());
    final long maxMillis =
        Math.max(this.permitRejectedMinDelay.toMillis(), this.permitRejectedMaxDelay.toMillis());
    return Duration.ofMillis(this.random.nextLong(minMillis, maxMillis));
  }
}
