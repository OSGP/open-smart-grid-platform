/*
 * Copyright 2022 Alliander N.V.
 */

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

import static org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig.KAFKA_OAUTH_CLIENT_ID_CONFIG;
import static org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig.KAFKA_OAUTH_SCOPE_CONFIG;
import static org.opensmartgridplatform.shared.application.config.kafka.oauth.KafkaOAuthConfig.KAFKA_OAUTH_TOKEN_FILE_CONFIG;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientAssertion;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthenticateCallbackHandler implements AuthenticateCallbackHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OAuthAuthenticateCallbackHandler.class);

  protected String tokenFilePath;
  protected String clientId;
  protected Set<String> scope;

  @Override
  public void configure(
      final Map<String, ?> configs,
      final String saslMechanism,
      final List<AppConfigurationEntry> jaasConfigEntries) {
    this.clientId = getProperty(KAFKA_OAUTH_CLIENT_ID_CONFIG, configs);
    this.scope =
        Arrays.stream(getProperty(KAFKA_OAUTH_SCOPE_CONFIG, configs).split(","))
            .collect(Collectors.toSet());
    this.tokenFilePath = getProperty(KAFKA_OAUTH_TOKEN_FILE_CONFIG, configs);
  }

  private String getProperty(final String propertyName, final Map<String, ?> configs) {
    if (!configs.containsKey(propertyName)) {
      throw new ConfigException("Kafka property: '" + propertyName + "' not supplied");
    }
    if (configs.get(propertyName) == null) {
      throw new ConfigException("Kafka property: '" + propertyName + "' is null");
    }
    if (!(configs.get(propertyName) instanceof String)) {
      throw new ConfigException("Kafka property: '" + propertyName + "' is not of type String");
    }
    return (String) configs.get(propertyName);
  }

  @Override
  public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (final Callback callback : callbacks) {
      if (callback instanceof OAuthBearerTokenCallback) {
        final OAuthBearerToken token = this.getToken();
        ((OAuthBearerTokenCallback) callback).token(token);
      } else if (callback instanceof OAuthBearerValidatorCallback) {
        LOGGER.info("Validate callback");
        throw new UnsupportedCallbackException(callback, "Validate not yet implemented");
      } else {
        throw new UnsupportedCallbackException(
            callback, "Unknown callback type " + callback.getClass().getName());
      }
    }
  }

  /** Retrieves a new JWT token from Azure Active Directory. */
  protected OAuthBearerToken getToken() {
    try {
      String token = readTokenFile(tokenFilePath);
      final IClientAssertion credential = ClientCredentialFactory.createFromClientAssertion(token);
      ClientCredentialParameters aadParameters = ClientCredentialParameters.builder(scope).build();
      ConfidentialClientApplication aadClient =
          ConfidentialClientApplication.builder(clientId, credential).build();
      final IAuthenticationResult authResult = aadClient.acquireToken(aadParameters).get();

      return new BasicOAuthBearerToken(
          authResult.accessToken(),
          scope,
          authResult.expiresOnDate().toInstant().toEpochMilli(),
          aadClient.clientId(),
          System.currentTimeMillis());
    } catch (final Exception e) {
      throw new KafkaOAuthException("Caught an exception while retrieving JWT token", e);
    }
  }

  /**
   * Reads the content of the token file
   *
   * @return content of the token file
   */
  protected static String readTokenFile(final String tokenFilePath) {
    try {
      File tokenFile = new File(tokenFilePath);
      final byte[] bytes = Files.readAllBytes(tokenFile.toPath());
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new KafkaOAuthException("Could not read Token file from: " + tokenFilePath, e);
    }
  }

  @Override
  public void close() {
    // No need to close an oauth session, the token will expire automatically
  }
}
