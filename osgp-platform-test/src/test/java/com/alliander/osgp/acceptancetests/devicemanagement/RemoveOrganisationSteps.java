package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;

@Configurable
@DomainSteps
public class RemoveOrganisationSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveOrganisationSteps.class);

    private static final String ORGANISATION_ROOT = "Alliander";
    private static final String ORGANISATION_ROOT_NAME = "Alliander";
    private static final String ORGANISATION_PREFIX = "LIA";

    private DeviceManagementEndpoint deviceManagementEndpoint;

    private RemoveOrganisationRequest request;
    private RemoveOrganisationResponse response;

    private boolean isExisting;

    @Autowired
    private DeviceManagementService deviceManagementService;

    private Organisation adminOrganisation;
    private Organisation removedOrganisation;

    @Autowired
    private OrganisationRepository organisationRepositoryMock;

    private Throwable throwable;

    // === SET UP ===

    private void setUp() {
        Mockito.reset(new Object[] { this.organisationRepositoryMock });

        this.deviceManagementEndpoint = new DeviceManagementEndpoint(this.deviceManagementService, new DeviceManagementMapper());

        this.throwable = null;
    }

    // === GIVEN ===

    @DomainStep("a remove organisation request for an isExisting (.*) organisation (.*) with name (.*) in platformFunctionGroup (.*)")
    public void givenARemoveOrganisationRequest(final Boolean isExisting, final String organisationIdentification, final String name,
            final String platformFunctionGroup) {
        LOGGER.info("GIVEN: \"a remove organisation request for an isExisting {} organisation {}\"", isExisting, organisationIdentification);

        this.setUp();

        // Create the request
        this.request = new RemoveOrganisationRequest();

        this.request.setOrganisationIdentification(organisationIdentification);

        this.isExisting = isExisting;

        if (isExisting) {
            this.removedOrganisation = new OrganisationBuilder().withOrganisationIdentification(organisationIdentification).withName(name)
                    .withFunctionGroup(PlatformFunctionGroup.valueOf(platformFunctionGroup)).build();

            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisationIdentification)).thenReturn(this.removedOrganisation);
        } else {
            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisationIdentification)).thenReturn(null);
        }

    }

    @DomainStep("the remove organisation request refers to an organisation that is authorised")
    public void givenTheRemoveOrganisationRequestRefersToAnOrganisationThatIsAuthorised() {
        LOGGER.info("GIVEN: \"the remove organisation request refers to an organisation that is authorised: {}\".", ORGANISATION_ROOT);

        this.adminOrganisation = new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT_NAME, ORGANISATION_PREFIX, PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT)).thenReturn(this.adminOrganisation);

    }

    // === WHEN ===

    @DomainStep("removing an organisation")
    public void whenRemovingAnOrganisation() {
        LOGGER.info("WHEN: \"removing an organisation\".");

        try {
            this.response = this.deviceManagementEndpoint.removeOrganisation(ORGANISATION_ROOT, this.request);
        } catch (final Throwable t) {
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the organisation will be removed from the platform")
    public boolean thenTheOrganisationWillBeRemovedFromThePlatform() {
        LOGGER.info("THEN: \"the organisation will be removed from the platform\".");

        if (this.isExisting) {
            verify(this.organisationRepositoryMock, times(1)).save(any(Organisation.class));

            Assert.assertNull(this.throwable);
        } else {
            Assert.assertNotNull(this.throwable);
        }

        return true;
    }

    @DomainStep("the remove organisation request should return result (.*)")
    public boolean thenTheResponseShouldReturn(final String result) {
        LOGGER.info("THEN: the set remove organisation request should return {}.", result);

        if (result.toUpperCase().equals("OK")) {
            try {
                Assert.assertNotNull("Response should not be null", this.response);
                Assert.assertNull("Throwable should be null", this.throwable);
            } catch (final AssertionError e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        } else {
            try {
                Assert.assertNotNull("Throwable should not be null", this.throwable);
                Assert.assertEquals(result.toUpperCase(), this.throwable.getCause().getClass().getSimpleName().toUpperCase());
            } catch (final AssertionError e) {
                LOGGER.error("Exception [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
                return false;
            }
        }

        return true;
    }

}
