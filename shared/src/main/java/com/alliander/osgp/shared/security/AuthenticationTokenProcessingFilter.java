/**
 * Copyright 2015 Smart Society Services B.V.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.alliander.osgp.shared.usermanagement.AuthenticationClientException;

/**
 * Checks the Request headers for presence of valid authentication token.
 */
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenProcessingFilter.class);

    public static final String TOKEN_HEADER_KEY = "X-Auth-Token";

    public static final String ORGANISATION_HEADER_KEY = "X-Organisation";

    public static final String TOKEN_PARAM_KEY = "authToken";

    public static final String ORGANISATION_PARAM_KEY = "authOrganisation";

    private final CustomAuthenticationManager authenticationManager;

    public AuthenticationTokenProcessingFilter(final CustomAuthenticationManager customAuthenticationManager) {
        this.authenticationManager = customAuthenticationManager;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)) {
            throw new IOException("Expecting a HTTP request");
        }

        // Read the token from the HTTP Headers
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String authToken = this.getValue(httpRequest, TOKEN_HEADER_KEY, TOKEN_PARAM_KEY);
        final String organisation = this.getValue(httpRequest, ORGANISATION_HEADER_KEY, ORGANISATION_PARAM_KEY);

        if (authToken != null && organisation != null) {
            // Validate the token in the authentication manager
            final CustomAuthentication authentication = new CustomAuthentication();
            authentication.setOrganisationIdentification(organisation);
            authentication.setToken(authToken);

            try {
                // Perform validation and set security context
                this.authenticationManager.validateToken(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Set header with new token (if available)
                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setHeader(TOKEN_HEADER_KEY, authentication.getToken());

            } catch (final AuthenticationClientException e) {
                LOGGER.warn("Failed to validate token", e);
            }
        }

        // Continue with next filter
        chain.doFilter(request, response);
    }

    /**
     * Get value from header or query parameters.
     *
     * @param request
     *            servlet request
     * @return value when found or null otherwise
     */
    private String getValue(final HttpServletRequest request, final String headerKey, final String paramKey) {
        String value = request.getHeader(headerKey);

        // Second fetch from query parameters
        if (value == null) {
            value = request.getParameter(paramKey);
        }

        return value;
    }
}
