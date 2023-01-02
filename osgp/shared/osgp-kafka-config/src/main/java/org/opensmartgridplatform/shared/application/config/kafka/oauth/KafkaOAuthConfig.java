/*
 * Copyright 2022 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;

public class KafkaOAuthConfig {

  private static final ConfigDef PRODUCER_CONFIG;

  private static final ConfigDef CONSUMER_CONFIG;

  public static final String KAFKA_OAUTH_CLIENT_ID_CONFIG = "oauth.client.id";
  public static final String KAFKA_OAUTH_CLIENT_ID_DOC = "Client id of the azure ad OAuth client";

  public static final String KAFKA_OAUTH_SCOPE_CONFIG = "oauth.scope";
  public static final String KAFKA_OAUTH_SCOPE_DOC = "Scope of the OAuth JWT token";

  public static final String KAFKA_OAUTH_TOKEN_FILE_CONFIG = "oauth.token.file";
  public static final String KAFKA_OAUTH_TOKEN_FILE_DOC =
      "Path of the file containing the token needed for retrieving the OAuth JWT token";

  static {
    PRODUCER_CONFIG = addKafkaConfig(ProducerConfig.configDef());
    CONSUMER_CONFIG = addKafkaConfig(ConsumerConfig.configDef());
  }

  private static ConfigDef addKafkaConfig(ConfigDef configDef) {
    return configDef
        .define(
            KAFKA_OAUTH_CLIENT_ID_CONFIG,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.MEDIUM,
            KAFKA_OAUTH_CLIENT_ID_DOC)
        .define(
            KAFKA_OAUTH_SCOPE_CONFIG,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.MEDIUM,
            KAFKA_OAUTH_SCOPE_DOC)
        .define(
            KAFKA_OAUTH_TOKEN_FILE_CONFIG,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.MEDIUM,
            KAFKA_OAUTH_TOKEN_FILE_DOC);
  }

  public static ConfigDef producerConfigDef() {
    return new ConfigDef(PRODUCER_CONFIG);
  }

  public static ConfigDef consumerConfigDef() {
    return new ConfigDef(CONSUMER_CONFIG);
  }
}
