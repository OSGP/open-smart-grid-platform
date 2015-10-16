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

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * This class offers a web client for the web-api-user-management web service.
 */
public class UserManagementClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementClient.class);

    private static final String CONSTRUCTION_FAILED = "UserManagementClient construction failed";

    private String usersPath = "/users";
    private String userPath = "/users/";
    private String addNewUserPath = "/users/add";
    private String changeUserPasswordPath = "/changepassword";
    private String removeUserPath = "/remove";
    private String changeUserDataPath = "/change";

    private String rolesPath = "/roles";
    private String applicationsPath = "/applications";

    private String organisationPath = "/organisations/";

    /**
     * Construct a UserManagementClient instance.
     *
     * @param keystoreLocation
     *            The location of the key store.
     * @param keystorePassword
     *            The password for the key store.
     * @param keystoreType
     *            The type of the key store.
     * @param baseAddress
     *            The base address or URL for the UserManagementClient.
     *
     * @throws UserManagementClientException
     *             In case the construction fails, a
     *             UserManagmentClientException will be thrown.
     */
    public UserManagementClient(final String keystoreLocation, final String keystorePassword,
            final String keystoreType, final String baseAddress) throws UserManagementClientException {

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

            this.webClient = WebClient.create(baseAddress, providers);
            if (this.webClient == null) {
                throw new UserManagementClientException("webclient is null");
            }

            // Set up the HTTP Conduit to use the TrustManagers.
            final ClientConfiguration config = WebClient.getConfig(this.webClient);
            final HTTPConduit conduit = config.getHttpConduit();

            conduit.setTlsClientParameters(new TLSClientParameters());
            conduit.getTlsClientParameters().setTrustManagers(tmf.getTrustManagers());
        } catch (final Exception e) {
            LOGGER.error(CONSTRUCTION_FAILED, e);
            throw new UserManagementClientException(CONSTRUCTION_FAILED, e);
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
            throw new UserManagementClientException(CONSTRUCTION_FAILED, exception);
        }
    }

    /**
     * Get all users for all organisations.
     *
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a list of all users for all
     *         organisations.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getAllUsers(final String organisationIdentification, final String token)
            throws UserManagementClientException {

        final Response response = this.getWebClientInstance().path(this.usersPath)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String users;
        try {
            users = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("get all users response exception", e);
        }

        return users;
    }

    /**
     * Get the users for an organisation.
     *
     * @param usersForOrganisationIdentification
     *            The organisation identification for which the users are
     *            requested.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a list of all users for the
     *         organisation.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getUsers(final String usersForOrganisationIdentification, final String organisationIdentification,
            final String token) throws UserManagementClientException {

        final Response response = this.getWebClientInstance()
                .path(this.organisationPath + usersForOrganisationIdentification + this.usersPath)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String users;
        try {
            users = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("get users response exception", e);
        }

        return users;
    }

    /**
     * Get a user.
     *
     * @param username
     *            The user name of the user to get.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a user.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getUser(final String username, final String organisationIdentification, final String token)
            throws UserManagementClientException {

        final Response response = this.getWebClientInstance().path(this.userPath + username)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String user;
        try {
            user = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("get user response exception", e);
        }

        return user;
    }

    /**
     * Change a user's password.
     *
     * @param organisationIdentificationForUser
     *            The organisation identification of the user.
     * @param username
     *            The user name of the user to get.
     * @param newPassword
     *            The new password for the user.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a feedback message.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String changePassword(final String organisationIdentificationForUser, final String username,
            final String newPassword, final String organisationIdentification, final String token)
                    throws UserManagementClientException {

        final ChangeUserPasswordRequest changeUserPasswordRequest = new ChangeUserPasswordRequest(
                organisationIdentificationForUser, username, newPassword);

        final Response response = this.getWebClientInstance()
                .path(this.userPath + username + this.changeUserPasswordPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(changeUserPasswordRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("change password response exception", e);
        }

        return apiResponse;
    }

    /**
     * Add a new user.
     *
     * @param organisationIdentificationForUser
     *            The organisation identification for the new user.
     * @param username
     *            The user name of the new user.
     * @param password
     *            The password of the new user.
     * @param role
     *            The role of the new user.
     * @param applications
     *            The application permissions for the new user.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing the new user.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String addNewUser(final String organisationIdentificationForUser, final String username,
            final String password, final String role, final String applications,
            final String organisationIdentification, final String token) throws UserManagementClientException {

        final AddNewUserRequest addNewUserRequest = new AddNewUserRequest(organisationIdentificationForUser, username,
                password, role, applications);

        final Response response = this.getWebClientInstance()
                .path(this.organisationPath + organisationIdentificationForUser + this.addNewUserPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(addNewUserRequest);

        String user;
        try {
            user = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("add new user response exception", e);
        }

        return user;
    }

    /**
     * Remove a user.
     *
     * @param organisationIdentificationForUser
     *            The organisation identification for the user to remove.
     * @param username
     *            The user name of the user to remove.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a succesMessage or errorMessage.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String removeUser(final String organisationIdentificationForUser, final String username,
            final String organisationIdentification, final String token) throws UserManagementClientException {

        final RemoveUserRequest removeUserRequest = new RemoveUserRequest(organisationIdentificationForUser, username);

        final Response response = this.getWebClientInstance().path(this.userPath + username + this.removeUserPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(removeUserRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("remove user response exception", e);
        }

        return apiResponse;
    }

    /**
     * Change a users data.
     *
     * @param organisationIdentificationForUser
     *            The organisation identification for the user.
     * @param username
     *            The user name of the user to get.
     * @param newUsername
     *            The new user name for the user.
     * @param newPassword
     *            The new password for the user.
     * @param newRole
     *            The new role for the user.
     * @param newApplications
     *            The new list of applications for the user.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing a succesMessage or errorMessage.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String changeUserData(final String organisationIdentificationForUser, final String username,
            final String newUsername, final String newFirstName, final String newMiddleName, final String newLastName,
            final String newEmailAddress, final String newPassword, final String newRole, final String newApplications,
            final String organisationIdentification, final String token) throws UserManagementClientException {

        final ChangeUserRequest changeUserRequest = new ChangeUserRequest(organisationIdentificationForUser, username,
                newUsername, newFirstName, newMiddleName, newLastName, newEmailAddress, newPassword, newRole,
                newApplications);

        final Response response = this.getWebClientInstance().path(this.userPath + username + this.changeUserDataPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(changeUserRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("change user data response exception", e);
        }

        return apiResponse;
    }

    /**
     * Get the list of all roles.
     *
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing the list of roles.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getRoles(final String organisationIdentification, final String token)
            throws UserManagementClientException {

        final Response response = this.getWebClientInstance().path(this.usersPath + this.rolesPath)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String roles;
        try {
            roles = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("get roles response exception", e);
        }

        return roles;
    }

    /**
     * Get the list of all applications.
     *
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     *
     * @return A JSON string containing the list of applications.
     *
     * @throws UserManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getApplications(final String organisationIdentification, final String token)
            throws UserManagementClientException {

        final Response response = this.getWebClientInstance().path(this.usersPath + this.applicationsPath)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String applications;
        try {
            applications = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new UserManagementClientException("get applications response exception", e);
        }

        return applications;
    }
}
