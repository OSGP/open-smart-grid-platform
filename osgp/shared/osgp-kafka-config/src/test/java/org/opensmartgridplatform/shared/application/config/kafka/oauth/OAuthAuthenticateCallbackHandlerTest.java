// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import static org.junit.jupiter.api.Assertions.*;
import static org.opensmartgridplatform.shared.application.config.kafka.oauth.OAuthAuthenticateCallbackHandler.*;

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

  private Map<String, Object> correctProperties() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(CLIENT_ID_CONFIG, "client-id");
    properties.put(TOKEN_ENDPOINT_CONFIG, "https://token.server.com");
    properties.put(TOKEN_FILE_CONFIG, "file");
    properties.put(SCOPE_CONFIG, "scope-one,scope-two");
    return properties;
  }

  @Test
  void successfulConfigure() {
    final Map<String, Object> properties = correctProperties();

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    handler.setFields(properties);

    assertEquals("client-id", handler.clientId);
    assertEquals("file", handler.tokenFilePath);
    assertEquals("https://token.server.com", handler.tokenEndPoint);
    assertEquals(new HashSet<>(Arrays.asList("scope-one", "scope-two")), handler.scope);
  }

  @Test
  void noClientIdConfigured() {
    final Map<String, Object> properties = correctProperties();
    properties.remove(CLIENT_ID_CONFIG);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));

    assertEquals("Kafka property: clientId, not supplied", kafkaException.getMessage());
  }

  @Test
  void noTokenEndpointConfigured() {
    final Map<String, Object> properties = correctProperties();
    properties.remove(TOKEN_ENDPOINT_CONFIG);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));
    assertEquals("Kafka property: tokenEndpoint, not supplied", kafkaException.getMessage());
  }

  @Test
  void noFileConfigured() {
    final Map<String, Object> properties = correctProperties();
    properties.remove(TOKEN_FILE_CONFIG);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));
    assertEquals("Kafka property: tokenFile, not supplied", kafkaException.getMessage());
  }

  @Test
  void noScopeConfigured() {
    final Map<String, Object> properties = correctProperties();
    properties.remove(SCOPE_CONFIG);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));
    assertEquals("Kafka property: scope, not supplied", kafkaException.getMessage());
  }

  @Test
  void incorrectConfig() {
    final Map<String, Object> properties = correctProperties();
    properties.replace(SCOPE_CONFIG, new String[] {"one", "two"});

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));
    assertEquals("Kafka property: scope, is not of type String", kafkaException.getMessage());
  }

  @Test
  void nullConfig() {
    final Map<String, Object> properties = correctProperties();
    properties.replace(SCOPE_CONFIG, null);

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();

    KafkaException kafkaException =
        assertThrows(KafkaException.class, () -> handler.setFields(properties));
    assertEquals("Kafka property: scope, is null", kafkaException.getMessage());
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

    final Map<String, Object> properties = correctProperties();

    handler.setFields(properties);

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
