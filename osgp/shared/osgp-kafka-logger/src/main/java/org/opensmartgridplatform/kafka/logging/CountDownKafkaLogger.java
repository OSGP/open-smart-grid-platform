//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.kafka.logging;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * KafkaLogger implementation that can be used in tests to verify it is called, and only called with
 * some expected value in the consumer record.
 *
 * <p>A typical way to use it, is to create the following beans in the Spring test context:<br>
 *
 * <ul>
 *   <li>a bean containing the expected value
 *   <li>a bean containing a count down latch with the number of times the log method should be
 *       called (typically once)
 *   <li>a bean with this implementation as the KafkaLogger, taking in the bean values of the count
 *       down latch and expected value as parameters
 * </ul>
 *
 * The test case can auto-wire the count down latch and the expected value.<br>
 * The expected value can be used by a test producer to send it to the topic for the consumer.<br>
 * The count down latch can be used to assert that the KafkaLogger was called with the expected
 * value by awaiting the count down to zero.
 */
public class CountDownKafkaLogger extends Slf4jKafkaLogger {

  private final CountDownLatch countDownLatch;
  private final Object expectedValue;

  public CountDownKafkaLogger(final CountDownLatch countDownLatch, final Object expectedValue) {
    this.countDownLatch = countDownLatch;
    this.expectedValue = expectedValue;
  }

  @Override
  public void log(final ConsumerRecord<?, ?> consumerRecord) {
    super.log(consumerRecord);
    if (Objects.equals(this.expectedValue, consumerRecord.value())) {
      this.countDownLatch.countDown();
    } else {
      throw new AssertionError(
          "Unexpected value:"
              + System.lineSeparator()
              + consumerRecord.value()
              + System.lineSeparator()
              + "expected:"
              + System.lineSeparator()
              + this.expectedValue);
    }
  }
}
