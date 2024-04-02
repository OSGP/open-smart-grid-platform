// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

class MessagePriorityHandlerTest {

  private static Stream<Arguments> data() {
    return Stream.of(
        Arguments.of(new MessagePriorityHandler(true, 1, 1, 0), 0, 8, 8),
        Arguments.of(new MessagePriorityHandler(true, 2, 1, 0), 0, 8, 8),
        Arguments.of(new MessagePriorityHandler(true, 1, 1, 0), 1, 8, 7),
        Arguments.of(new MessagePriorityHandler(true, 2, 1, 0), 1, 8, 8),
        Arguments.of(new MessagePriorityHandler(true, 2, 1, 0), 2, 8, 7),
        Arguments.of(new MessagePriorityHandler(true, 1, 3, 0), 1, 8, 5),
        Arguments.of(new MessagePriorityHandler(false, 2, 1, 0), 2, 5, 5));
  }

  @ParameterizedTest
  @MethodSource("data")
  void testRecalculatePriority(
      final MessagePriorityHandler handler,
      final int retryCount,
      final int initialPriority,
      final int expectedPriority) {
    final MessageMetadata metadata =
        new MessageMetadata.Builder()
            .withRetryCount(retryCount)
            .withMessagePriority(initialPriority)
            .build();
    final int newPriority = handler.recalculatePriority(metadata);
    assertThat(newPriority).isEqualTo(expectedPriority);
  }
}
