/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.deviceinstallation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.junit.Assert;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceInstallationEndpoint;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

@Configurable
@DomainSteps
public class FindRecentDevicesSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindRecentDevicesSteps.class);

    private static final String DEVICE_ID = "DEVICE-01";
    private static final String OWNER_ID = "ORGANISATION-01";
    private static final String OWNER_UNKNOWN = "UNKNOWN";
    private static final String OWNER_EMPTY = "";

    private String organisation;
    private FindRecentDevicesRequest request;
    private FindRecentDevicesResponse response;
    private Throwable throwable;

    private DeviceInstallationEndpoint deviceInstallationEndpoint;

    private Ssld device;

    private Organisation owner;

    // Repository Mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository authorizationRepositoryMock;

    // Application Service
    @Autowired
    @Qualifier(value = "wsCoreDeviceInstallationService")
    private DeviceInstallationService deviceInstallationService;

    @Autowired
    @Qualifier("coreDeviceInstallationMapper")
    private DeviceInstallationMapper deviceInstallationMapper;

    public void Setup() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.ssldRepositoryMock,
                this.authorizationRepositoryMock, this.organisationRepositoryMock });

        this.deviceInstallationEndpoint = new DeviceInstallationEndpoint(this.deviceInstallationService,
                this.deviceInstallationMapper);

        // Create the owner
        this.owner = new OrganisationBuilder().withOrganisationIdentification(OWNER_ID).build();
        when(this.organisationRepositoryMock.findByOrganisationIdentification(OWNER_ID)).thenReturn(this.owner);

        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    // === GIVEN ===
    @DomainStep("a valid find recent devices request")
    public void givenAValidFindRecentDevicesRequest() {
        LOGGER.info("GIVEN: \"a valid find recent devices request\".");

        this.Setup();

        this.request = new FindRecentDevicesRequest();
        this.organisation = OWNER_ID;
    }

    @DomainStep("a valid device")
    public void andAValidDevice() {
        LOGGER.info("GIVEN: \"a valid device\".");

        this.device = new Ssld(DEVICE_ID, "alias", "city", "postal-code", "street", "street-number", "municipality",
                12.34F, 14.23F);
        when(this.deviceRepositoryMock.findRecentDevices(eq(this.owner), any(Date.class))).thenReturn(
                Arrays.asList((Device) this.device));
        when(this.ssldRepositoryMock.findOne(any(Long.class))).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
    }

    @DomainStep("a device (.*) with (.*)")
    public void andADeviceWithHasSchedule(final String deviceIdentification, final String hasSchedule) {
        LOGGER.info("GIVEN: \"a device {} with {}\".", deviceIdentification, hasSchedule);

        // Create the device
        this.device = (Ssld) new DeviceBuilder().withDeviceIdentification(deviceIdentification).build();

        if (hasSchedule != "EMPTY") {
            this.device.setHasSchedule(hasSchedule == "true");
        }

        // Make sure that this device is returned.
        when(this.deviceRepositoryMock.findRecentDevices(eq(this.owner), any(Date.class))).thenReturn(
                Arrays.asList((Device) this.device));
        when(this.ssldRepositoryMock.findOne(any(Long.class))).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(deviceIdentification)).thenReturn(this.device);
    }

    @DomainStep("a find recent devices request with unknown owner organisation")
    public void givenAFindRecentDevicesRequestWithUnknownOwnerOrganisation() {
        LOGGER.info("GIVEN: \"a find recent devices request with unknown owner organisation\".");

        this.Setup();

        this.device = new Ssld(DEVICE_ID, "alias", "city", "postal-code", "street", "street-number", "municipality",
                12.34F, 14.23F);

        when(this.organisationRepositoryMock.findByOrganisationIdentification(OWNER_UNKNOWN)).thenReturn(null);

        this.request = new FindRecentDevicesRequest();
        this.organisation = OWNER_UNKNOWN;
    }

    @DomainStep("a find recent devices request with empty owner organisation")
    public void givenAFindRecentDeviceRequestWithEmptyOwner() {
        LOGGER.info("GIVEN: \"a find recent devices request with empty owner organisation\".");

        this.Setup();

        this.device = new Ssld(DEVICE_ID, "alias", "city", "postal-code", "street", "street-number", "municipality",
                12.34F, 14.23F);

        this.request = new FindRecentDevicesRequest();
        this.organisation = OWNER_EMPTY;
    }

    // === WHEN ===

    @DomainStep("the find recent devices request is received")
    public void whenTheFindRecentDevicesRequestIsReceived() {
        LOGGER.info("WHEN: \"the find recent devices request is received\".");

        MockitoAnnotations.initMocks(this);

        try {
            this.response = this.deviceInstallationEndpoint.findRecentDevices(this.organisation, this.request);
        } catch (final Throwable t) {
            if (t instanceof ValidationException) {
                LOGGER.info("Exception: {}", ((ValidationException) t).toString());
            } else {
                LOGGER.error("Exception: {}", t.getClass().getSimpleName());
            }
            this.throwable = t;
        }
    }

    // === THEN ===
    @DomainStep("the find recent devices response should contain (.*) device")
    public void thenTheFindRecentDevicesResponseShouldContainNumberDevices(final String number) {
        LOGGER.info("THEN: \"the find recent devices response should contain {} device(s)\".", number);

        if (this.response == null) {
            LOGGER.error("FindRecentDevicesResponse is null");
        } else if (this.response.getDevices() == null) {
            LOGGER.error("device list is null");
        } else {
            LOGGER.info("device list size: {}", this.response.getDevices().size());
        }

        Assert.assertTrue(this.response.getDevices().size() == Integer.parseInt(number));
    }

    @DomainStep("the find recent devices request should return a find recent devices response")
    public boolean thenTheFindRecentDevicesRequestShouldReturnAFindRecentDevicesResponse() {
        LOGGER.info("THEN: \"the find recent devices request should return a find recent devices response\".");
        return this.response != null && this.throwable == null;
    }

    @DomainStep("the find recent devices response should contain only the devices for the owner organisation")
    public boolean thenTheFindRecentDevicesResponseShouldContainOnlyTheDevicesForTheOwnerOrganisation() {
        LOGGER.info("THEN: \"the find recent devices response should contain only the devices for the owner organisation\".");

        try {
            verify(this.organisationRepositoryMock, times(1)).findByOrganisationIdentification(OWNER_ID);
            verifyNoMoreInteractions(this.organisationRepositoryMock);
            verify(this.deviceRepositoryMock, times(1)).findRecentDevices(eq(this.owner), any(Date.class));
            verifyNoMoreInteractions(this.deviceRepositoryMock);
        } catch (final Throwable t) {
            return false;
        }
        return this.response.getDevices() != null && this.response.getDevices().size() == 1;
    }

    @DomainStep("the find recent devices request should return an unknown organisation exception")
    public boolean thenTheFindRecentDevicesRequestShouldReturnAnUnkownOrganisationException() {
        LOGGER.info("THEN: \"the find recent devices request should return an unknown organisation exception\".");

        try {
            verify(this.organisationRepositoryMock, times(1)).findByOrganisationIdentification(OWNER_UNKNOWN);
            verifyNoMoreInteractions(this.organisationRepositoryMock);

            verify(this.deviceRepositoryMock, times(0)).findRecentDevices(any(Organisation.class), any(Date.class));
            verifyNoMoreInteractions(this.deviceRepositoryMock);
        } catch (final Throwable t) {
            return false;
        }
        return this.throwable != null && this.throwable.getCause() instanceof UnknownEntityException;
    }

    @DomainStep("the find recent devices request should return an empty owner exception")
    public boolean thenTheFindRecentDevicesRequestShouldReturnAnEmptyOwnerException() {
        LOGGER.info("THEN: \"the find recent devices request should return an empty owner exception\".");

        try {
            verify(this.organisationRepositoryMock, times(0)).findByOrganisationIdentification(OWNER_EMPTY);
            verifyNoMoreInteractions(this.organisationRepositoryMock);

            verify(this.deviceRepositoryMock, times(0)).findRecentDevices(any(Organisation.class), any(Date.class));
            verifyNoMoreInteractions(this.deviceRepositoryMock);
        } catch (final Throwable t) {
            LOGGER.error("Exception: {}", t.toString());
            return false;
        }

        return this.throwable != null && this.throwable.getCause() instanceof ValidationException;
    }

    @DomainStep("the device in the response matches device (.*) with hasschedule (.*)")
    public void andTheDeviceInTheResponseMatchesDeviceWithHasSchedule(final String deviceIdentification,
            final String hasSchedule) {
        LOGGER.info("THEN: \"the device in the response matches device {} with hasschedule {}\".",
                deviceIdentification, hasSchedule);

        Assert.assertTrue(this.response.getDevices().get(0).getDeviceIdentification().equals(deviceIdentification));
        Assert.assertTrue(this.response.getDevices().get(0).isHasSchedule() == (hasSchedule == "true"));
    }
}
