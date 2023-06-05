// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.kafka.in;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StringMessageConsumer extends AbstractMessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageConsumer.class);

  protected StringMessageConsumer(
      @Value("${distributionautomation.kafka.consumer.wait.fail.duration:90000}")
          final long waitFailMillis) {
    super(waitFailMillis);
  }

  @KafkaListener(
      containerFactory = "distributionAutomationMessageKafkaListenerContainerFactory",
      topics = "${distributionautomation.kafka.topic.message}")
  public void listen(final ConsumerRecord<String, String> consumerRecord) {
    LOGGER.info("received consumerRecord");
    this.consumerRecord = consumerRecord;
  }
}
