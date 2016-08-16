/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.usermanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

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
    private static final String AUTHENTICATE_FAILED = "authenticate failed";
    private static final String NOT_AUTHORIZED = "Access to KeycloakClient not authorized";
    private static final String RESPONSE_BODY_NOT_JSON = "response body is not JSON data";

    private static final String BEARER = "Bearer";

    private static final String PATH_ELEMENT_REALM = "{realm}";
    private static final String PATH_ELEMENT_USER_ID = "{userId}";
    private static final String PATH_ELEMENT_SESSION_ID = "{sessionId}";
    private static final String PATH_ELEMENT_REALMS = "/realms/" + PATH_ELEMENT_REALM;
    private static final String PATH_ELEMENT_ADMIN_REALMS = "/admin" + PATH_ELEMENT_REALMS;
    private static final String TOKEN_PATH_TEMPLATE = PATH_ELEMENT_REALMS + "/protocol/openid-connect/token";
    private static final String USERS_PATH_TEMPLATE = PATH_ELEMENT_ADMIN_REALMS + "/users";
    private static final String USER_PATH_TEMPLATE = USERS_PATH_TEMPLATE + "/" + PATH_ELEMENT_USER_ID;
    private static final String USER_SESSIONS_PATH_TEMPLATE = USER_PATH_TEMPLATE + "/sessions";
    private static final String SESSION_PATH_TEMPLATE = PATH_ELEMENT_ADMIN_REALMS + "/sessions/"
            + PATH_ELEMENT_SESSION_ID;

    private final String apiClient;
    private final String apiClientSecret;
    private final String apiUser;
    private final String apiPassword;
    private final String loginClient;
    private final String realm;

    private final String tokenPath;
    private final String usersPath;
    private final String userPath;
    private final String userSessionsPath;
    private final String sessionPath;

    private String currentToken = null;

    /**
     * Construct an AuthenticationClient instance.
     *
     * @param keyStoreSettings
     *            Settings to determine a KeyStore and TrustManagerFactory.
     * @param KeycloakApiSettings
     *            Settings used accessing the Keycloak API.
     * @param loginClient
     *            The keycloak client used for application logins.
     * @param realm
     *            The keycloak realm with clients and users for external logins.
     *
     * @throws KeycloakClientException
     *             In case the construction fails.
     */
    public KeycloakClient(final KeyStoreSettings keyStoreSettings, final KeycloakApiSettings keycloakApiSettings,
            final String loginClient, final String realm) throws KeycloakClientException {

        this.apiClient = keycloakApiSettings.getApiClient();
        this.apiClientSecret = keycloakApiSettings.getApiClientSecret();
        this.apiUser = keycloakApiSettings.getApiUser();
        this.apiPassword = keycloakApiSettings.getApiPassword();
        this.loginClient = loginClient;
        this.realm = realm;

        this.tokenPath = TOKEN_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.usersPath = USERS_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.userPath = USER_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.userSessionsPath = USER_SESSIONS_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);
        this.sessionPath = SESSION_PATH_TEMPLATE.replace(PATH_ELEMENT_REALM, realm);

        try {

            final TrustManagerFactory tmf = keyStoreSettings.getTrustManagerFactory();

            final List<Object> providers = new ArrayList<Object>();
            providers.add(new JacksonJaxbJsonProvider());

            this.webClient = WebClient.create(keycloakApiSettings.getBaseAddress(), providers, true);
            if (this.webClient == null) {
                throw new IllegalStateException("webclient is null");
            }

            final ClientConfiguration config = WebClient.getConfig(this.webClient);
            final HTTPConduit conduit = config.getHttpConduit();

            conduit.setTlsClientParameters(new TLSClientParameters());
            conduit.getTlsClientParameters().setTrustManagers(tmf.getTrustManagers());

            this.jacksonObjectMapper = new ObjectMapper();
        } catch (final Exception e) {
            LOGGER.error(CONSTRUCTION_FAILED, e);
            throw new KeycloakClientException(CONSTRUCTION_FAILED, e);
        }
    }

    private void initializeToken() throws KeycloakClientException {

        final Form form = new Form().param("grant_type", "password").param("username", this.apiUser)
                .param("password", this.apiPassword).param("client_id", this.apiClient)
                .param("client_secret", this.apiClientSecret);

        LOGGER.info("Initializing Keycloak API bearer token for client '{}' and realm '{}'.", this.apiClient,
                this.realm);

        final Response response = this.getWebClientInstance().path(this.tokenPath).form(form);
        final JsonNode jsonNode = this.getJsonResponseBody(response);

        final JsonNode accessToken = jsonNode.get("access_token");

        if (accessToken == null || !accessToken.isTextual()) {
            throw new KeycloakClientException(
                    "Keycloak API access token response does not contain a JSON text field 'access_token'.");
        }

        this.currentToken = accessToken.textValue();
    }

    /**
     * If a bearer token has been retrieved before, this method returns the
     * existing token. If not it retrieves a new token from Keycloak.
     * <p>
     * Since tokens expire, it may occur that an existing token no longer can be
     * used to authorize calls. By calling {@link #refreshToken()} the old token
     * will be invalidated so that a new one can be obtained.
     * <p>
     * Methods needing the token, will call this method themselves. There is no
     * need to invoke it explicitly for authorization to work.
     *
     * @return a bearer token that can be used to authorize API calls.
     * @throws KeycloakClientException
     *             if the bearer token can not be retrieved.
     */
    public String getToken() throws KeycloakClientException {
        synchronized (this) {
            if (this.currentToken == null) {
                this.initializeToken();
            }
            return this.currentToken;
        }
    }

    /**
     * Invalidates an earlier retrieved bearer token. This will cause a new
     * token to be obtained, an can be used when the current token has expired.
     */
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

    /**
     * Performs a user lookup by username.
     * <p>
     * This method assumes an existing unique username is provided, so a exactly
     * one user will be found in the lookup.
     *
     * @param username
     *            an existing Keycloak username for the configured realm.
     * @return the user ID for the user with the given username.
     * @throws KeycloakClientException
     *             if retrieving a single user ID for the given username does
     *             not succeed.
     */
    public String getUserId(final String username) throws KeycloakClientException {

        LOGGER.info("Retrieving Keycloak user ID for user '{}' and realm '{}'.", username, this.realm);

        final WebClient getUserIdWebClient = this.getWebClientInstance().path(this.usersPath)
                .query("username", username);

        Response response = this.withBearerToken(getUserIdWebClient).get();

        JsonNode jsonNode;
        try {
            jsonNode = this.getJsonResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to the user lookup.", e);
            this.refreshToken();
            response = this.withBearerToken(getUserIdWebClient).get();
            jsonNode = this.getJsonResponseBody(response);
        }

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

    /**
     * Looks up the sessions for the user with the given user ID. If a session
     * exists that has the login client in its clients collection, the session
     * ID for this session is returned. If no such session is returned, this
     * method returns {@code null}.
     * <p>
     * The user ID can be looked up by username by calling
     * {@link #getUserId(String)}.
     *
     * @param userId
     *            an existing Keycloak user ID for the configured realm.
     *
     * @return the session ID for a login session for the user with the given ID
     *         with the configured login client and realm, or {@code null} if
     *         such a session is not found.
     * @throws KeycloakClientException
     *             in case of errors while obtaining the session ID from
     *             Keycloak.
     */
    public String getUserSessionId(final String userId) throws KeycloakClientException {
        LOGGER.info("Retrieving Keycloak user session for user ID '{}' with client '{}' for realm '{}'.", userId,
                this.loginClient, this.realm);

        final WebClient getUserSessionIdWebClient = this.getWebClientInstance().path(
                this.userSessionsPath.replace(PATH_ELEMENT_USER_ID, userId));

        Response response = this.withBearerToken(getUserSessionIdWebClient).get();
        JsonNode jsonNode;
        try {
            jsonNode = this.getJsonResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to get the session ID.", e);
            this.refreshToken();
            response = this.withBearerToken(getUserSessionIdWebClient).get();
            jsonNode = this.getJsonResponseBody(response);
        }

        final ArrayNode jsonArray = this.asArrayNode(jsonNode, "from Keycloak API user lookup");

        if (jsonArray.size() == 0) {
            /*
             * No sessions in Keycloak for the given user. This would be a
             * normal situation when the application login is based only on a
             * Mellon session still active, when there is no single logout at
             * the end of a Keycloak session.
             */
            LOGGER.info("No active Keycloak sessions for user ID '{}' for realm '{}'.", userId, this.realm);
            return null;
        }

        final String sessionId = this.determineSessionIdWithLoginClient(jsonArray);

        if (sessionId == null) {
            LOGGER.info("No active Keycloak sessions for user ID '{}' with client '{}' for realm '{}'.", userId,
                    this.loginClient, this.realm);
        }

        return sessionId;
    }

    private String determineSessionIdWithLoginClient(final ArrayNode sessionRepresentationArray) {
        final Iterator<JsonNode> elements = sessionRepresentationArray.elements();
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
            if (this.useSessionIdForClients(clients)) {
                return sessionId.textValue();
            }
        }
        return null;
    }

    private boolean useSessionIdForClients(final JsonNode clientsObject) {
        final Iterator<JsonNode> clientNameNodeIterator = clientsObject.elements();
        while (clientNameNodeIterator.hasNext()) {
            final JsonNode clientName = clientNameNodeIterator.next();
            if (clientName == null || !clientName.isTextual()) {
                LOGGER.warn("value in clients is not a JSON text node with a client name");
                continue;
            }
            if (clientName.textValue().equals(this.loginClient)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method as a shortcut for retrieving the user ID by the
     * username, and then looking up the user session ID for this user with the
     * login client.
     *
     * @see #getUserId(String)
     * @see #getUserSessionId(String)
     * @param username
     *            an existing Keycloak username for the configured realm.
     * @return a session ID for the user with the given username with the login
     *         client, or {@code null}, if such a session can not be found.
     * @throws KeycloakClientException
     *             if anything goes wrong in the process of the session ID
     *             lookup.
     */
    public String getUserSessionIdByUsername(final String username) throws KeycloakClientException {
        return this.getUserSessionId(this.getUserId(username));
    }

    /**
     * Removes the user session with the given session ID. Does nothing if the
     * given session ID is {@code null}.
     *
     * @param sessionId
     *            an existing Keycloak session ID with the configured realm.
     * @throws KeycloakClientException
     *             if removing the user session fails.
     */
    public void removeUserSession(final String sessionId) throws KeycloakClientException {
        if (sessionId == null) {
            return;
        }
        LOGGER.info("Removing Keycloak user session with ID '{}' for realm '{}'.", sessionId, this.realm);

        final WebClient removeUserSessionWebClient = this.getWebClientInstance().path(
                this.sessionPath.replace(PATH_ELEMENT_SESSION_ID, sessionId));

        Response response = this.withBearerToken(removeUserSessionWebClient).delete();
        try {
            this.checkResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to remove the session.", e);
            this.refreshToken();
            response = this.withBearerToken(removeUserSessionWebClient).delete();
            this.checkResponseBody(response);
        }
    }

    /**
     * Convenience method as a shortcut for retrieving the user ID by the
     * username, then looking up the user session ID for this user with the
     * login client and removing the session.
     * <p>
     * If no session to be removed is found, this method does nothing.
     *
     * @see #getUserSessionIdByUsername(String)
     * @see #removeUserSession(String)
     * @param username
     *            an existing Keycloak username for the configured realm.
     * @throws KeycloakClientException
     *             if anything goes wrong in the process of removing the user
     *             session.
     */
    public void removeUserSessionByUsername(final String username) throws KeycloakClientException {
        this.removeUserSession(this.getUserSessionIdByUsername(username));
    }

    /**
     * Creates a new user in the configured realm with the given username and
     * password. The username must not already exist.
     *
     * @param username
     *            a new username with the configured realm.
     * @param password
     *            the password for the new user.
     * @throws KeycloakClientException
     *             if creating the user fails.
     */
    public void addNewUser(final String username, final String password) throws KeycloakClientException {

        LOGGER.info("Adding new user '{}' to Keycloak realm '{}'.", username, this.realm);

        final WebClient createUserWebClient = this.getWebClientInstance().path(this.usersPath);

        final ObjectNode newUserRepresentation = this.jacksonObjectMapper.createObjectNode();
        newUserRepresentation.put("username", username);
        newUserRepresentation.put("enabled", true);

        Response response = this.withBearerToken(createUserWebClient).post(newUserRepresentation);
        try {
            this.checkResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to add new user.", e);
            this.refreshToken();
            response = this.withBearerToken(createUserWebClient).post(newUserRepresentation);
            this.checkResponseBody(response);
        }

        /*
         * At the time this code was written, the user credential (password) had
         * to be updated after creating the user, in order for the user to be
         * able to login (credential.temparary=false did not work when creating
         * a user).
         * 
         * The keycloak mailing list mentions the possibility of this behavior
         * being fixed in a future release:
         * 
         * https://lists.jboss.org/pipermail/keycloak-user/2016-March/005311.html
         * 
         * Quote:
         * 
         * We'll fix this in the future so you can do a single post with new
         * user, including credentials and role mappings. For now you'll have to
         * do the two separate requests though.
         * 
         * https://lists.jboss.org/pipermail/keycloak-user/2014-July/000498.html
         * 
         * Quote:
         * 
         * - Use the endpoint to setup temporary password of user (It will
         * automatically add requiredAction for UPDATE_PASSWORD
         * 
         * - Then use the endpoint for update user and send the empty array of
         * requiredActions in it. This will ensure that UPDATE_PASSWORD required
         * action will be deleted and user won't need to update password again.
         */
        this.updatePasswordByUsername(username, password);
    }

    /**
     * Updates the password of the user with the given user ID.
     *
     * @param userId
     *            an existing Keycloak user ID with the configured realm.
     * @param password
     *            the new password for the user with the given user ID.
     * @throws KeycloakClientException
     *             if updating the user password fails.
     */
    public void updatePassword(final String userId, final String password) throws KeycloakClientException {

        /*
         * Update the password by setting a temporary password and clearing the
         * required actions (do not require the user to update the password in
         * Keycloak before a new password can be used).
         * 
         * See the comments near the bottom of addNewUser(String, String) for
         * more details on the reasons behind this approach.
         */

        LOGGER.info("Updating password for user '{}' for Keycloak realm '{}'.", userId, this.realm);

        final ObjectNode credentialRepresentation = this.jacksonObjectMapper.createObjectNode();
        credentialRepresentation.put("type", "password");
        credentialRepresentation.put("value", password);

        final WebClient resetPasswordWebClient = this.getWebClientInstance().path(
                this.userPath.replace(PATH_ELEMENT_USER_ID, userId) + "/reset-password");

        Response response = this.withBearerToken(resetPasswordWebClient).put(credentialRepresentation);
        try {
            this.checkResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to reset the user password.", e);
            this.refreshToken();
            response = this.withBearerToken(resetPasswordWebClient).put(credentialRepresentation);
            this.checkResponseBody(response);
        }

        final ObjectNode updateUserRequiredActionsRepresentation = this.jacksonObjectMapper.createObjectNode();
        final ArrayNode requiredActionsRepresentation = this.jacksonObjectMapper.createArrayNode();
        updateUserRequiredActionsRepresentation.set("requiredActions", requiredActionsRepresentation);

        final WebClient updateUserWebClient = this.getWebClientInstance().path(
                this.userPath.replace(PATH_ELEMENT_USER_ID, userId));

        response = this.withBearerToken(updateUserWebClient).put(updateUserRequiredActionsRepresentation);
        try {
            this.checkResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to update the required actions.", e);
            this.refreshToken();
            response = this.withBearerToken(updateUserWebClient).put(updateUserRequiredActionsRepresentation);
            this.checkResponseBody(response);
        }
    }

    /**
     * Convenience method as a shortcut for retrieving the user ID by the
     * username, then updating the password credential for this user.
     *
     * @see #getUserId(String)
     * @see #updatePassword(String, String)
     * @param username
     *            an existing Keycloak username for the configured realm.
     * @param password
     *            the new password for the user with the given username.
     * @throws KeycloakClientException
     *             if anything goes wrong in the process of updating the
     *             password.
     */
    public void updatePasswordByUsername(final String username, final String password) throws KeycloakClientException {
        this.updatePassword(this.getUserId(username), password);
    }

    /**
     * Deletes the user with the given user ID.
     *
     * @param userId
     *            an existing Keycloak user ID for the configured realm.
     * @throws KeycloakClientException
     *             if anything goes wrong in the process of removing the user.
     */
    public void removeUser(final String userId) throws KeycloakClientException {

        LOGGER.info("Removing user '{}' from Keycloak realm '{}'.", userId, this.realm);

        final WebClient deleteUserWebClient = this.getWebClientInstance().path(
                this.userPath.replace(PATH_ELEMENT_USER_ID, userId));

        Response response = this.withBearerToken(deleteUserWebClient).delete();
        try {
            this.checkResponseBody(response);
        } catch (final KeycloakBearerException e) {
            LOGGER.debug("It looks like the bearer token expired, retry API call to delete the user.", e);
            this.refreshToken();
            response = this.withBearerToken(deleteUserWebClient).delete();
            this.checkResponseBody(response);
        }
    }

    /**
     * Convenience method as a shortcut for retrieving the user ID by the
     * username, then removing the user.
     *
     * @see #getUserId(String)
     * @see #removeUser(String)
     * @param username
     *            an existing Keycloak username for the configured realm.
     * @throws KeycloakClientException
     *             if anything goes wrong in the process of removing the user.
     */
    public void removeUserByUsername(final String username) throws KeycloakClientException {
        this.removeUser(this.getUserId(username));
    }

    private WebClient withBearerToken(final WebClient webClient) throws KeycloakClientException {
        final String bearerToken = "bearer " + this.getToken();
        return webClient.replaceHeader(HttpHeaders.AUTHORIZATION, bearerToken);
    }

    private JsonNode getJsonResponseBody(final Response response) throws KeycloakClientException {

        if (response == null) {
            throw new KeycloakClientException(RESPONSE_IS_NULL);
        }

        final StatusType statusInfo = response.getStatusInfo();
        LOGGER.info("Received response with status: {} - {} ({})", statusInfo.getStatusCode(),
                statusInfo.getReasonPhrase(), statusInfo.getFamily());

        if (statusInfo.getStatusCode() == Status.UNAUTHORIZED.getStatusCode()) {
            throw new KeycloakBearerException(NOT_AUTHORIZED);
        }

        final String responseBody = response.readEntity(String.class);

        if (StringUtils.isEmpty(responseBody)) {
            throw new KeycloakClientException(RESPONSE_BODY_IS_EMPTY);
        }

        this.checkBearer(responseBody);

        try {
            LOGGER.info("Parsing Keycloak API JSON response: {}", responseBody);
            final JsonNode jsonNode = this.jacksonObjectMapper.reader().readTree(responseBody);
            this.checkForError(jsonNode);
            return jsonNode;
        } catch (final IOException e) {
            LOGGER.error("Error reading JSON data from response body: {}", responseBody, e);
            throw new KeycloakClientException(RESPONSE_BODY_NOT_JSON, e);
        }
    }

    private void checkResponseBody(final Response response) throws KeycloakClientException {

        if (response == null) {
            throw new KeycloakClientException(RESPONSE_IS_NULL);
        }

        final StatusType statusInfo = response.getStatusInfo();
        LOGGER.info("Received response with status: {} - {} ({})", statusInfo.getStatusCode(),
                statusInfo.getReasonPhrase(), statusInfo.getFamily());

        if (statusInfo.getStatusCode() == javax.ws.rs.core.Response.Status.UNAUTHORIZED.getStatusCode()) {
            throw new KeycloakBearerException(NOT_AUTHORIZED);
        }

        final String responseBody = response.readEntity(String.class);

        this.checkBearer(responseBody);

        if (!StringUtils.isEmpty(responseBody)) {
            LOGGER.warn("Response body is not blank: {}", responseBody);
        }
    }

    private void checkBearer(final String responseBody) throws KeycloakBearerException {
        if (BEARER.equals(responseBody)) {
            throw new KeycloakBearerException(AUTHENTICATE_FAILED);
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

    private ArrayNode asArrayNode(final JsonNode jsonNode, final String detail) throws KeycloakClientException {

        if (jsonNode == null || !jsonNode.isArray()) {
            throw new KeycloakClientException("Expected array result " + detail + ", got: "
                    + (jsonNode == null ? "null" : jsonNode.getNodeType().name()));
        }

        return (ArrayNode) jsonNode;
    }
}
