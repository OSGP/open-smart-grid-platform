package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

@Configurable
@DomainSteps
public class SetOwnerSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetOwnerSteps.class);
    private static final String ORGANISATION_ID = "ORGANISATION-01";
    private static final String OWNER_ORGANISATION_ID = "ORGANISATION-02";
    private static final String NEWOWNER_ORGANISATION_ID = "ORGANISATION-03";
    private static final String ORGANISATION_PREFIX = "ORG";
    private DeviceManagementEndpoint deviceManagementEndpoint;
    private SetOwnerRequest request;
    private SetOwnerResponse response;
    private Device device;
    private Organisation organisation;
    private Organisation ownerOrganisation;
    private Organisation newOwnerOrganisation;

    // Repository Mocks
    @Autowired
    private DeviceRepository deviceRepositoryMock;
    @Autowired
    private OrganisationRepository organisationRepositoryMock;
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepositoryMock;

    // Application Services
    @Autowired
    private DeviceManagementService deviceManagementService;

    private void setUp() {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.organisationRepositoryMock,
                this.deviceAuthorizationRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService,
                new DeviceManagementMapper());

        this.request = null;
        this.response = null;

        this.organisation = new Organisation(ORGANISATION_ID, ORGANISATION_ID, ORGANISATION_PREFIX,
                PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ID)).thenReturn(
                this.organisation);
    }

    // === GIVEN ===

    @DomainStep("a device (.*) with owner (.*)")
    public void givenADeviceWithOwner(final String deviceIdentification, final String ownerOrganisationName) {
        this.setUp();

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification).build();

        if (ownerOrganisationName != null && ownerOrganisationName != "") {
            this.ownerOrganisation = new Organisation(OWNER_ORGANISATION_ID, ownerOrganisationName,
                    ORGANISATION_PREFIX, PlatformFunctionGroup.ADMIN);
            this.device.addAuthorization(this.ownerOrganisation, DeviceFunctionGroup.OWNER);
        }

        when(this.deviceRepositoryMock.findByDeviceIdentification(this.device.getDeviceIdentification())).thenReturn(
                this.device);
    }

    @DomainStep("a valid set owner request with device (.*) and neworganisation (.*)")
    public void givenAValidSetOwnerRequest(final String deviceIdentification, final String newOrganisationName) {
        this.newOwnerOrganisation = new Organisation(NEWOWNER_ORGANISATION_ID, newOrganisationName,
                ORGANISATION_PREFIX, PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(NEWOWNER_ORGANISATION_ID)).thenReturn(
                this.newOwnerOrganisation);

        this.request = new SetOwnerRequest();
        this.request.setDeviceIdentification(deviceIdentification);
        this.request.setOrganisationIdentification(this.newOwnerOrganisation.getOrganisationIdentification());
    }

    // === WHEN ===

    @DomainStep("the set owner request is received on OSGP")
    public void whenTheSetOwnerRequestIsReceivedOnOSGP() throws UnknownEntityException, ValidationException,
            NotAuthorizedException {
        LOGGER.info("WHEN: \"the set owner request is received on OSGP\".");

        try {
            this.response = this.deviceManagementEndpoint.setOwner(ORGANISATION_ID, this.request);
        } catch (final Throwable t) {
            LOGGER.error("Exception [{}]: {}", t.getClass().getSimpleName(), t.getMessage());
        }
    }

    // === THEN ===

    @DomainStep("the device's owner is updated to (.*)")
    public boolean thenTheDeviceOwnerIsUpdatedTo(final String newOrganisationName) {
        verify(this.deviceAuthorizationRepositoryMock, times(1)).save(
                new DeviceAuthorization(this.device, this.newOwnerOrganisation, DeviceFunctionGroup.OWNER));
        return true;
    }

    @DomainStep("the set owner response is returned")
    public boolean andTheSetOwnerResponseIsReturned() {
        return this.response != null;
    }
}
