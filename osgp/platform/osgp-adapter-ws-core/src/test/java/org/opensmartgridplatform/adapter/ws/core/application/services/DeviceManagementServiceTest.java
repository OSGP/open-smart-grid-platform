/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.core.application.criteria.SearchEventsCriteria;
import org.opensmartgridplatform.adapter.ws.infra.specifications.JpaEventSpecifications;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class DeviceManagementServiceTest {
  @Mock private SearchEventsCriteria criteria;
  @InjectMocks private DeviceManagementService deviceManagementService;
  @Mock private DomainHelperService domainHelperService;
  @Mock private EventRepository eventRepository;
  @Mock private JpaEventSpecifications eventSpecifications;
  @Mock private PagingSettings pagingSettings;
  @Mock private Specification<Event> specification;
  @Mock private Specification<Event> descriptionSpecification;
  @Mock private Specification<Event> descriptionStartsWithSpecification;

  private static final String DESCRIPTION = "description";
  private static final String DESCRIPTION_STARTS_WITH = "descriptionStartValue";

  @Test
  void findEventsTest() throws FunctionalException {
    this.generalMockForHandleDescriptionTests();

    // set descriptionStartWith value
    when(this.criteria.getDescriptionStartsWith()).thenReturn(DESCRIPTION_STARTS_WITH);
    when(this.criteria.getDescription()).thenReturn(DESCRIPTION);

    when(this.descriptionSpecification.or(this.descriptionStartsWithSpecification))
        .thenReturn(this.specification);

    // call method
    this.deviceManagementService.findEvents(this.criteria);

    // verify the method calls
    verify(this.descriptionSpecification, times(1)).or(this.descriptionStartsWithSpecification);
    verify(this.specification, times(1)).and(this.specification);
  }

  @Test
  void findEventsWithNoDescriptionOrDescriptionStartsWithTest() throws FunctionalException {
    this.generalMockForHandleDescriptionTests();

    // call method
    this.deviceManagementService.findEvents(this.criteria);

    // verify the method calls
    verify(this.specification, never()).and(this.descriptionStartsWithSpecification);
    verify(this.specification, never()).and(this.descriptionSpecification);
    verify(this.specification, never()).and(this.specification);
  }

  @Test
  void findEventsWithNoDescriptionTest() throws FunctionalException {
    this.generalMockForHandleDescriptionTests();

    // set descriptionStartWith value
    when(this.criteria.getDescriptionStartsWith()).thenReturn(DESCRIPTION_STARTS_WITH);

    // call method
    this.deviceManagementService.findEvents(this.criteria);

    // verify the method calls
    verify(this.specification, times(1)).and(this.descriptionStartsWithSpecification);
    verify(this.specification, never()).and(this.descriptionSpecification);
    verify(this.descriptionSpecification, never()).or(this.descriptionStartsWithSpecification);
  }

  @Test
  void findEventsWithNoDescriptionStartsWithTest() throws FunctionalException {
    this.generalMockForHandleDescriptionTests();

    // set description value
    when(this.criteria.getDescription()).thenReturn(DESCRIPTION);

    // call method
    this.deviceManagementService.findEvents(this.criteria);

    // verify method calls
    verify(this.specification, never()).and(this.descriptionStartsWithSpecification);
    verify(this.specification, times(1)).and(this.descriptionSpecification);
    verify(this.descriptionSpecification, never()).or(this.descriptionStartsWithSpecification);
  }

  private void generalMockForHandleDescriptionTests() throws FunctionalException {
    when(this.criteria.getOrganisationIdentification()).thenReturn("orgIdentification");
    when(this.criteria.getDeviceIdentification()).thenReturn("deviceIdentification");
    when(this.domainHelperService.findOrganisation(any())).thenReturn(new Organisation());
    doNothing().when(this.pagingSettings).updatePagingSettings(any());
    when(this.domainHelperService.findDevice(any())).thenReturn(new Device());
    doNothing().when(this.domainHelperService).isAllowed(any(), any(), any());
    when(this.eventSpecifications.isFromDevice(any())).thenReturn(this.specification);
    when(this.pagingSettings.getPageNumber()).thenReturn(1);
    when(this.pagingSettings.getPageSize()).thenReturn(1);
    when(this.eventRepository.findAll((Specification<Event>) any(), any(Pageable.class)))
        .thenReturn(null);

    when(this.eventSpecifications.hasEventTypes(any())).thenReturn(null);
    when(this.specification.and(null)).thenReturn(this.specification);

    when(this.eventSpecifications.withDescription(any())).thenReturn(this.descriptionSpecification);
    when(this.eventSpecifications.startsWithDescription(any()))
        .thenReturn(this.descriptionStartsWithSpecification);
  }
}
