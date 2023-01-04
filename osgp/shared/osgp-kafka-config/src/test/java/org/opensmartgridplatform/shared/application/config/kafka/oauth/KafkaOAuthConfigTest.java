/*
 * Copyright 2023 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

class KafkaOAuthConfigTest {

  @Test
  void producerConfigDef() {
    ConfigDef producerConfigDef = KafkaOAuthConfig.producerConfigDef();

    // OAuth
    assertTrue(
        producerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG));
    assertTrue(
        producerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG));
    assertTrue(
        producerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG));

    // Producer
    assertTrue(
        producerConfigDef.configKeys().containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
  }

  @Test
  void consumerConfigDef() {
    ConfigDef consumerConfigDef = KafkaOAuthConfig.consumerConfigDef();

    // OAuth
    assertTrue(
        consumerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG));
    assertTrue(
        consumerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG));
    assertTrue(
        consumerConfigDef.configKeys().containsKey(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG));

    // Consumer
    assertTrue(
        consumerConfigDef.configKeys().containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
  }
}
