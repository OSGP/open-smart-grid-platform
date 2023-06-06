// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.kafka.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jKafkaLogger implements KafkaLogger {

  private static final String NEWLINE = System.lineSeparator();
  private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jKafkaLogger.class);

  @Override
  public void log(final ConsumerRecord<?, ?> consumerRecord) {
    final String topic = consumerRecord.topic();
    final int partition = consumerRecord.partition();
    final String timestampType = consumerRecord.timestampType().name;
    final long timestamp = consumerRecord.timestamp();
    final ZonedDateTime timestampDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    final Object key = consumerRecord.key();
    final Object value = consumerRecord.value();
    LOGGER.info(
        "Received consumer record with:{}  topic = {}{}  partition = {}{}  timestamp = {} ({} {}){}  key = {}{}  value = {}",
        NEWLINE,
        topic,
        NEWLINE,
        partition,
        NEWLINE,
        timestamp,
        timestampType,
        timestampDateTime,
        NEWLINE,
        key,
        NEWLINE,
        value);
  }
}
