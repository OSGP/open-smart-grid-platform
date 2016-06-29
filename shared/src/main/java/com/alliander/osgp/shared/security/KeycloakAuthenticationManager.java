/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alliander.osgp.shared.usermanagement.AuthenticationClient;
import com.alliander.osgp.shared.usermanagement.KeycloakClient;
import com.alliander.osgp.shared.usermanagement.KeycloakClientException;
import com.alliander.osgp.shared.usermanagement.LoginRequest;
import com.alliander.osgp.shared.usermanagement.LoginResponse;

public class KeycloakAuthenticationManager implements AuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakAuthenticationManager.class);

    private static final String NULL_AUTHENTICATION = "null authentication with login from Identity Provider";
    private static final String LOGIN_ATTEMPT_FAILED = "login attempt from Identity Provider failed";
    private static final String LOGIN_RESPONSE_IS_NULL = "response on login from Identity Provider is null";
    private static final String LOGIN_RESPONSE_IS_NOT_OK = "response on login from Identity Provider is not OK";
    private static final String OK = "OK";

    private final String application;
    private final String mellonSharedSecret;
    private final AuthenticationClient authenticationClient;
    private final KeycloakClient keycloakClient;

    public KeycloakAuthenticationManager(final AuthenticationClient authenticationClient,
            final KeycloakClient keycloakClient, final String mellonSharedSecret, final String application) {
        this.authenticationClient = authenticationClient;
        this.keycloakClient = keycloakClient;
        this.mellonSharedSecret = mellonSharedSecret;
        this.application = application;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) {
        if (authentication == null) {
            LOGGER.debug(NULL_AUTHENTICATION);
            throw new BadCredentialsException(NULL_AUTHENTICATION);
        }

        final String username = authentication.getName();

        final LoginRequest loginRequest = new LoginRequest(username, null, this.application);
        LoginResponse loginResponse = null;

        try {
            loginResponse = this.authenticationClient.loginMellon(loginRequest, this.mellonSharedSecret);
        } catch (final Exception e) {
            LOGGER.debug(LOGIN_ATTEMPT_FAILED, e);
            throw new BadCredentialsException(LOGIN_ATTEMPT_FAILED, e);
        }

        if (loginResponse == null) {
            LOGGER.debug(LOGIN_RESPONSE_IS_NULL);
            throw new BadCredentialsException(LOGIN_RESPONSE_IS_NULL);
        }

        if (!loginResponse.getFeedbackMessage().equals(OK)) {
            LOGGER.debug(LOGIN_RESPONSE_IS_NOT_OK);
            throw new BadCredentialsException(LOGIN_RESPONSE_IS_NOT_OK);
        }

        return this.createCustomAuthenticationInstance(username, loginResponse);
    }

    /**
     * Logout the Keycloak user session with the login client for a user with
     * the given username.
     *
     * @param mellonUsername
     *            a username obtained from Mellon, which should be an existing
     *            Keycloak username.
     * @throws KeycloakClientException
     */
    public void logout(final String mellonUsername) throws KeycloakClientException {
        this.keycloakClient.removeUserSessionByUsername(mellonUsername);
    }

    private CustomAuthentication createCustomAuthenticationInstance(final String username,
            final LoginResponse loginResponse) {

        final CustomAuthentication customAuthentication = new CustomAuthentication();
        customAuthentication.setAuthenticated(true);
        customAuthentication.setUserName(username);
        customAuthentication.setOrganisationIdentification(loginResponse.getOrganisationIdentification());
        customAuthentication.setDomains(loginResponse.getDomains());
        customAuthentication.getAuthorities().clear();
        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(loginResponse.getRole());
        customAuthentication.getAuthorities().add(grantedAuthority);
        customAuthentication.setApplications(loginResponse.getApplications());
        customAuthentication.setToken(loginResponse.getToken());

        return customAuthentication;
    }
}
