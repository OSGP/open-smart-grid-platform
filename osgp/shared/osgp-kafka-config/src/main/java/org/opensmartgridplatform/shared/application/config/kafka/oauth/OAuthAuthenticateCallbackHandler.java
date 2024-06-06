// Copyright 2022 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.kafka.oauth;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthenticateCallbackHandler implements AuthenticateCallbackHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OAuthAuthenticateCallbackHandler.class);

  public static final String CLIENT_ID_CONFIG = "clientId";
  public static final String CLIENT_ID_DOC = "Client id of the azure ad OAuth client";
  public static final String TOKEN_ENDPOINT_CONFIG = "tokenEndpoint";
  public static final String TOKEN_ENDPOINT_DOC = "Token endpoint of the azure ad OAuth client";
  public static final String SCOPE_CONFIG = "scope";
  public static final String SCOPE_DOC = "Scope of the OAuth JWT token";
  public static final String TOKEN_FILE_CONFIG = "tokenFile";
  public static final String TOKEN_FILE_DOC =
      "Path of the file containing the token needed for retrieving the OAuth JWT token";

  protected String tokenFilePath;
  protected String tokenEndPoint;
  protected String clientId;
  protected Set<String> scope;

  @Override
  public void configure(
      final Map<String, ?> configs,
      final String saslMechanism,
      final List<AppConfigurationEntry> jaasConfigEntries) {
    Map<String, Object> options = getOptions(saslMechanism, jaasConfigEntries);
    setFields(options);
  }

  void setFields(final Map<String, ?> options) {
    this.clientId = getProperty(CLIENT_ID_CONFIG, options);
    this.tokenEndPoint = getProperty(TOKEN_ENDPOINT_CONFIG, options);
    this.scope =
        Arrays.stream(getProperty(SCOPE_CONFIG, options).split(",")).collect(Collectors.toSet());
    this.tokenFilePath = getProperty(TOKEN_FILE_CONFIG, options);
  }

  private Map<String, Object> getOptions(
      String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries) {
    if (!OAuthBearerLoginModule.OAUTHBEARER_MECHANISM.equals(saslMechanism))
      throw new IllegalArgumentException(
          String.format("Unexpected SASL mechanism: %s", saslMechanism));

    if (Objects.requireNonNull(jaasConfigEntries).size() != 1 || jaasConfigEntries.get(0) == null)
      throw new IllegalArgumentException(
          String.format(
              "Must supply exactly 1 non-null JAAS mechanism configuration (size was %d)",
              jaasConfigEntries.size()));

    return Collections.unmodifiableMap(jaasConfigEntries.get(0).getOptions());
  }

  private String getProperty(final String propertyName, final Map<String, ?> properties) {
    if (!properties.containsKey(propertyName)) {
      throw new ConfigException(String.format("Kafka property: %s, not supplied", propertyName));
    }
    if (properties.get(propertyName) == null) {
      throw new ConfigException(String.format("Kafka property: %s, is null", propertyName));
    }
    if (!(properties.get(propertyName) instanceof String)) {
      throw new ConfigException(
          String.format("Kafka property: %s, is not of type String", propertyName));
    }
    return (String) properties.get(propertyName);
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
      LOGGER.debug("Retrieving Kafka OAuth Token");

      String token = readTokenFile(tokenFilePath);
      final IClientAssertion credential = ClientCredentialFactory.createFromClientAssertion(token);
      ClientCredentialParameters aadParameters = ClientCredentialParameters.builder(scope).build();
      ConfidentialClientApplication aadClient =
          ConfidentialClientApplication.builder(clientId, credential)
              .authority(tokenEndPoint)
              .build();
      final IAuthenticationResult authResult = aadClient.acquireToken(aadParameters).get();

      return new BasicOAuthBearerToken(
          authResult.accessToken(),
          scope,
          authResult.expiresOnDate().toInstant().toEpochMilli(),
          aadClient.clientId(),
          System.currentTimeMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new KafkaOAuthException("Retrieving JWT token was interrupted", e);
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
