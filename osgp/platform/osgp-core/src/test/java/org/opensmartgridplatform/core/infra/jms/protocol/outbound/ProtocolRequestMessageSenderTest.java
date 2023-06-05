// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProtocolRequestMessageSenderTestConfig.class})
public class ProtocolRequestMessageSenderTest {

  @Autowired private ProtocolRequestMessageSender messageSender;

  @Autowired private int messageGroupCacheSize;

  @Test
  public void testMessageGroupIdShouldBeBetween0AndCacheSize() {
    for (int i = 0; i < 10000; i++) {
      final String uuid = UUID.randomUUID().toString();
      final String messageGroupId = this.messageSender.getMessageGroupId(uuid);
      final int actual = Integer.valueOf(messageGroupId);

      assertThat(actual >= 0 && actual < this.messageGroupCacheSize)
          .as("Message group id should be between 0 and cache size " + this.messageGroupCacheSize)
          .isTrue();
    }
  }

  @Test
  public void testMmessageGroupIdShouldBeSameForSameDevice() {
    final String deviceId = "test-device-001";
    final Set<String> groupIds = new HashSet<>();

    for (int i = 0; i < 100; i++) {
      groupIds.add(this.messageSender.getMessageGroupId(deviceId));
    }
    final int actual = groupIds.size();
    assertThat(actual == 1).as("Message group id should be the same for same device").isTrue();
  }

  @Test
  public void testNumberOfMessageGroupsShouldNotExceedCacheSize() {
    final Set<String> groupIds = new HashSet<>();

    for (int i = 0; i < 10000; i++) {
      final String uuid = UUID.randomUUID().toString();
      groupIds.add(this.messageSender.getMessageGroupId(uuid));
    }
    final int actual = groupIds.size();
    assertThat(actual <= this.messageGroupCacheSize)
        .as("Number of message groups should not exceed cache size " + this.messageGroupCacheSize)
        .isTrue();
  }
}
