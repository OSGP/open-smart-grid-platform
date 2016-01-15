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
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import com.alliander.osgp.adapter.ws.core.application.services.DeviceInstallationService;
import com.alliander.osgp.adapter.ws.core.endpoints.DeviceInstallationEndpoint;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceAuthorizationBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

@Configurable
@DomainSteps
public class AddDeviceSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDeviceSteps.class);

    private static final String DEVICE_ID = "NewDevice";
    private static final String DEVICE_ID_EMPTY = "";
    private static final String DEVICE_ID_SPACES = "   ";
    private static final String DEVICE_ID_INVALID = "ThisDeviceIdentificationIsWayTooLooooooooooooooooooooooooooooooooooooooooong";
    private static final String DEVICE_ID_EXISTING = "ExistingDevice";
    private static final String ORGANISATION_ID_OWNER = "OwnerOrganisation";
    private static final String ORGANISATION_ID_UNKNOWN = "UnknownOrganisation";
    private static final String ORGANISATION_ID_EMPTY = "";

    private static final String OSLP_1_0_PROTOCOL = "OSLP";
    private static final String OSLP_1_0_PROTOCOL_VERSION = "1.0";

    private String organisation;
    private AddDeviceRequest request;
    private AddDeviceResponse response;
    private Throwable throwable;

    private Ssld device;
    private Organisation owner;
    private DeviceAuthorization authOwner;

    private DeviceInstallationEndpoint deviceInstallationEndpoint;

    // Repository Mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private SsldRepository ssldRepositoryMock;
    @Autowired
    private WritableDeviceRepository writableDeviceRepositoryMock;
    @Autowired
    private WritableSsldRepository writableSsldRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository authorizationRepositoryMock;
    @Autowired
    private WritableDeviceAuthorizationRepository writableAuthorizationRepositoryMock;
    @Autowired
    private ProtocolInfoRepository protocolInfoRepositoryMock;

    // //Channel Mock
    //
    // @Autowired private OslpDeviceService oslpDeviceService;

    // Application Services
    @Autowired
    @Qualifier(value = "wsCoreDeviceInstallationService")
    private DeviceInstallationService deviceInstallationService;

    public void setUp() {
        Mockito.reset(new Object[] { this.authorizationRepositoryMock, this.deviceRepositoryMock,
                this.ssldRepositoryMock, this.writableAuthorizationRepositoryMock, this.writableDeviceRepositoryMock,
                this.writableSsldRepositoryMock, this.organisationRepositoryMock });

        final DeviceInstallationMapper deviceInstallationMapper = new DeviceInstallationMapper();
        deviceInstallationMapper.initialize();

        this.deviceInstallationEndpoint = new DeviceInstallationEndpoint(this.deviceInstallationService,
                deviceInstallationMapper);

        this.device = new Ssld(DEVICE_ID, "alias", "city", "postal-code", "street", "street-number", "municipality",
                12.1234F, 14.1234F);

        this.owner = new OrganisationBuilder().withOrganisationIdentification(ORGANISATION_ID_OWNER).build();

        this.device.addAuthorization(this.owner, DeviceFunctionGroup.OWNER);

        this.authOwner = new DeviceAuthorizationBuilder().withDevice(this.device).withOrganisation(this.owner)
                .withFunctionGroup(DeviceFunctionGroup.OWNER).build();

        this.request = null;
        this.response = null;
        this.throwable = null;
    }

    // === GIVEN ===

    @DomainStep("a valid add device request")
    public void givenAValidAddDeviceRequest() {
        LOGGER.info("GIVEN: \"a valid add device request\".");

        this.setUp();

        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(null);
        when(this.deviceRepositoryMock.save(any(Device.class))).thenReturn(this.device);

        when(this.ssldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(null);
        when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);
        when(this.ssldRepositoryMock.save(any(Ssld.class))).thenReturn(this.device);

        when(this.writableDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(null);
        when(this.writableDeviceRepositoryMock.save(any(Device.class))).thenReturn(this.device);

        when(this.writableSsldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(null);
        when(this.writableSsldRepositoryMock.findOne(1L)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.save(any(Ssld.class))).thenReturn(this.device);

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID_OWNER)).thenReturn(
                this.owner);

        when(this.authorizationRepositoryMock.findByOrganisationAndDevice(this.owner, this.device)).thenReturn(
                Arrays.asList(this.authOwner));
        when(this.authorizationRepositoryMock.save(eq(this.authOwner))).thenReturn(this.authOwner);
        when(this.writableAuthorizationRepositoryMock.save(eq(this.authOwner))).thenReturn(this.authOwner);

        when(this.protocolInfoRepositoryMock.findByProtocolAndProtocolVersion(any(String.class), any(String.class)))
        .thenReturn(ProtocolInfoTestUtils.getProtocolInfo(OSLP_1_0_PROTOCOL, OSLP_1_0_PROTOCOL_VERSION));

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_OWNER;
    }

    @DomainStep("an add device request with empty device identification")
    public void givenAnAddDeviceRequestWithEmptyDeviceIdentification() {
        LOGGER.info("GIVEN: \"an add device request with empty device identification\".");

        this.setUp();

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID_OWNER)).thenReturn(
                this.owner);

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID_EMPTY);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_OWNER;
    }

    @DomainStep("an add device request with only spaces as device identification")
    public void givenAnAddDeviceRequestWithOnlySpacesAsDeviceIdentification() {
        LOGGER.info("GIVEN: \"an add device request with only spaces as device identification\".");

        this.setUp();

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID_SPACES);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_OWNER;
    }

    @DomainStep("an add device request with invalid device identification")
    public void givenAnAddDeviceRequestWithInvalidDeviceIdentification() {
        LOGGER.info("GIVEN: \"an add device request with invalid device identification\".");

        this.setUp();

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID_INVALID);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_OWNER;
    }

    @DomainStep("an add device request with existing device identification")
    public void givenAnAddDeviceRequestWithExistingDeviceIdentification() {
        LOGGER.info("GIVEN: \"an add device request with existing device identification\".");

        this.setUp();

        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID_EXISTING)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(DEVICE_ID_EXISTING)).thenReturn(this.device);
        when(this.writableDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID_EXISTING)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.findByDeviceIdentification(DEVICE_ID_EXISTING)).thenReturn(this.device);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID_OWNER)).thenReturn(
                this.owner);
        when(this.authorizationRepositoryMock.findByDeviceAndFunctionGroup(this.device, DeviceFunctionGroup.OWNER))
        .thenReturn(Arrays.asList(this.authOwner));
        when(
                this.writableAuthorizationRepositoryMock.findByDeviceAndFunctionGroup(this.device,
                        DeviceFunctionGroup.OWNER)).thenReturn(Arrays.asList(this.authOwner));

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID_EXISTING);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_OWNER;
    }

    @DomainStep("an add device request with unknown owner organisation")
    public void givenAnAddDeviceRequestWithUnknownOwnerOrganisation() {
        LOGGER.info("GIVEN: \"an add device request with unknown owner organisation\".");

        this.setUp();

        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);

        when(this.writableDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.findOne(1L)).thenReturn(this.device);

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID_UNKNOWN))
        .thenReturn(null);

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_UNKNOWN;
    }

    @DomainStep("an add device request with empty owner organisation")
    public void givenAnAddDeviceRequestWithEmptyOwnerOrganisation() {
        LOGGER.info("GIVEN: \"an add device request with empty owner organisation\".");

        this.setUp();

        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.ssldRepositoryMock.findOne(1L)).thenReturn(this.device);

        when(this.writableDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.writableSsldRepositoryMock.findOne(1L)).thenReturn(this.device);

        this.request = new AddDeviceRequest();
        final com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device device = new com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device();
        device.setDeviceIdentification(DEVICE_ID);
        this.request.setDevice(device);
        this.organisation = ORGANISATION_ID_EMPTY;
    }

    // === WHEN ===

    @DomainStep("the add device request is received")
    public void whenTheAddDeviceRequestIsReceived() {
        LOGGER.info("WHEN: \"the add device request is received\".");

        try {
            this.response = this.deviceInstallationEndpoint.addDevice(this.organisation, this.request);
        } catch (final Throwable t) {
            if (t instanceof ValidationException) {
                LOGGER.error("Exception: {}", ((ValidationException) t).toString());
            } else {
                LOGGER.error("Exception: {}", t.getClass().getSimpleName());
            }

            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the device should be created")
    public boolean thenTheDeviceShouldBeCreated() {
        LOGGER.info("THEN: \"the device should be created\".");

        try {
            verify(this.writableAuthorizationRepositoryMock, times(1)).save(any(DeviceAuthorization.class));
        } catch (final Throwable t) {
            return false;
        }
        return true;
    }

    @DomainStep("the device should not be created")
    public boolean thenTheDeviceShouldNotBeCreated() {
        LOGGER.info("THEN: \"the device should not be created\".");

        try {
            verify(this.deviceRepositoryMock, times(0)).save(any(Device.class));
        } catch (final Throwable t) {
            LOGGER.info("|Exception: \"the device should not be created\".");
            return false;
        }
        return true;
    }

    @DomainStep("the owner authorization should be created")
    public boolean thenTheOwnerAuthorizationShouldBeSet() {
        LOGGER.info("THEN: \"the owner authorization should be created\".");

        try {
            verify(this.writableAuthorizationRepositoryMock, times(1)).save(eq(this.authOwner));
        } catch (final Throwable t) {
            return false;
        }
        return true;
    }

    @DomainStep("the owner authorization should not be created")
    public boolean thenTheOwnerAuthorizationShouldNotBeSet() {
        LOGGER.info("THEN: \"the owner authorization should not be created\".");

        try {
            verify(this.authorizationRepositoryMock, times(0)).save(any(DeviceAuthorization.class));
        } catch (final Throwable t) {
            return false;
        }
        return true;
    }

    @DomainStep("the add device request should return an add device response")
    public boolean thenTheAddDeviceRequestShouldReturnAnAddDeviceResponse() {
        LOGGER.info("THEN: \"the add device request should return an add device response\".");

        return this.response != null && this.throwable == null;
    }

    @DomainStep("the add device request should return a validation exception")
    public boolean thenTheAddDeviceRequestShouldReturnAValidationException() throws Throwable {
        LOGGER.info("THEN: \"the add device request should return a validation exception\".");

        return this.throwable != null && this.throwable.getCause() instanceof ValidationException;
    }

    @DomainStep("the add device request should return an existing entity exception")
    public boolean thenTheAddDeviceRequestShouldReturnAnExistingEntityException() {
        LOGGER.info("THEN: \"the add device request should return an existing entity exception\".");

        return this.throwable != null && this.throwable.getCause() instanceof ExistingEntityException;
    }

    @DomainStep("the add device request should return an unknown entity exception")
    public boolean thenTheAddDeviceRequestShouldReturnAnUnknownEntityException() {
        LOGGER.info("THEN: \"the add device request should return an unknown entity exception\".");

        return this.throwable != null && this.throwable.getCause() instanceof UnknownEntityException;
    }
}
