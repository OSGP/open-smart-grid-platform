// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging;

import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePriorityHandler {

  private final boolean decreaseEnabled;
  private final int decreaseInterval;
  private final int decreaseValue;
  private final int minimumPriority;

  public MessagePriorityHandler(
      @Value("${schedule.retry.dlms.decrease_priority.enabled:false}")
          final boolean decreaseEnabled,
      @Value("${schedule.retry.dlms.decrease_priority.interval:1}") final int decreaseInterval,
      @Value("${schedule.retry.dlms.decrease_priority.value:1}") final int decreaseValue,
      @Value("${schedule.retry.dlms.decrease_priority.min_priority:0}") final int minimumPriority) {
    super();
    this.decreaseEnabled = decreaseEnabled;
    this.decreaseInterval = decreaseInterval;
    this.decreaseValue = decreaseValue;
    this.minimumPriority = minimumPriority;
  }

  public int recalculatePriority(final MessageMetadata messageMetadata) {
    final int retryCount = messageMetadata.getRetryCount();
    final int priority = messageMetadata.getMessagePriority();

    if (this.decreaseEnabled && (retryCount > 0) && retryCount % this.decreaseInterval == 0) {
      return Math.max(priority - this.decreaseValue, this.minimumPriority);
    }

    return priority;
  }
}
