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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaSystemException;

import com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint;
import com.alliander.osgp.adapter.ws.core.application.mapping.DeviceManagementMapper;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.OrganisationBuilder;
import com.alliander.osgp.domain.core.exceptions.ExistingEntityException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Configurable
@DomainSteps
public class CreateNewOrganisationSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateNewOrganisationSteps.class);

    private static final String ORGANISATION_ROOT = "Heerlen";
    private static final String GRID_MANAGMENT_ORGANISATION = "LianderNetManagement";
    private static final String ORGANISATION_IDENTIFICATION = "GemeenteArnhem";
    private static final String ORGANISATION_NAME = "Gemeente Arnhem";
    private static final String ORGANISATION_PREFIX = "ORG";

    private com.alliander.osgp.adapter.ws.admin.endpoints.DeviceManagementEndpoint adminDeviceManagementEndpoint;
    private com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint coreDeviceManagementEndpoint;

    @Autowired
    @Qualifier(value = "wsAdminDeviceManagementService")
    private com.alliander.osgp.adapter.ws.admin.application.services.DeviceManagementService adminDeviceManagementService;

    @Autowired
    @Qualifier(value = "wsCoreDeviceManagementService")
    private com.alliander.osgp.adapter.ws.core.application.services.DeviceManagementService coreDeviceManagementService;
    @Autowired
    @Qualifier("coreDeviceManagementMapper")
    private DeviceManagementMapper deviceManagementMapper;

    private Organisation newOrganisation;

    @Autowired
    private OrganisationRepository organisationRepositoryMock;

    private com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation organisationToBeAdded;
    private Throwable throwable;
    private FindAllOrganisationsResponse responseList;
    private List<Organisation> organisations;

    public void setUp() {
        Mockito.reset(new Object[] { this.organisationRepositoryMock });

        this.adminDeviceManagementEndpoint = new DeviceManagementEndpoint(this.adminDeviceManagementService,
                new com.alliander.osgp.adapter.ws.admin.application.mapping.DeviceManagementMapper());

        this.coreDeviceManagementEndpoint = new com.alliander.osgp.adapter.ws.core.endpoints.DeviceManagementEndpoint(
                this.coreDeviceManagementService, this.deviceManagementMapper);

        this.newOrganisation = new OrganisationBuilder().withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
                .withName(ORGANISATION_NAME).withPrefix(ORGANISATION_PREFIX)
                .withFunctionGroup(PlatformFunctionGroup.USER).build();

        this.throwable = null;
    }

    @DomainStep("a valid organisation (.*) with (.*) and functionGroup (.*)")
    public void givenAValidOrganisationWithAndFunctionGroup(final String name, final String organisationIdentification,
            final String functionGroup) {

        LOGGER.info("GIVEN: \"a valid organisation {} with {} and functiongroup {}\".", name,
                organisationIdentification, functionGroup);

        this.setUp();

        // Create a valid organisation whith the given data.
        this.organisationToBeAdded = new com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation();
        this.organisationToBeAdded.setOrganisationIdentification(organisationIdentification);
        this.organisationToBeAdded.setName(name);
        this.organisationToBeAdded.setPrefix(ORGANISATION_PREFIX);
        this.organisationToBeAdded
        .setFunctionGroup(com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup
                .valueOf(functionGroup));
        this.organisationToBeAdded.getDomains().add(PlatformDomain.COMMON);
        this.organisationToBeAdded.getDomains().add(PlatformDomain.PUBLIC_LIGHTING);
        this.organisationToBeAdded.getDomains().add(PlatformDomain.TARIFF_SWITCHING);
    }

    @DomainStep("an invalid organisation (.*) with (.*) and functionGroup (.*)")
    public void givenAnInvalidOrganisationWithAndFunctionGroup(final String name,
            final String organisationIdentification, final String functionGroup) throws IOException {

        LOGGER.info("GIVEN: \"an invalid organisation {} with {} and functiongroup {}\".", name,
                organisationIdentification, functionGroup);

        this.setUp();
        // Create a invalid organisation with an invalid identification to be
        // added.
        this.organisationToBeAdded = new com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation();
        this.organisationToBeAdded.setOrganisationIdentification(organisationIdentification);
        this.organisationToBeAdded.setName(name);
        this.organisationToBeAdded.setPrefix(ORGANISATION_PREFIX);
        this.organisationToBeAdded
        .setFunctionGroup(com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup
                .valueOf(functionGroup));
        this.organisationToBeAdded.getDomains().add(PlatformDomain.COMMON);
        this.organisationToBeAdded.getDomains().add(PlatformDomain.PUBLIC_LIGHTING);
        this.organisationToBeAdded.getDomains().add(PlatformDomain.TARIFF_SWITCHING);
    }

    @DomainStep("two organisations")
    public void givenTwoOrganisations() {

        LOGGER.info("GIVEN: \"two organisations\".");

        this.setUp();
        this.organisations = new ArrayList<Organisation>();
        Organisation organisation = new Organisation("GemeenteHeerlen", "Heerlen", "HRL", PlatformFunctionGroup.USER);
        this.organisations.add(organisation);
        organisation = new Organisation("LianderNetManagement", "Liander Net Management", "LIA",
                PlatformFunctionGroup.ADMIN);
        this.organisations.add(organisation);
    }

    @DomainStep("an operator part of the grid management organisation")
    public void anOperatorPartOfTheGridManagementOrganisation() {
        // nothing special needed here, organisations list index 1
    }

    @DomainStep("an operator part of a municipality organisation")
    public void givenAnOperatorPartOfMunicipalityOrganisation() {
        // nothing special needed here, organisations list index 0
    }

    @DomainStep("creating a new organization")
    public void whenCreatingANewOrganization() throws Throwable {

        LOGGER.info("WHEN: \"creating a new organisation\".");

        // Create the request
        final CreateOrganisationRequest request = new CreateOrganisationRequest();
        request.setOrganisation(this.organisationToBeAdded);

        // Expect the organisation being search, but not found.
        MockitoAnnotations.initMocks(this);
        when(this.organisationRepositoryMock.save(any(Organisation.class))).thenReturn(this.newOrganisation);

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT))
        .thenReturn(
                new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT, ORGANISATION_PREFIX,
                        PlatformFunctionGroup.ADMIN));
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_IDENTIFICATION)).thenReturn(
                null);

        this.adminDeviceManagementEndpoint.createOrganisation(ORGANISATION_ROOT, request);
    }

    @DomainStep("creating an organisation that already exists on the platform")
    public void whenCreatingAnOrganisationThatAlreadyExistsOnThePlatform() throws Throwable {

        LOGGER.info("WHEN: \"creating an organisation that already exists on the platform\".");

        // Create the request
        final CreateOrganisationRequest request = new CreateOrganisationRequest();
        request.setOrganisation(this.organisationToBeAdded);

        // Expect the organisation being search, and found.
        MockitoAnnotations.initMocks(this);
        // Make sure that the adding organisation is authorized.
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT))
        .thenReturn(
                new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT, ORGANISATION_PREFIX,
                        PlatformFunctionGroup.ADMIN));

        // And that when saving the organisation an exception is thrown.
        when(this.organisationRepositoryMock.save(any(Organisation.class))).thenThrow(
                new JpaSystemException(new PersistenceException("Duplicate entry")));

        try {
            this.adminDeviceManagementEndpoint.createOrganisation(ORGANISATION_ROOT, request);
        } catch (final OsgpException ex) {
            this.throwable = ex;
        }
    }

    @DomainStep("creating an organisation with an invalid organization identification")
    public void whenCreatingAnOrganisationWithAnInvalidOrganisationIdentification() {

        LOGGER.info("WHEN: \"creating an organisation with an invalid organisation identification\".");

        // Create the request
        final CreateOrganisationRequest request = new CreateOrganisationRequest();
        request.setOrganisation(this.organisationToBeAdded);

        // Expect the organisation being search, and found.
        MockitoAnnotations.initMocks(this);
        // Make sure that the adding organisation is authorized.
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT))
        .thenReturn(
                new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT, ORGANISATION_PREFIX,
                        PlatformFunctionGroup.ADMIN));

        try {
            this.adminDeviceManagementEndpoint.createOrganisation(ORGANISATION_ROOT, request);
        } catch (final Throwable t) {
            this.throwable = t;
        }
    }

    @DomainStep("the grid managment organisation operator views the list of organisations")
    public void whenTheGridManagementOrganisationOperatorViewsTheListOfOrganisations() throws Throwable {

        LOGGER.info("WHEN: \"the operator views the list of organisations\".");

        // Expect the organisation being search, and found.
        MockitoAnnotations.initMocks(this);
        // Make sure that the adding organisation is authorized.
        when(this.organisationRepositoryMock.findByOrganisationIdentification(GRID_MANAGMENT_ORGANISATION)).thenReturn(
                new Organisation(GRID_MANAGMENT_ORGANISATION, GRID_MANAGMENT_ORGANISATION, "LIA",
                        PlatformFunctionGroup.ADMIN));

        when(this.organisationRepositoryMock.findAll()).thenReturn(this.organisations);

        final FindAllOrganisationsRequest request = new FindAllOrganisationsRequest();

        // Find all organisations.
        this.responseList = this.coreDeviceManagementEndpoint
                .findAllOrganisations(GRID_MANAGMENT_ORGANISATION, request);
    }

    @DomainStep("the operator views the list of organisations")
    public void whenTheOperatorViewsTheListOfOrganisations() throws Throwable {

        LOGGER.info("WHEN: \"the operator views the list of organisations\".");

        // Expect the organisation being search, and found.
        MockitoAnnotations.initMocks(this);
        // Make sure that the adding organisation is authorized.
        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT))
        .thenReturn(
                new Organisation(ORGANISATION_ROOT, ORGANISATION_ROOT, ORGANISATION_PREFIX,
                        PlatformFunctionGroup.ADMIN));

        when(this.organisationRepositoryMock.findByOrganisationIdentification(ORGANISATION_ROOT)).thenReturn(
                this.organisations.get(0));

        final FindAllOrganisationsRequest request = new FindAllOrganisationsRequest();

        // Find all organisations.
        this.responseList = this.coreDeviceManagementEndpoint.findAllOrganisations(ORGANISATION_ROOT, request);
    }

    @DomainStep("the organization will be added to the platform")
    public boolean thenTheOrganizationWillBeAddedToThePlatform() throws Throwable {

        LOGGER.info("THEN: \"the organisation will be added to the platform\".");

        // Test this by verifying if the save method is called at the
        // organisationRepositoryMock once.
        verify(this.organisationRepositoryMock, times(1)).save(any(Organisation.class));
        return true;
    }

    @DomainStep("the organisation is not added to the platform")
    public boolean thenTheOrganisationIsNotAddedToThePlatform() {

        LOGGER.info("THEN: \"the organisation is not added to the platform\".");

        // Test this by verifying if the save method is called at the
        // organisationRepositoryMock once.
        verify(this.organisationRepositoryMock, times(1)).save(any(Organisation.class));

        return this.throwable.getCause() instanceof ExistingEntityException;
    }

    @DomainStep("the organisation is not added to the platform due to the invalid organisation")
    public boolean thenTheOrganisationIsNotAddedToThePlatformDueToTheInvalidOrganisation() {

        LOGGER.info("THEN: \"the organisation is not added to the platform due to the invalid organisation\".");

        // Test this by verifying if the save method is not called at the
        // organisationRepositoryMock once.
        verify(this.organisationRepositoryMock, times(0)).save(any(Organisation.class));

        // And that the invalid organisation exception was thrown.
        return this.throwable.getCause() instanceof ValidationException;
    }

    @DomainStep("a list with the two organisations is returned")
    public boolean thenAListWithTheTwoOrganisationsIsReturned() {

        LOGGER.info("THEN: \"a list with the two organisations is returned\".");

        // Verify that the findByOrganisationIdentification() method is
        // executed.
        verify(this.organisationRepositoryMock, times(1)).findAll();

        // Verify that the requested organisation exists.
        for (final Organisation organisation : this.organisations) {
            boolean organisationFoundInResultList = false;
            for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Organisation resultOrganisation : this.responseList
                    .getOrganisations()) {
                if (organisation.getOrganisationIdentification().equals(
                        resultOrganisation.getOrganisationIdentification())) {
                    organisationFoundInResultList = true;
                    if (organisation.getName().equals(resultOrganisation.getName())
                            && organisation.getFunctionGroup().ordinal() == resultOrganisation.getFunctionGroup()
                                    .ordinal()) {
                        // Do nothing, oke
                    } else {
                        return false;
                    }
                }
            }

            if (!organisationFoundInResultList) {
                return false;
            }
        }
        return true;
    }

    @DomainStep("a list containing the municipality organisation is returned")
    public boolean thenAListContainingTheMunicipalityOrganisationIsReturned() {

        LOGGER.info("THEN: \"a list containing the municipality organisation is returned\".");

        // Verify that the findByOrganisationIdentification() method is
        // executed.
        verify(this.organisationRepositoryMock, times(2)).findByOrganisationIdentification(ORGANISATION_ROOT);

        // Verify that the requested organisation exists.
        final Organisation organisation = this.organisations.get(0);

        boolean organisationFoundInResultList = false;
        for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Organisation resultOrganisation : this.responseList
                .getOrganisations()) {
            if (organisation.getOrganisationIdentification().equals(resultOrganisation.getOrganisationIdentification())) {
                organisationFoundInResultList = true;
                if (organisation.getName().equals(resultOrganisation.getName())
                        && organisation.getFunctionGroup().ordinal() == resultOrganisation.getFunctionGroup().ordinal()) {
                    // Do nothing, oke
                } else {
                    return false;
                }
            }
        }

        if (!organisationFoundInResultList) {
            return false;
        }

        return true;
    }

    @DomainStep("the operator receives feedback about the addition")
    public boolean thenTheOperatorReceivesFeedbackAboutTheAddition() {

        LOGGER.info("TODO: THEN: \"the operator receives feedback about the addition\".");

        // implement step
        return true;
    }

    @DomainStep("the operator receives an error message indicating that the organisation already exists")
    public boolean thenTheOperatorReceivesAnErrorMessageIndicatingThatTheOrganisationAlreadyExists() {

        LOGGER.info("TODO: THEN: \"the operator receives an error message indicating that the organisation already exists\".");

        // implement step
        return true;
    }

    @DomainStep("the operator will receive an error message")
    public boolean thenTheOperatorWillReceiveAnErrorMessage() {

        LOGGER.info("TODO: THEN: \"the operator will receive an error message\".");

        // implement step
        return true;
    }
}
