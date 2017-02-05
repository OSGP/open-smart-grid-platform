/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class AuthorizePlatformFunctions {

    @Autowired
    private AdminDeviceManagementClient adminDeviceManagementClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizePlatformFunctions.class);

    private PlatformFunction platformFunction;
    private Throwable throwable;

    @When("receiving a platform function request")
    public void receivingADeviceFunctionRequest(final Map<String, String> requestParameters)
            throws OperationNotSupportedException, WebServiceSecurityException, GeneralSecurityException, IOException {
        this.platformFunction = getEnum(requestParameters, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunction.class);

        try {
            switch (this.platformFunction) {
            case CREATE_ORGANISATION:
                this.createOrganisation(requestParameters);
                break;
            case GET_ORGANISATIONS:
                this.getOrganisations(requestParameters);
                break;
            case GET_MESSAGES:
                this.getMessages(requestParameters);
                break;
            case GET_DEVICE_NO_OWNER:
                this.getDevicesWithoutOwner(requestParameters);
                break;
            case SET_OWNER:
                this.setOwner(requestParameters);
                break;
            case UPDATE_KEY:
                this.updateKey(requestParameters);
                break;
            case REVOKE_KEY:
                this.revokeKey(requestParameters);
                break;
            default:
                throw new OperationNotSupportedException(
                        "PlatformFunction " + this.platformFunction + " does not exist.");
            }
        } catch (final Throwable t) {
            LOGGER.info("Exception: {}", t.getClass().getSimpleName());
            this.throwable = t;
        }
    }

    @Then("the platform function response is \"([^\"]*)\"")
    public void theDeviceFunctionResponseIsSuccessful(final Boolean allowed) {
        final Object response = ScenarioContext.Current().get(Keys.RESPONSE);

        if (allowed) {
            Assert.assertTrue(!(response instanceof SoapFaultClientException));
        } else {
            Assert.assertTrue(this.throwable != null);
        }
    }

    private void createOrganisation(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final CreateOrganisationRequest request = new CreateOrganisationRequest();

        final Organisation organisation = new Organisation();
        organisation.setName(Defaults.DEFAULT_ORGANIZATION_NAME);
        organisation.setOrganisationIdentification(Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
        organisation.setFunctionGroup(
                getEnum(requestParameters, Keys.KEY_PLATFORM_FUNCTION_GROUP, PlatformFunctionGroup.class));
        organisation.setPrefix(Defaults.DEFAULT_ORGANIZATION_PREFIX);
        organisation.setEnabled(Defaults.DEFAULT_ORGANIZATION_ENABLED);
        request.setOrganisation(organisation);

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.createOrganization(request));
    }

    private void getOrganisations(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.findAllOrganizations(new FindAllOrganisationsRequest()));
    }

    private void getMessages(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();

        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.findMessageLogs(request));
    }

    private void getDevicesWithoutOwner(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.adminDeviceManagementClient.findDevicesWithoutOwner(new FindDevicesWhichHaveNoOwnerRequest()));
    }

    private void setOwner(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final SetOwnerRequest request = new SetOwnerRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setOrganisationIdentification(getString(requestParameters, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.setOwner(request));
    }

    private void updateKey(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final UpdateKeyRequest request = new UpdateKeyRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setProtocolInfoId(Defaults.DEFAULT_PROTOCOL_INFO_ID);
        request.setPublicKey(Defaults.DEFAULT_PUBLIC_KEY);

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.getUpdateKeyResponse(request));
    }

    private void revokeKey(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final RevokeKeyRequest request = new RevokeKeyRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        ScenarioContext.Current().put(Keys.RESPONSE, this.adminDeviceManagementClient.getRevokeKeyResponse(request));
    }
}