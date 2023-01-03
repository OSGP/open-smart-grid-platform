/*
 * Copyright 2023 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import static org.apache.kafka.common.security.auth.SecurityProtocol.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig.*;
import static org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig.KAFKA_OAUTH_TOKEN_FILE_CONFIG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.security.auth.callback.Callback;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OAuthAuthenticateCallbackHandlerTest {

  @Test
  void successfulConfigure() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    handler.configure(configs, SASL_SSL.name, null);

    assertEquals("client-id", handler.clientId);
    assertEquals("file", handler.tokenFilePath);
    assertEquals(new HashSet<>(Arrays.asList("scope-one", "scope-two")), handler.scope);
  }

  @Test
  void noClientIdConfigured() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.configure(configs, SASL_SSL.name, null));

    assertEquals("Kafka property: 'oauth.client.id' not supplied", kafkaException.getMessage());
  }

  @Test
  void noFileConfigured() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.configure(configs, SASL_SSL.name, null));
    assertEquals("Kafka property: 'oauth.token.file' not supplied", kafkaException.getMessage());
  }

  @Test
  void noScopeConfigured() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.configure(configs, SASL_SSL.name, null));
    assertEquals("Kafka property: 'oauth.scope' not supplied", kafkaException.getMessage());
  }

  @Test
  void incorrectConfig() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, new String[] {"one", "two"});

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.configure(configs, SASL_SSL.name, null));
    assertEquals(
        "Kafka property: 'oauth.scope' is not of type String", kafkaException.getMessage());
  }

  @Test
  void nullConfig() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, null);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.configure(configs, SASL_SSL.name, null));
    assertEquals("Kafka property: 'oauth.scope' is null", kafkaException.getMessage());
  }

  @Test
  void testTokenHandle() {
    OAuthAuthenticateCallbackHandler handler = Mockito.mock(OAuthAuthenticateCallbackHandler.class);
    Mockito.when(handler.getToken())
        .thenReturn(
            new BasicOAuthBearerToken(
                "test-jwt-token",
                new HashSet<>(Arrays.asList("scope-one", "scope-two")),
                100,
                "principal-name",
                10000L));

    final Map<String, Object> configs = new HashMap<>();
    configs.put(KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    handler.configure(configs, SASL_SSL.name, null);

    try {
      OAuthBearerTokenCallback callback = new OAuthBearerTokenCallback();
      handler.handle(new Callback[] {callback});
      assertEquals("test-jwt-token", callback.token().value());
    } catch (Exception ignored) {
    }
  }

  @Test
  void readFile() {
    String testTokenPath =
        OAuthAuthenticateCallbackHandlerTest.class.getResource("/token-file").getPath();
    String token = OAuthAuthenticateCallbackHandler.readTokenFile(testTokenPath);

    assertEquals("test-token-data\n", token);
  }

  @Test
  void readNonExistentFile() {
    KafkaOAuthException kafkaOAuthException =
        assertThrows(
            KafkaOAuthException.class,
            () -> OAuthAuthenticateCallbackHandler.readTokenFile("/non-existent-file"));
    assertEquals(
        "Could not read Token file from: /non-existent-file", kafkaOAuthException.getMessage());
  }
}
