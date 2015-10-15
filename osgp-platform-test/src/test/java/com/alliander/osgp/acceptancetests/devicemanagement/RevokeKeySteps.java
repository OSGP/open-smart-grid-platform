/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import com.alliander.osgp.core.application.services.DeviceResponseMessageService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

@Configurable
@DomainSteps
public class RevokeKeySteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeKeySteps.class);
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String ORGANISATION_PREFIX = "ORG";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // Domain fields
    private DeviceManagementEndpoint deviceManagementEndpoint;
    @Autowired
    private DeviceManagementService deviceManagementService;

    private RevokeKeyRequest request;
    private RevokeKeyResponse response;

    // OSGP fields
    @Autowired
    private DeviceResponseMessageService deviceResponseMessageService;
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;
    private Device device;
    private Organisation organisation;

    // Protocol fields
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;
    private OslpDevice oslpDevice;

    // Test fields
    private Throwable throwable;

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.oslpDeviceRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService,
                new DeviceManagementMapper());

        this.throwable = null;
        this.request = null;
        this.response = null;

    }

    // === GIVEN ===

    @DomainStep("a revoke key request with (.*)")
    public void givenARevokeKeyRequest(final String device) {
        LOGGER.info("GIVEN: \"a revoke key request with device [{}].", device);

        this.setUp();

        this.request = new RevokeKeyRequest();
        this.request.setDeviceIdentification(device);
    }

    @DomainStep("the revoke key request refers to an existing device (.*)")
    public void givenTheRevokeKeyRequestRefersToAnExistingDevice(final String device) {
        LOGGER.info("GIVEN: \"the revoke key request refers to an existing device [{}].", device);

        this.device = new DeviceBuilder().withDeviceIdentification(device).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(device).build();

        when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.device);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(this.oslpDevice);
        when(this.deviceRepositoryMock.save(any(Device.class))).thenReturn(this.device);
        when(this.oslpDeviceRepositoryMock.save(any(OslpDevice.class))).thenReturn(this.oslpDevice);
    }

    @DomainStep("the revoke key request refers to a non-existing device (.*)")
    public void givenTheRevokeKeyRequestRefersToANonExistingDevice(final String device) {
        LOGGER.info("GIVEN: \"the revoke key request refers to a non-existing device [{}].", device);

        when(this.deviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
    }

    @DomainStep("the revoke key request refers to an existing organisation that is authorized")
    public void givenTheRevokeKeyRequestRefersToAnExistingOrganisation() {
        LOGGER.info("GIVEN: \"the revoke key request refers to an existing organisation that is authorized\".");

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);

        final List<DeviceAuthorization> authorizations = new ArrayList<>();
        authorizations.add(new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.organisation)
                .withFunctionGroup(DeviceFunctionGroup.OWNER).build());
        when(this.deviceAuthorizationRepositoryMock.findByOrganisationAndDevice(this.organisation, this.device))
                .thenReturn(authorizations);

    }

    // === WHEN ===

    @DomainStep("the revoke key request is received on OSGP")
    public void whenTheRevokeKeyRequestIsReceivedOnOSGP() {
        LOGGER.info("WHEN: \"the revoke key request is received on OSGP\".");

        try {
            this.response = this.deviceManagementEndpoint.revokeKey(ORGANISATION_ID, this.request);

            // /Thread.sleep(1000);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the device's key should be cleared")
    public boolean thenTheDeviceKeyShouldBeCleared() {
        LOGGER.info("THEN: \"the device's key should be cleared\".");

        try {
            final ArgumentCaptor<OslpDevice> oslpDeviceArgument = ArgumentCaptor.forClass(OslpDevice.class);
            verify(this.oslpDeviceRepositoryMock, timeout(10000).times(1)).save(oslpDeviceArgument.capture());

            Assert.assertEquals("Oslp Devices should match", this.device.getDeviceIdentification(), oslpDeviceArgument
                    .getValue().getDeviceIdentification());
            Assert.assertFalse("Oslp Device key should be cleared", oslpDeviceArgument.getValue().isPublicKeyPresent());

            final ArgumentCaptor<Device> deviceArgument = ArgumentCaptor.forClass(Device.class);
            verify(this.deviceRepositoryMock, timeout(10000).times(1)).save(deviceArgument.capture());

            Assert.assertEquals("Devices should match", this.device.getDeviceIdentification(), deviceArgument
                    .getValue().getDeviceIdentification());
            Assert.assertFalse("Device key should be cleared", deviceArgument.getValue().isPublicKeyPresent());

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("no device should be created")
    public boolean thenNoDeviceShouldBeCreated() {
        LOGGER.info("THEN: \"no device should be created\".");

        try {
            verify(this.deviceRepositoryMock, times(0)).save(any(Device.class));
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }

        return true;
    }

    @DomainStep("the revoke key request should return a revoke key response")
    public boolean thenTheRequestShouldReturnAResponse() {
        LOGGER.info("THEN: \"the revoke key request should return a revoke key response\".");

        try {
            Assert.assertNotNull("Response should not be null", this.response);
            Assert.assertNull("Throwable should be null", this.throwable);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }

    @DomainStep("the revoke key request should return an error message")
    public boolean thenTheRequestShouldReturnAnError() {
        LOGGER.info("THEN: \"the revoke key request should return an error message\".");

        try {
            Assert.assertNull("Response should be null", this.response);
            Assert.assertNotNull("Throwable should not be null", this.throwable);

        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
            return false;
        }
        return true;
    }
}
