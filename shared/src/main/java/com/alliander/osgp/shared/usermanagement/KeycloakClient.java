/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * This class offers a web client for the Keycloak management API.
 * <p>
 * It offers functionality for logging out users who are authenticated through
 * Keycloak and Apache module auth_mellon, and for user management delegation
 * (keep users in sync with Keycloak, usernames are shared).
 */
public class KeycloakClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClient.class);

    private static final String CONSTRUCTION_FAILED = "KeycloakClient construction failed";
    private static final String LOGIN_FAILED = "login failed";
    private static final String AUTHENTICATE_FAILED = "authenticate failed";
    private static final String RESPONSE_BODY_NOT_JSON = "response body is not JSON data";

    private static final String PATH_ELEMENT_REALM = "{realm}";
    private static final String PATH_ELEMENT_USER_ID = "{userId}";
    private static final String PATH_ELEMENT_REALMS = "/realms/" + PATH_ELEMENT_REALM;
    private static final String PATH_ELEMENT_ADMIN_REALMS = "/admin" + PATH_ELEMENT_REALMS;
    private static final String TOKEN_PATH_TEMPLATE = PATH_ELEMENT_REALMS
            + "/protocol/openid-connect/token";
    private static final String USERS_PATH_TEMPLATE = PATH_ELEMENT_ADMIN_REALMS + "/users";
    private static final String USER_SESSIONS_PATH_TEMPLATE = USERS_PATH_TEMPLATE + "/" + PATH_ELEMENT_USER_ID
            + "/sessions";

    private final String apiClient;
    private final String apiClientSecret;
    private final String apiUser;
    private final String apiPassword;
    private final String loginClient;
    private final String realm;

    private final String tokenPath;
    private final String usersPath;
    private final String userSessionsPath;

    private String currentToken = null;

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
     * @param apiClient
     *            The name of the keycloak client used for API calls.
     * @param apiClientSecret
     *            The configured secret of the keycloak client used for API
     *            calls.
     * @param apiUser
     *            The username that is to be used with the keycloak client for
     *            API calls.
     * @param apiPassword
     *            The password for the user that is to be used with the keycloak
     *            client for API calls.
     * @param loginClient
     *            The keycloak client used for application logins.
     * @param realm
     *            The keycloak realm with clients and users for external logins.
     *
     * @throws KeycloakClientException
     *             In case the construction fails.
     */
    public KeycloakClient(final String keystoreLocation, final String keystorePassword, final String keystoreType,
            final String baseAddress, final String apiClient, final String apiClientSecret, final String apiUser,
            final String apiPassword, final String loginClient, final String realm)
                    throws KeycloakClientException {

        this.apiClient = apiClient;
        this.apiClientSecret = apiClientSecret;
        this.apiUser = apiUser;
        this.apiPassword = apiPassword;
        this.loginClient = loginClient;
        this.realm = realm;

        this.tokenPath = TOKEN_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.usersPath = USERS_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.userSessionsPath = USER_SESSIONS_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);

        InputStream stream = null;
        boolean isClosed = false;
        Exception exception = null;

        try {

            final KeyStore keystore = KeyStore.getInstance(keystoreType.toUpperCase());

            stream = new FileInputStream(keystoreLocation);
            keystore.load(stream, keystorePassword.toCharArray());

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            final List<Object> providers = new ArrayList<Object>();
            providers.add(new JacksonJaxbJsonProvider());

            this.webClient = WebClient.create(baseAddress, providers, true);
            if (this.webClient == null) {
                throw new AuthenticationClientException("webclient is null");
            }

            final ClientConfiguration config = WebClient.getConfig(this.webClient);
            final HTTPConduit conduit = config.getHttpConduit();

            conduit.setTlsClientParameters(new TLSClientParameters());
            conduit.getTlsClientParameters().setTrustManagers(tmf.getTrustManagers());

            this.jacksonObjectMapper = new ObjectMapper();
        } catch (final Exception e) {
            LOGGER.error(CONSTRUCTION_FAILED, e);
            throw new KeycloakClientException(CONSTRUCTION_FAILED, e);
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
            throw new KeycloakClientException(CONSTRUCTION_FAILED, exception);
        }
    }

    private void initializeToken() throws KeycloakClientException {

        final Form form = new Form().param("grant_type", "password").param("username", this.apiUser)
                .param("password", this.apiPassword).param("client_id", this.apiClient)
                .param("client_secret", this.apiClientSecret);

        LOGGER.info("Initializing Keycloak API bearer token.");

        final Response response = this.getWebClientInstance().path(this.tokenPath).form(form);
        final JsonNode jsonNode = this.getJsonResponseBody(response);

        this.checkForError(jsonNode);

        final JsonNode accessToken = jsonNode.get("access_token");

        if (accessToken == null || !accessToken.isTextual()) {
            throw new KeycloakClientException(
                    "Keycloak API access token response does not contain a JSON text field 'access_token'.");
        }

        this.currentToken = accessToken.textValue();
    }

    public String getToken() throws KeycloakClientException {
        synchronized (this) {
            if (this.currentToken == null) {
                this.initializeToken();
            }
            return this.currentToken;
        }
    }

    public void refreshToken() {
        /*
         * The Keycloak API offers the ability to refresh access tokens. For
         * now, it looks simpler to ask a new token when an old one is no longer
         * valid. Set the currentToken to null so the next call to getToken will
         * reinitialize it.
         */
        synchronized (this) {
            this.currentToken = null;
        }
    }

    public String getUserId(final String username) throws KeycloakClientException {

        LOGGER.info("Retrieving Keycloak user ID for user '{}'.", username);

        final Response response = this.withBearerToken(
                this.getWebClientInstance().path(this.usersPath).query("username", username)).get();

        final JsonNode jsonNode = this.getJsonResponseBody(response);

        this.checkForError(jsonNode);

        if (!jsonNode.isArray()) {
            throw new KeycloakClientException("Expected array result from Keycloak API user lookup, got: "
                    + jsonNode.getNodeType().name());
        }
        final ArrayNode jsonArray = (ArrayNode) jsonNode;

        if (jsonArray.size() != 1) {
            throw new KeycloakClientException("Expected 1 array result from Keycloak API user lookup for username '"
                    + username + "', got: " + jsonArray.size());
        }

        final JsonNode userRepresentation = jsonArray.get(0);

        final JsonNode userId = userRepresentation.get("id");

        if (userId == null || !userId.isTextual()) {
            throw new KeycloakClientException(
                    "Keycloak API user representation does not contain a JSON text field 'id'.");
        }

        return userId.textValue();
    }

    public String getUserSessionId(final String userId) throws KeycloakClientException {

        LOGGER.info("Retrieving Keycloak user session for user ID '{}' and for client '{}'.", userId, this.loginClient);

        final Response response = this.withBearerToken(
                this.getWebClientInstance().path(this.userSessionsPath.replace(PATH_ELEMENT_USER_ID, userId))).get();

        final JsonNode jsonNode = this.getJsonResponseBody(response);

        this.checkForError(jsonNode);

        if (!jsonNode.isArray()) {
            throw new KeycloakClientException("Expected array result from Keycloak API user lookup, got: "
                    + jsonNode.getNodeType().name());
        }
        final ArrayNode jsonArray = (ArrayNode) jsonNode;

        if (jsonArray.size() == 0) {
            /*
             * No sessions in Keycloak for the given user. This would be a
             * normal situation when the application login is based only on a
             * Mellon session still active, when there is nog single logout at
             * the end of a Keycloak session.
             */
            return "";
        }

        final Iterator<JsonNode> elements = jsonArray.elements();
        final boolean sessionFound = false;
        while (!sessionFound && elements.hasNext()) {
            final JsonNode sessionRepresentation = elements.next();
            final JsonNode sessionId = sessionRepresentation.get("id");
            if (sessionId == null || !sessionId.isTextual()) {
                LOGGER.warn("sessionId is not a JSON text node for a user session");
                continue;
            }
            final JsonNode clients = sessionRepresentation.get("clients");
            if (clients == null || !clients.isObject()) {
                LOGGER.warn("clients is not a JSON object node for a user session");
                continue;
            }
            final ObjectNode clientsObject = (ObjectNode) clients;
            // TODO iterate over clientsObject.fields();
            // "clients":{"3e653765-8504-4333-a892-1960b1e9fbbd":"login-service"}
            // the field value will have to be a text node that equals the
            // loginClient
            // the field's name for this field is the session ID we are looking
            // for
            return "TODO User session ID for user " + userId;
        }
        // no session found for the user and the login client
        return "";
    }

    public String getUserSessionIdByUsername(final String username) throws KeycloakClientException {
        return this.getUserSessionId(this.getUserId(username));
    }

    private WebClient withBearerToken(final WebClient webClient) throws KeycloakClientException {
        final String bearerToken = "bearer " + this.getToken();
        return webClient.header(HttpHeaders.AUTHORIZATION, bearerToken);
    }

    private JsonNode getJsonResponseBody(final Response response) throws KeycloakClientException {

        if (response == null) {
            throw new KeycloakClientException(RESPONSE_IS_NULL);
        }

        final String responseBody = response.readEntity(String.class);

        if (StringUtils.isEmpty(responseBody)) {
            throw new KeycloakClientException(RESPONSE_BODY_IS_EMPTY);
        }

        try {
            LOGGER.info("Parsing Keycloak API JSON response: {}", responseBody);
            return this.jacksonObjectMapper.reader().readTree(responseBody);
        } catch (final IOException e) {
            LOGGER.error("Error reading JSON data from response body: {}", responseBody, e);
            throw new KeycloakClientException(RESPONSE_BODY_NOT_JSON, e);
        }
    }

    private void checkForError(final JsonNode jsonNode) throws KeycloakClientException {

        final JsonNode error = jsonNode.get("error");
        final JsonNode errorDescription = jsonNode.get("error_description");

        if (error != null || errorDescription != null) {
            final StringBuilder errorBuilder = new StringBuilder();
            if (error != null && error.isTextual()) {
                errorBuilder.append("error: ").append(error.textValue());
            }
            if (errorDescription != null && errorDescription.isTextual()) {
                if (errorBuilder.length() != 0) {
                    errorBuilder.append(", ");
                }
                errorBuilder.append("description: ").append(errorDescription.textValue());
            }
            throw new KeycloakClientException("Keycloak API call returned "
                    + (errorBuilder.length() == 0 ? "error" : errorBuilder));
        }
    }
}
