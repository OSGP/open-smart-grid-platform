// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.Duration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.springframework.test.util.ReflectionTestUtils;

class ThrottlingClientConfigTest {
  private final Duration permitRejectedMinDelay = Duration.parse("PT50S");
  private final Duration permitRejectedMaxDelay = Duration.parse("PT70S");
  private final Duration permitRejectedHighPrioDelay = Duration.parse("PT2S");

  @ParameterizedTest
  @EnumSource(MessagePriorityEnum.class)
  void testHttpComponentsMessageSender(final MessagePriorityEnum messagePriority) {
    final ThrottlingClientConfig throttlingClientConfig = new ThrottlingClientConfig();

    ReflectionTestUtils.setField(
        throttlingClientConfig,
        "permitRejectedMinDelay",
        this.permitRejectedMinDelay,
        Duration.class);
    ReflectionTestUtils.setField(
        throttlingClientConfig,
        "permitRejectedMaxDelay",
        this.permitRejectedMaxDelay,
        Duration.class);
    ReflectionTestUtils.setField(
        throttlingClientConfig,
        "permitRejectedHighPrioDelay",
        this.permitRejectedHighPrioDelay,
        Duration.class);

    final Duration result =
        throttlingClientConfig.permitRejectedDelay(messagePriority.getPriority());
    if (messagePriority.getPriority() > MessagePriorityEnum.DEFAULT.getPriority()) {
      assertThat(result).isEqualTo(this.permitRejectedHighPrioDelay);
    } else {
      assertThat(result).isBetween(this.permitRejectedMinDelay, this.permitRejectedMaxDelay);
    }
  }
}
