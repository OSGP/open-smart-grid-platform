/*
 * Copyright 2022 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

public class KafkaOauthConfigs {

  public static final String KAFKA_OAUTH_TOKEN_FILE_CONFIG = "oauth.token.file";
  public static final String KAFKA_OAUTH_TOKEN_FILE_DOC =
      "Path of the file containing the token needed for retrieving the OAuth JWT token";

  public static final String KAFKA_OAUTH_SCOPE_CONFIG = "oauth.scope";
  public static final String KAFKA_OAUTH_SCOPE_DOC = "Scope of the OAuth JWT token";

  public static final String KAFKA_OAUTH_CLIENT_ID_CONFIG = "oauth.client.id";
  public static final String KAFKA_OAUTH_CLIENT_ID_DOC = "Client id of the azure ad OAuth client";
}
