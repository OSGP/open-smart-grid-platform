/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig;

public class KafkaProperties {

  private KafkaProperties() {
    // hide implicit constructor
  }

  public static Map<String, Class<?>> commonProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, String.class);

    map.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, String.class);

    map.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, String.class);
    map.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, String.class);
    map.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, String.class);
    map.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, String.class);
    map.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, String.class);
    map.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, String.class);
    map.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, String.class);
    map.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, String.class);
    map.put(SslConfigs.SSL_PROTOCOL_CONFIG, String.class);
    map.put(SslConfigs.SSL_PROVIDER_CONFIG, String.class);
    map.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, String.class);

    map.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, String.class);
    map.put(SaslConfigs.SASL_JAAS_CONFIG, String.class);
    map.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, String.class);
    map.put(SaslConfigs.SASL_LOGIN_CALLBACK_HANDLER_CLASS, String.class);
    map.put(SaslConfigs.SASL_LOGIN_CLASS, String.class);
    map.put(SaslConfigs.SASL_MECHANISM, String.class);

    map.put(KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG, String.class);
    map.put(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG, String.class);
    map.put(KafkaOAuthConfig.KAFKA_OAUTH_TOKEN_FILE_CONFIG, String.class);
    return map;
  }

  public static Map<String, Class<?>> producerProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, String.class);
    map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, String.class);
    map.put(ProducerConfig.ACKS_CONFIG, String.class);
    map.put(ProducerConfig.BUFFER_MEMORY_CONFIG, Long.class);
    map.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, String.class);
    map.put(ProducerConfig.RETRIES_CONFIG, Integer.class);
    map.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.class);
    map.put(ProducerConfig.CLIENT_ID_CONFIG, String.class);
    map.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, Integer.class);
    map.put(ProducerConfig.LINGER_MS_CONFIG, Integer.class);
    map.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.class);
    map.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, Integer.class);
    map.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, String.class);
    map.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, String.class);
    map.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, Long.class);
    map.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, Integer.class);
    map.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, Integer.class);
    map.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Integer.class);
    return map;
  }

  public static Map<String, Class<?>> consumerProperties() {
    final HashMap<String, Class<?>> map = new HashMap<>();
    map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, String.class);
    map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, String.class);
    map.put(ConsumerConfig.GROUP_ID_CONFIG, String.class);
    map.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, Integer.class);
    map.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, Integer.class);
    map.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, Integer.class);
    map.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.class);
    map.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, Boolean.class);
    map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, String.class);
    map.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, Integer.class);
    map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.class);
    map.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, Boolean.class);
    map.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, Integer.class);
    map.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, String.class);
    map.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, String.class);
    map.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.class);
    map.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.class);
    map.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, String.class);
    map.put(ConsumerConfig.SEND_BUFFER_CONFIG, Integer.class);
    map.put(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG, String.class);
    map.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, Long.class);
    map.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, Integer.class);
    map.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, Integer.class);
    return map;
  }
}
