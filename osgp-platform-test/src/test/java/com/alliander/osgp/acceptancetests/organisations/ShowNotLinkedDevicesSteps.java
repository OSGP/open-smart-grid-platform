/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.organisations;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Configurable
@DomainSteps
public class ShowNotLinkedDevicesSteps {

    private static final String ORGANISATION_IDENTIFICATION = "Alliander";
    private static final String ORGANISATION_NAME = "Alliander";
    private static final String DEVICE_ID = "l01";

    private DeviceManagementEndpoint deviceManagementEndpoint;
    private List<Device> devices;
    private Organisation organisation;

    // Application Service
    @Autowired
    private DeviceManagementService deviceManagementService;

    // Repository Mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;

    private FindDevicesWhichHaveNoOwnerResponse response;

    public void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService, new DeviceManagementMapper());

        this.organisation = new OrganisationBuilder().withOrganisationIdentification(ORGANISATION_IDENTIFICATION).withName(ORGANISATION_NAME)
                .withFunctionGroup(PlatformFunctionGroup.ADMIN).build();
    }

    @DomainStep("an existing valid organisation")
    public void givenAnExistingValidOrganisation() {
        this.setUp();
    }

    @DomainStep("one device which is not linked to an organisation")
    public void andOneDeviceWhichIsNotLinkedToAnOrganisation() {
        final Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        this.devices = new ArrayList<Device>();
        this.devices.add(device);
    }

    @DomainStep("a device which is connected to the organisation")
    public void andADeviceWhichIsConnectedToTheOrganisation() {
        // Prepare the result.
        this.devices = new ArrayList<Device>();
    }

    @DomainStep("viewing the devices which are not linked to an organisation")
    public void whenViewingTheDevicesWhichAreNotLinkedToAnOrganisation() throws OsgpException {
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_IDENTIFICATION)).thenReturn(this.organisation);
        when(this.deviceRepositoryMock.findDevicesWithNoOwner()).thenReturn(this.devices);

        // Create request ...
        final FindDevicesWhichHaveNoOwnerRequest request = new FindDevicesWhichHaveNoOwnerRequest();

        // And execute the request
        this.response = this.deviceManagementEndpoint.findDevicesWhichHaveNoOwner(ORGANISATION_IDENTIFICATION, request);
    }

    @DomainStep("that device is shown in the list of not to an organisation linked devices")
    public boolean thenThatDeviceIsShownInTheListOfNotToAnOrganisationLinkedDevices() {
        return this.response.getDevices().size() == 1;
    }

    @DomainStep("an message shall be shown stating that all devices are connected to an organisation")
    public boolean thenAnMessageShallBeShownStatingThatAllDevicesAreConnectedToAnOrganisation() {
        return this.response.getDevices().size() == 0;
    }
}
