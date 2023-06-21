// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.kafka.logging;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaLogger {
  void log(ConsumerRecord<?, ?> consumerRecord);
}
