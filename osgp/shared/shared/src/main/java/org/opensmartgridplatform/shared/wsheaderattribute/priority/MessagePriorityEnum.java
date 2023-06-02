//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.wsheaderattribute.priority;

public enum MessagePriorityEnum {
  // Source: http://activemq.apache.org/how-can-i-support-priority-queues.html
  // The full range of priority values (0-9) are supported by the JDBC message
  // store. For KahaDB three priority categories are supported, Low (< 4),
  // Default (= 4) and High (> 4).
  LOW(0),
  DEFAULT(4),
  HIGH(9);

  private int priority;

  private MessagePriorityEnum(final int priority) {
    this.priority = priority;
  }

  public int getPriority() {
    return this.priority;
  }

  public static int getMessagePriority(final String messagePriority) {
    if (messagePriority == null || "".equals(messagePriority)) {
      return MessagePriorityEnum.DEFAULT.getPriority();
    }

    final int messagePriorityValue = Integer.parseInt(messagePriority);

    if (messagePriorityValue < 0) {
      return MessagePriorityEnum.LOW.getPriority();
    }

    if (messagePriorityValue > 9) {
      return MessagePriorityEnum.HIGH.getPriority();
    }

    return messagePriorityValue;
  }
}
