// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class AbstractMessageConsumer {

  protected ConsumerRecord<String, String> consumerRecord;
  private final long waitFailMillis;

  protected AbstractMessageConsumer(final long waitFailMillis) {
    this.waitFailMillis = waitFailMillis;
  }

  public void checkKafkaOutput(final String expectedMessage) {

    final long startTime = System.currentTimeMillis();
    long remaining = this.waitFailMillis;

    while (remaining > 0 && this.waitForConsumerRecord(expectedMessage)) {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (final InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      final long elapsed = System.currentTimeMillis() - startTime;
      remaining = this.waitFailMillis - elapsed;
    }
    assertThat(this.consumerRecord).isNotNull();
    final String message = this.consumerRecord.value();
    assertThat(message).isEqualTo(expectedMessage);
  }

  private boolean waitForConsumerRecord(final String expectedMessage) {
    return this.consumerRecord == null || !expectedMessage.equals(this.consumerRecord.value());
  }
}
