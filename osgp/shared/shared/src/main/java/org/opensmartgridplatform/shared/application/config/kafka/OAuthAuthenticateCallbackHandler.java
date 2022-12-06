/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.application.config.kafka;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientAssertion;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAuthenticateCallbackHandler implements AuthenticateCallbackHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OAuthAuthenticateCallbackHandler.class);
  private ClientCredentialParameters aadParameters;

  private ConfidentialClientApplication aadClient;

  @Override
  public void configure(
      final Map<String, ?> configs,
      final String saslMechanism,
      final List<AppConfigurationEntry> jaasConfigEntries) {
    final String tokenFromFile = null;
    final String scope = null;
    final String clientId = null;
    final IClientAssertion credential =
        ClientCredentialFactory.createFromClientAssertion(tokenFromFile);
    this.aadParameters = ClientCredentialParameters.builder(Collections.singleton(scope)).build();
    this.aadClient = ConfidentialClientApplication.builder(clientId, credential).build();
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

  private OAuthBearerToken getToken() {
    try {
      final IAuthenticationResult authResult =
          this.aadClient.acquireToken(this.aadParameters).get();
      return null; // TODO: BasicOAuthBearerToken
    } catch (final InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    throw new RuntimeException("Not implemented");
  }
}
