/*
 * Copyright 2023 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.security.auth.callback.Callback;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OAuthAuthenticateCallbackHandlerTest {

  @Test
  void configure() {
    final Map<String, Object> configs = new HashMap<>();
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    OAuthAuthenticateCallbackHandler handler = new OAuthAuthenticateCallbackHandler();
    handler.configure(configs, SecurityProtocol.SASL_SSL.name, null);

    assertEquals("client-id", handler.clientId);
    assertEquals("file", handler.tokenFilePath);
    assertEquals(new HashSet<>(Arrays.asList("scope-one", "scope-two")), handler.scope);
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
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG, "client-id");
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_TOKEN_FILE_CONFIG, "file");
    configs.put(KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG, "scope-one,scope-two");

    handler.configure(configs, SecurityProtocol.SASL_SSL.name, null);

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
}
