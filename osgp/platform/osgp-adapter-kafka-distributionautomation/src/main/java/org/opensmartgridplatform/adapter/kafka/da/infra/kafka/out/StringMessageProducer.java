// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StringMessageProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringMessageProducer.class);

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  public StringMessageProducer(
      @Qualifier("distributionAutomationMessageKafkaTemplate")
          final KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(final String message) {
    LOGGER.info("StringMessageProducer.send is called with message: {}", message);
    /*
     * No need for callback functionality now; by default, the template is configured with a
     * LoggingProducerListener, which logs errors and does nothing when the send is successful.
     */
    this.kafkaTemplate.sendDefault(message);
  }
}
