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
 * 
 * @author CGI
 * 
 */
public class OrganisationManagementClient extends AbstractClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganisationManagementClient.class);

    private static final String CONSTRUCTION_FAILED = "OrganisationManagementClient construction failed";

    private String organisationsPath = "/organisations";
    private String organisationPath = "/organisations/";
    private String addNewOrganisationPath = "/organisations/add";
    private String removeOrganisationPath = "/organisations/remove";
    private String activateOrganisationPath = "/organisations/activate";
    private String changeOrganisationDataPath = "/organisations/change";

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
     * @throws OrganisationManagementClientException
     *             In case the construction fails, a
     *             OrganisationManagementClientException will be thrown.
     */
    public OrganisationManagementClient(final String keystoreLocation, final String keystorePassword,
            final String keystoreType, final String baseAddress) throws OrganisationManagementClientException {

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
            throw new OrganisationManagementClientException(CONSTRUCTION_FAILED, e);
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
            throw new OrganisationManagementClientException(CONSTRUCTION_FAILED, exception);
        }
    }

    /**
     * Get all organisations.
     * 
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing a list of organisations.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getOrganisations(final String organisationIdentification, final String token)
            throws OrganisationManagementClientException {

        final Response response = this.getWebClientInstance().path(this.organisationsPath)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String organisations;
        try {
            organisations = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("get organisations response exception", e);
        }

        return organisations;
    }

    /**
     * Get an organisation.
     * 
     * @param organisationIdentificationToGet
     *            The organisation identification of the organisation to get.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing an organisation.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String getOrganisation(final String organisationIdentificationToGet,
            final String organisationIdentification, final String token) throws OrganisationManagementClientException {

        final Response response = this.getWebClientInstance()
                .path(this.organisationPath + organisationIdentificationToGet)
                .headers(this.createHeaders(organisationIdentification, token)).get();

        String organisation;
        try {
            organisation = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("get organisation response exception", e);
        }

        return organisation;
    }

    /**
     * Add a new organisation.
     * 
     * @param newOrganisationIdentification
     *            The organisation identification of the organisation to add.
     * @param name
     *            The organisation name of the organisation to add.
     * @param prefix
     *            The prefix for the organisation [NOT USED].
     * @param functionGroup
     *            The platform function group of the organisation to add.
     * @param enabled
     *            The enabled state of the organisation to add.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing a succesMessage or errorMessage.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String addNewOrganisation(final String newOrganisationIdentification, final String name,
            final String prefix, final String functionGroup, final boolean enabled,
            final String organisationIdentification, final String token) throws OrganisationManagementClientException {

        final AddNewOrganisationRequest addNewOrganisationRequest = new AddNewOrganisationRequest(
                newOrganisationIdentification, name, prefix, functionGroup, enabled);

        final Response response = this.getWebClientInstance().path(this.addNewOrganisationPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(addNewOrganisationRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("add new organisation response exception", e);
        }

        return apiResponse;
    }

    /**
     * Remove a.k.a. deactivate an organisation.
     * 
     * @param organisationIdentificationToRemove
     *            The organisation identification of the organisation to remove.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing a succesMessage or errorMessage.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String removeOrganisation(final String organisationIdentificationToRemove,
            final String organisationIdentification, final String token) throws OrganisationManagementClientException {

        final RemoveOrganisationRequest removeOrganisationRequest = new RemoveOrganisationRequest(
                organisationIdentificationToRemove);

        final Response response = this.getWebClientInstance().path(this.removeOrganisationPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(removeOrganisationRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("remove organisation response exception", e);
        }

        return apiResponse;
    }

    /**
     * Activate an organisation.
     * 
     * @param organisationIdentificationToActivate
     *            The organisation identification of the organisation to
     *            activate.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing a succesMessage or errorMessage.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String activateOrganisation(final String organisationIdentificationToActivate,
            final String organisationIdentification, final String token) throws OrganisationManagementClientException {

        final ActivateOrganisationRequest activateOrganisationRequest = new ActivateOrganisationRequest(
                organisationIdentificationToActivate);

        final Response response = this.getWebClientInstance().path(this.activateOrganisationPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(activateOrganisationRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("activate organisation response exception", e);
        }

        return apiResponse;
    }

    /**
     * Change an organisations data.
     * 
     * @param organisationIdentificationToChange
     *            The organisation identification of the organisation to change.
     * @param newOrganisationIdentification
     *            The new organisation identification for the organisation.
     * @param newOrganisationName
     *            The new organisation name for the organisation.
     * @param functionGroup
     *            The new platform function group for the organisation.
     * @param organisationIdentification
     *            The organisation identification of the organisation issuing
     *            the request.
     * @param token
     *            The authentication token.
     * 
     * @return A JSON string containing a succesMessage or errorMessage.
     * 
     * @throws OrganisationManagementClientException
     *             In case the response is null, the HTTP status code is not
     *             equal to 200 OK or if the response body is empty.
     */
    public String changeOrganisationData(final String organisationIdentificationToChange,
            final String newOrganisationIdentification, final String newOrganisationName, final String functionGroup,
            final String organisationIdentification, final String token) throws OrganisationManagementClientException {

        final ChangeOrganisationRequest changeOrganisationRequest = new ChangeOrganisationRequest(
                organisationIdentificationToChange, newOrganisationIdentification, newOrganisationName, functionGroup);

        final Response response = this.getWebClientInstance().path(this.changeOrganisationDataPath)
                .headers(this.createHeaders(organisationIdentification, token)).post(changeOrganisationRequest);

        String apiResponse;
        try {
            apiResponse = this.checkResponse(response);
        } catch (final ResponseException e) {
            throw new OrganisationManagementClientException("change organisation data response exception", e);
        }

        return apiResponse;
    }
}
