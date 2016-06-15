/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * This class offers a web client for the web-api-user-management web service.
 * Login and authenticate functionality.
 */
public class AuthenticationClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationClient.class);

    private static final String CONSTRUCTION_FAILED = "AuthenticationClient construction failed";
    private static final String LOGIN_FAILED = "login failed";
    private static final String AUTHENTICATE_FAILED = "authenticate failed";

    /*
     * To login: POST to /login JSON like:
     * {"username":"myname","password":"mypass","application":"myapp"}
     */
    private String loginPath = "/login";

    /*
     * To login: POST to /login-mellon JSON like:
     * {"username":"myname","password":"ignored","application":"myapp"} with
     * header value token = "the mellon shared secret"
     */
    private String loginMellonPath = "/login-mellon";

    /*
     * To authenticate: GET to /authenticate with proper header values like:
     * requestingOrgId = "some org id" and token = "the security token"
     */
    private String authenticatePath = "/authenticate";

    /**
     * Construct an AuthenticationClient instance.
     *
     * @param keystoreLocation
     *            The location of the key store.
     * @param keystorePassword
     *            The password for the key store.
     * @param keystoreType
     *            The type of the key store.
     * @param baseAddress
     *            The base address or URL for the AuthenticationClient.
     *
     * @throws AuthenticationClientException
     *             In case the construction fails, an
     *             AuthenticationClientException will be thrown.
     */
    public AuthenticationClient(final String keystoreLocation, final String keystorePassword,
            final String keystoreType, final String baseAddress) throws AuthenticationClientException {

        InputStream stream = null;
        boolean isClosed = false;
        Exception exception = null;

        try {
            // Create the KeyStore.
            final KeyStore keystore = KeyStore.getInstance(keystoreType.toUpperCase());

            stream = new FileInputStream(keystoreLocation);
            keystore.load(stream, keystorePassword.toCharArray());

            // Create TrustManagerFactory and initialize it using the KeyStore.
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            // Create Apache CXF WebClient with JSON provider.
            final List<Object> providers = new ArrayList<Object>();
            providers.add(new JacksonJaxbJsonProvider());

            this.webClient = WebClient.create(baseAddress, providers, true);
            if (this.webClient == null) {
                throw new AuthenticationClientException("webclient is null");
            }

            // Set up the HTTP Conduit to use the TrustManagers.
            final ClientConfiguration config = WebClient.getConfig(this.webClient);
            final HTTPConduit conduit = config.getHttpConduit();

            conduit.setTlsClientParameters(new TLSClientParameters());
            conduit.getTlsClientParameters().setTrustManagers(tmf.getTrustManagers());

            this.jacksonObjectMapper = new ObjectMapper();
        } catch (final Exception e) {
            LOGGER.error(CONSTRUCTION_FAILED, e);
            throw new AuthenticationClientException(CONSTRUCTION_FAILED, e);
        } finally {
            try {
                stream.close();
                isClosed = true;
            } catch (final Exception streamCloseException) {
                LOGGER.error(CONSTRUCTION_FAILED, streamCloseException);
                exception = streamCloseException;
            }
        }

        if (!isClosed) {
            throw new AuthenticationClientException(CONSTRUCTION_FAILED, exception);
        }
    }

    /**
     * Login to the user management API.
     *
     * @param loginRequest
     *            The LoginRequest containing user name and password.
     *
     * @return A LoginResponse containing feedback message, error message and
     *         authentication token.
     *
     * @throws AuthenticationClientException
     *             In case the LoginRequest argument is null, the user name or
     *             password are an empty string, the response is null, the HTTP
     *             status code is not equal to 200 OK or if the response body is
     *             empty.
     */
    public LoginResponse login(final LoginRequest loginRequest) throws AuthenticationClientException {

        LoginResponse loginResponse = null;

        try {
            // Check the input parameters.
            if (loginRequest == null) {
                throw new AuthenticationClientException("loginRequest is null");
            }

            if (StringUtils.isEmpty(loginRequest.getUsername())) {
                throw new AuthenticationClientException("username is empty");
            }

            if (StringUtils.isEmpty(loginRequest.getPassword())) {
                throw new AuthenticationClientException("password is empty");
            }

            // Pause for 2 seconds to annoy brute force attempts.
            Thread.sleep(2000);

            // Call the User Management API.
            final Response response = this.getWebClientInstance().path(this.loginPath).post(loginRequest);

            // Check the response.
            final String body = this.checkResponse(response);

            // Map the body to the LoginResponse.
            loginResponse = this.jacksonObjectMapper.readValue(body, LoginResponse.class);
        } catch (final Exception e) {
            LOGGER.error(LOGIN_FAILED, e);
            throw new AuthenticationClientException(LOGIN_FAILED, e);
        }

        return loginResponse;
    }

    /**
     * Login to the user management API with username from Mellon based on
     * external Identity Provider login.
     *
     * @param loginRequest
     *            The LoginRequest containing user name and application, any
     *            password is ignored.
     * @param secret
     *            a shared secret to communicate with REST APIs regarding an
     *            externally authenticated user whose username is received
     *            through Mellon.
     *
     * @return A LoginResponse containing feedback message, error message and
     *         authentication token.
     *
     * @throws AuthenticationClientException
     *             In case the LoginRequest argument is null, the user name or
     *             application are an empty string, the response is null, the
     *             HTTP status code is not equal to 200 OK or if the response
     *             body is empty.
     */
    public LoginResponse loginMellon(final LoginRequest loginRequest, final String secret)
            throws AuthenticationClientException {

        LoginResponse loginResponse = null;

        try {
            if (loginRequest == null) {
                throw new AuthenticationClientException("loginRequest is null");
            }

            if (StringUtils.isEmpty(loginRequest.getUsername())) {
                throw new AuthenticationClientException("username is empty");
            }

            // Pause for 0.1 seconds to annoy brute force attempts.
            Thread.sleep(100);

            final Response response = this.getWebClientInstance().path(this.loginMellonPath)
                    .header(HEADER_PARAM_TOKEN, secret).post(loginRequest);

            final String body = this.checkResponse(response);

            loginResponse = this.jacksonObjectMapper.readValue(body, LoginResponse.class);
        } catch (final Exception e) {
            LOGGER.error(LOGIN_FAILED, e);
            throw new AuthenticationClientException(LOGIN_FAILED, e);
        }

        return loginResponse;
    }

    /**
     * Check the validity of an authentication token.
     *
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return An AuthenticationResponse containing feedback message, error
     *         message and new authentication token.
     *
     * @throws AuthenticationClientException
     *             In case the organisationIdentification or token are an empty
     *             string, the token is not valid, the response is null, the
     *             HTTP status code is not equal to 200 OK or if the response
     *             body is empty.
     */
    public AuthenticationResponse authenticate(final String organisationIdentification, final String token)
            throws AuthenticationClientException {

        AuthenticationResponse authenticationResponse = null;

        try {
            // Check the input parameters.
            if (StringUtils.isEmpty(organisationIdentification)) {
                throw new AuthenticationClientException("organisationIdentification is empty");
            }

            if (StringUtils.isEmpty(token)) {
                throw new AuthenticationClientException("token is empty");
            }

            // Pause for 0,1 seconds to annoy brute force attempts.
            Thread.sleep(100);

            // Call the User Management API.
            final Response response = this.getWebClientInstance().path(this.authenticatePath)
                    .headers(this.createHeaders(organisationIdentification, token)).get();

            // Check the response.
            final String body = this.checkResponse(response);

            // Map the body to the AuthenticationResponse.
            authenticationResponse = this.jacksonObjectMapper.readValue(body, AuthenticationResponse.class);
        } catch (final Exception e) {
            LOGGER.error(AUTHENTICATE_FAILED, e);
            throw new AuthenticationClientException(AUTHENTICATE_FAILED, e);
        }

        return authenticationResponse;
    }
}
