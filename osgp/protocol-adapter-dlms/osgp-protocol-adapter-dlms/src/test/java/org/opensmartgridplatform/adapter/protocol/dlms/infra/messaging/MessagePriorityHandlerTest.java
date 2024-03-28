// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

class MessagePriorityHandlerTest {

  @Test
  void testRecalculatePriorityWithRetryCountIsZero() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(true, 2, 1, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(0).withMessagePriority(8).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertThat(newPriority).isEqualTo(8);
  }

  @Test
  void testRecalculatePriorityWithRetryCountIsZeroAndDecreaseIntervalIsOne() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(true, 1, 1, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(0).withMessagePriority(8).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertThat(newPriority).isEqualTo(7);
  }

  @Test
  void testRecalculatePriorityWithRetryCountIsBelowDecreaseInterval() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(true, 2, 1, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(1).withMessagePriority(8).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertThat(newPriority).isEqualTo(8);
  }

  @Test
  void testRecalculatePriorityWithRetryCountHitsDecreaseInterval() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(true, 2, 1, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(2).withMessagePriority(8).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertThat(newPriority).isEqualTo(7);
  }

  @Test
  void testRecalculatePriorityWithValueBiggerThanOne() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(true, 1, 3, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(1).withMessagePriority(8).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertThat(newPriority).isEqualTo(5);
  }

  @Test
  void testRecalculatePriorityWithDecreaseDisabled() {
    final MessagePriorityHandler handler = new MessagePriorityHandler(false, 2, 1, 0);
    final MessageMetadata metadata =
        new MessageMetadata.Builder().withRetryCount(2).withMessagePriority(5).build();

    final int newPriority = handler.recalculatePriority(metadata);

    assertEquals(5, newPriority);
  }
}
