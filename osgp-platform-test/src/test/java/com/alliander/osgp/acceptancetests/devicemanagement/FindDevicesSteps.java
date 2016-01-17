/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

@Configurable
@DomainSteps
public class FindDevicesSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindDevicesSteps.class);
    private static final String ORGANISATION_ID = "LianderNetManagement";
    private static final String ORGANISATION_PREFIX = "LIA";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGESIZE = 25;
    private DeviceManagementEndpoint deviceManagementEndpoint;
    private FindDevicesRequest request;
    private FindDevicesResponse response;
    private Device device;
    private Organisation organisation;
    private Page<Device> devices;
    private PageRequest pageRequest;

    // Repository Mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;

    // Application Services
    @Autowired
    private DeviceManagementService deviceManagementService;
    @Autowired
    @Qualifier("coreDeviceManagementMapper")
    private DeviceManagementMapper deviceManagementMapper;

    private Organisation ownerOrganisation;

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.ssldRepositoryMock,
                this.organisationRepositoryMock, this.deviceAuthorizationRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService,
                this.deviceManagementMapper);

        this.request = null;
        this.response = null;

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);
    }

    // === GIVEN ===

    @DomainStep("a valid find devices request with pageSize (.*) and page (.*)")
    public void givenAValidFindDevicesRequest(final String pageSize, final String page) {
        this.setUp();

        this.request = new FindDevicesRequest();
        this.request.setPageSize(Integer.parseInt(pageSize));
        this.request.setPage(Integer.parseInt(page));
    }

    @DomainStep("a device (.*) with ownerid (.*) and ownername (.*)")
    public void andADevice(final String deviceIdentification, final String ownerId, final String ownerName)
            throws UnknownHostException {
        this.ownerOrganisation = new Organisation(ownerId, ownerName, ORGANISATION_PREFIX, PlatformFunctionGroup.ADMIN);

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(InetAddress.getLocalHost()).build();

        this.device.addAuthorization(this.ownerOrganisation, DeviceFunctionGroup.OWNER);

        this.pageRequest = new PageRequest(DEFAULT_PAGE, DEFAULT_PAGESIZE, Sort.Direction.DESC, "creationTime");

        final List<Device> devicesList = new ArrayList<Device>();
        devicesList.add(this.device);
        this.devices = new PageImpl<Device>(devicesList, this.pageRequest, devicesList.size());

        when(this.deviceRepositoryMock.findAll(this.pageRequest)).thenReturn(this.devices);
        when(this.ssldRepositoryMock.findByDeviceIdentification(any(String.class))).thenReturn(null);
        when(this.ssldRepositoryMock.findOne(any(Long.class))).thenReturn(null);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.OWNER).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
        .thenReturn(authorizations);
    }

    // === WHEN ===

    @DomainStep("the find devices request is received")
    public void whenTheFindDevicesRequestIsReceived() throws UnknownEntityException, ValidationException,
    NotAuthorizedException {
        LOGGER.info("WHEN: \"the the find devices request is received\".");

        try {
            this.response = this.deviceManagementEndpoint.findDevices(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
        }
    }

    // === THEN ===

    @DomainStep("the find devices request should return a find devices response")
    public boolean thenTheFindDevicesRequestShouldReturnAFindDevicesResponse() {
        LOGGER.info("THEN: \"the the find devices request should return a find devices response\".");

        // Expect that
        verify(this.deviceRepositoryMock, times(1)).findAll(any(PageRequest.class));

        return this.response != null;
    }

    @DomainStep("the find devices response should contain (.*) device")
    public void andTheFindDevicesResponseShouldContainDevices(final String numberOfDevices) {
        LOGGER.info("THEN: \"the the find devices response should contain devices\".");

        Assert.assertEquals(Integer.parseInt(numberOfDevices), this.response.getDevices().size());
    }

    @DomainStep("the device in the response matches device (.*)")
    public void andTheDeviceInTheResponseMatchesDevice(final String deviceIdentification) {
        LOGGER.info("THEN: \"the the find devices in the response matches device\".");

        Assert.assertEquals(this.ownerOrganisation.getName(), this.response.getDevices().get(0).getOwner());
    }
}
