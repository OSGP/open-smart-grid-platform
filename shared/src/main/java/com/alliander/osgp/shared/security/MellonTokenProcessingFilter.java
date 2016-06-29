/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.alliander.osgp.shared.usermanagement.KeycloakClientException;

public class MellonTokenProcessingFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MellonTokenProcessingFilter.class);

    private final boolean useMellonForUserIdentity;
    private final String httpHeaderForUsername;
    private final String httpHeaderForFrontendToken;
    private final String logoutUrl;
    private final KeycloakAuthenticationManager authenticationManager;

    public MellonTokenProcessingFilter(final boolean useMellonForUserIdentity, final String httpHeaderForUsername,
            final String httpHeaderForFrontendToken, final String logoutUrl,
            final KeycloakAuthenticationManager authenticationManager) {
        this.useMellonForUserIdentity = useMellonForUserIdentity;
        this.httpHeaderForUsername = httpHeaderForUsername;
        this.httpHeaderForFrontendToken = httpHeaderForFrontendToken;
        this.logoutUrl = logoutUrl;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        if (!this.useMellonForUserIdentity) {
            LOGGER.debug("Skipping Mellon token processing, since Mellon is not configured to be used.");
            chain.doFilter(request, response);
            return;
        }

        if (!(request instanceof HttpServletRequest)) {
            throw new IOException("Expecting a HTTP request");
        }

        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        /*
         * If this point in code is reached, the Apache module auth_mellon
         * should be configured, and has allowed the application to be reached.
         * The username from the HTTP header (configured as
         * httpHeaderForUsername) belongs with a user that has authenticated
         * with the 3rd party Identity Provider (IdP) Keycloak.
         *
         * Checks to be performed are if a user with the provided username
         * exists as an active user for this application, and if so, the
         * organisation, domains and authorities with the user need to be
         * retrieved without further checking of any password.
         */

        final String username = httpRequest.getHeader(this.httpHeaderForUsername);

        if (StringUtils.isEmpty(username) || "(null)".equals(username)) {
            LOGGER.error("MellonTokenProcessingFilter with mellon configured, no HTTP header \""
                    + this.httpHeaderForUsername + "\" for Mellon with a username.");
            chain.doFilter(request, response);
            return;
        }

        if (this.isLogoutRequest(httpRequest)) {
            this.logoutMellon((HttpServletResponse) response, username);
            return;
        }

        LOGGER.info("Validating login for user {} based on input via Mellon", username);

        final Authentication authenticationFromSecurityContext = SecurityContextHolder.getContext().getAuthentication();
        if (authenticationFromSecurityContext instanceof CustomAuthentication) {
            final CustomAuthentication existingAuthentication = (CustomAuthentication) authenticationFromSecurityContext;
            if (username.equals(existingAuthentication.getName())) {
                LOGGER.info(
                        "SecurityContext already has an authentication for user {}, stop further Mellon authentication",
                        username);
                this.includeFrontendHeader(response, existingAuthentication);
                chain.doFilter(request, response);
                return;
            }
            LOGGER.warn("Remove authentication for user {} from SecurityContext, continue authenticating user {}",
                    existingAuthentication.getName(), username);
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        final CustomAuthentication mellonAuthentication = new CustomAuthentication();
        mellonAuthentication.setUserName(username);
        try {
            final Authentication authentication = this.authenticationManager.authenticate(mellonAuthentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.includeFrontendHeader(response, authentication);
        } catch (final Exception e) {
            LOGGER.warn("Failed to login based on mellon username: '{}'", username, e);
        }

        chain.doFilter(request, response);
    }

    private void includeFrontendHeader(final ServletResponse response, final Authentication authentication) {
        if (StringUtils.isBlank(this.httpHeaderForFrontendToken) || !(authentication instanceof CustomAuthentication)
                || !(response instanceof HttpServletResponse)) {
            return;
        }
        ((HttpServletResponse) response).setHeader(this.httpHeaderForFrontendToken,
                ((CustomAuthentication) authentication).getToken());
    }

    private void logoutMellon(final HttpServletResponse httpResponse, final String username) throws ServletException,
            IOException {

        try {
            this.authenticationManager.logout(username);
        } catch (final KeycloakClientException e) {
            LOGGER.error("Error logging user '{}' out with the Keycloak API.", username, e);
        }

        httpResponse.sendRedirect(this.logoutUrl);
    }

    private boolean isLogoutRequest(final HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().equals(httpRequest.getServletContext().getContextPath() + "/logout");
    }
}
