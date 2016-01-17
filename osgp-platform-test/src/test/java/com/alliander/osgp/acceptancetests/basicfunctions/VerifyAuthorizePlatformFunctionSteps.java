/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.basicfunctions;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.alliander.osgp.adapter.protocol.oslp.infra.networking.DeviceService;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
//import com.alliander.gosp.platform.application.services.SecurityService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;

@Configurable
@DomainSteps()
public class VerifyAuthorizePlatformFunctionSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyAuthorizePlatformFunctionSteps.class);

    private static final String ORGANISATION = "ORGANISATION-01";
    private static final String DEVICE_ID = "DEVICE-01";

    // WS Adapter fields
    private com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint adminDeviceManagementEndpoint;

    private com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint coreDeviceManagementEndpoint;

    @Autowired
    @Qualifier(value = "wsAdminDeviceManagementService")
    private com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService adminDeviceManagementService;

    @Autowired
    @Qualifier(value = "wsCoreDeviceInstallationService")
    private com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService coreDeviceInstallationService;
    @Autowired
    @Qualifier(value = "wsCoreDeviceManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService coreDeviceManagementService;
    @Autowired
    @Qualifier("coreDeviceManagementMapper")
    private DeviceManagementMapper deviceManagementMapper;

    // Domain Adapter fields
    private Organisation organisation;

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository authorizationRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceLogItemRepository logItemRepositoryMock;

    // Protocol adapter fields
    @Mock
    private DeviceService deviceService;

    // Test fields
    private Throwable throwable;
    private Object response;

    private void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.authorizationRepositoryMock,
                this.organisationRepositoryMock, this.logItemRepositoryMock });

        this.adminDeviceManagementEndpoint = new com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint(
                this.adminDeviceManagementService,
                new com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper());
        this.coreDeviceManagementEndpoint = new com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint(
                this.coreDeviceManagementService, this.deviceManagementMapper);

        this.throwable = null;
        this.response = null;
    }

    @DomainStep("an organisation which is member of platform function group (.*)")
    public void givenAnAuthenticatedOrganisation(final String group) {
        this.setUp();

        this.organisation = new OrganisationBuilder()
                .withOrganisationIdentification(ORGANISATION)
                .withFunctionGroup(
                        com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup.valueOf(group.toUpperCase()))
                .build();
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION)).thenReturn(
                this.organisation);
        when(this.logItemRepositoryMock.findAll(any(Pageable.class))).thenReturn(
                new PageImpl<>(new ArrayList<DeviceLogItem>()));
    }

    @DomainStep("platform function (.*) is called")
    public void whenPlatformFunctionIsCalled(final String function) {
        try {
            switch (function) {
            case "CREATE_ORGANISATION":
                this.createOrganisation();
                break;
            case "GET_ORGANISATIONS":
                this.getOrganisations();
                break;
            case "GET_MESSAGES":
                this.getMessages();
                break;
            case "GET_DEVICE_NO_OWNER":
                this.getDeviceWithoNoOwner();
                break;
            case "SET_OWNER":
                this.setOwner();
                break;
            case "UPDATE_KEY":
                this.updateKey();
                break;
            case "REVOKE_KEY":
                this.revokeKey();
                break;
            default:
                throw new OperationNotSupportedException("Function " + function + " does not exist.");
            }

        } catch (final Throwable t) {
            this.throwable = t;
        }
    }

    @DomainStep("the platform function is (.*)")
    public boolean thenThePlatformFunctionIsAllowed(final boolean allowed) {
        LOGGER.info("Allowed: {}", allowed);
        LOGGER.info("Exception: {}", this.throwable != null ? this.throwable.getClass().getSimpleName() : null);
        LOGGER.info("Response {}", this.response != null);

        if (allowed) {
            return this.throwable == null && this.response != null;
        } else {
            Assert.assertTrue("Throwable should not be null", this.throwable != null);
            Assert.assertTrue("Response should be null", this.response == null);
            return this.throwable.getCause() instanceof NotAuthorizedException;
        }
    }

    private void createOrganisation() throws Throwable {
        final CreateOrganisationRequest request = new CreateOrganisationRequest();
        final com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation newOrganisation = new com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation();
        newOrganisation
                .setFunctionGroup(com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup.ADMIN);
        newOrganisation.setName("dummy");
        newOrganisation.setPrefix("org");
        newOrganisation.setOrganisationIdentification("dummy");
        newOrganisation.getDomains().add(PlatformDomain.COMMON);
        request.setOrganisation(newOrganisation);
        this.response = this.adminDeviceManagementEndpoint.createOrganisation(
                this.organisation.getOrganisationIdentification(), request);
    }

    private void getOrganisations() throws Throwable {
        final FindAllOrganisationsRequest request = new FindAllOrganisationsRequest();
        this.response = this.coreDeviceManagementEndpoint.findAllOrganisations(
                this.organisation.getOrganisationIdentification(), request);
    }

    private void getMessages() throws Throwable {
        final FindMessageLogsRequest request = new FindMessageLogsRequest();
        this.response = this.adminDeviceManagementEndpoint.findMessageLogs(
                this.organisation.getOrganisationIdentification(), request);
    }

    private void getDeviceWithoNoOwner() throws Throwable {
        final FindDevicesWhichHaveNoOwnerRequest request = new FindDevicesWhichHaveNoOwnerRequest();
        this.response = this.adminDeviceManagementEndpoint.findDevicesWhichHaveNoOwner(
                this.organisation.getOrganisationIdentification(), request);
    }

    private void setOwner() throws Throwable {
        final Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(device);
        final SetOwnerRequest request = new SetOwnerRequest();
        request.setDeviceIdentification(device.getDeviceIdentification());
        request.setOrganisationIdentification(this.organisation.getOrganisationIdentification());
        this.response = this.adminDeviceManagementEndpoint.setOwner(this.organisation.getOrganisationIdentification(),
                request);
    }

    private void updateKey() throws Throwable {
        final Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(device);
        final UpdateKeyRequest request = new UpdateKeyRequest();
        request.setDeviceIdentification(device.getDeviceIdentification());
        request.setPublicKey("KEY1");
        this.response = this.adminDeviceManagementEndpoint.updateKey(this.organisation.getOrganisationIdentification(),
                request);
    }

    private void revokeKey() throws Throwable {
        final Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(device);
        final RevokeKeyRequest request = new RevokeKeyRequest();
        request.setDeviceIdentification(device.getDeviceIdentification());
        this.response = this.adminDeviceManagementEndpoint.revokeKey(this.organisation.getOrganisationIdentification(),
                request);
    }
}
