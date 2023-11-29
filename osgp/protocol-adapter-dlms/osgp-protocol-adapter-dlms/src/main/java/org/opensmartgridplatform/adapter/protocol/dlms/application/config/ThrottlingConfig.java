// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.security.SecureRandom;
import java.time.Duration;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThrottlingConfig {
  private static final SecureRandom RANDOM = new SecureRandom();

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.min.delay:PT50S}')}")
  private Duration permitRejectedMinDelay;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.max.delay:PT70S}')}")
  private Duration permitRejectedMaxDelay;

  @Value("#{T(java.time.Duration).parse('${throttling.rejected.high.prio.delay:PT2S}')}")
  private Duration permitRejectedHighPrioDelay;

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
    return Duration.ofMillis(RANDOM.nextLong(minMillis, maxMillis));
  }
}
