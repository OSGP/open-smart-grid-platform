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
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;

@Configurable
@DomainSteps
public class ChangeOrganisationDataSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveOrganisationSteps.class);

    private static final String ORGANISATION_ROOT = "Alliander";
    private static final String ORGANISATION_ROOT_NAME = "Alliander";

    private static final String ORGANISATION_PREFIX = "ORG";

    private DeviceManagementEndpoint deviceManagementEndpoint;

    @Autowired
    private DeviceManagementService deviceManagementService;

    private Organisation adminOrganisation; // the org. issuing the change
                                            // request (Alliander)
    private Organisation changedOrganisation;// the org. being changed

    private boolean isExisting;

    private ChangeOrganisationRequest request;
    private ChangeOrganisationResponse response;

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

    @DomainStep("a change organisation request for an isExisting (.*) organisation (.*) with name (.*) and platformFunctionGroup (.*) with newOrganisationIdentification (.*) with newName (.*) and newPlatformFunctionGroup (.*)")
    public void givenAChangeOrganisationRequest(final boolean isExisting, final String organisationIdentification, final String name,
            final String platformFunctionGroup, final String newOrganisationIdentification, final String newName, final String newPlatformFunctionGroup) {
        LOGGER.info("GIVEN: \"a change organisation request for an organisation {}\"", organisationIdentification);

        this.setUp();

        // Create the request
        this.request = new ChangeOrganisationRequest();

        this.request.setOrganisationIdentification(organisationIdentification);
        this.request.setNewOrganisationIdentification(newOrganisationIdentification);
        this.request.setNewOrganisationName(newName);
        // this.request.setNewOrganisationPrefix("");
        this.request.setNewOrganisationPlatformFunctionGroup(PlatformFunctionGroup.valueOf(newPlatformFunctionGroup));

        this.isExisting = isExisting;

        if (isExisting) {
            final com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup pfg = com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup
                    .valueOf(platformFunctionGroup);

            this.changedOrganisation = new OrganisationBuilder().withOrganisationIdentification(organisationIdentification).withName(name)
                    .withFunctionGroup(pfg).build();

            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisationIdentification)).thenReturn(this.changedOrganisation);
        } else {
            when(this.organisationRepositoryMock.findByOrganisationIdentification(organisationIdentification)).thenReturn(null);
        }
    }

    @DomainStep("the change organisation request refers to an organisation that is authorised")
    public void givenTheChangeOrganisationRequestRefersToAnOrganisationThatIsAuthorised() {
        LOGGER.info("GIVEN: \"the change organisation request refers to an organisation that is authorised: {}\".", ORGANISATION_ROOT);

        this.adminOrganisation = new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT_NAME, ORGANISATION_PREFIX,
                com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup.ADMIN);
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT)).thenReturn(this.adminOrganisation);
    }

    // === WHEN ===

    @DomainStep("changing the data of an organisation")
    public void whenChangingTheDataOfAnOrganisation() {
        LOGGER.info("WHEN: \"changing the data of an organisation\".");

        try {
            this.response = this.deviceManagementEndpoint.changeOrganisation(ORGANISATION_ROOT, this.request);
        } catch (final Throwable t) {
            this.throwable = t;
        }
    }

    // === THEN ===

    @DomainStep("the organisations data will be changed in the platform database")
    public boolean thenTheOrganisationsDataWillBeChangedInThePlatformDatabase() {
        LOGGER.info("THEN: \"the organisation's data will be changed in the platform database\".");

        if (this.isExisting) {
            verify(this.organisationRepositoryMock, times(1)).save(any(Organisation.class));

            Assert.assertNull(this.throwable);
        } else {
            Assert.assertNotNull(this.throwable);
        }

        return true;
    }

    @DomainStep("the change organisation request should return result (.*)")
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
