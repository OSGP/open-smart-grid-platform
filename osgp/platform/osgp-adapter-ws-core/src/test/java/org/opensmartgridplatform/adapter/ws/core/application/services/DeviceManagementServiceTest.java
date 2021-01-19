/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
    @Mock
    private SearchEventsCriteria criteria;
    @InjectMocks
    private DeviceManagementService deviceManagementService;
    @Mock
    private DomainHelperService domainHelperService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private JpaEventSpecifications eventSpecifications;
    @Mock
    private PagingSettings pagingSettings;
    @Mock
    private Specification<Event> specification;
    @Mock
    private Specification<Event> descriptionSpecification;
    @Mock
    private Specification<Event> descriptionStartsWithSpecification;

    @Test
    void findEventsTest() throws FunctionalException {
        when(this.criteria.getOrganisationIdentification()).thenReturn("orgIdentification");
        when(this.criteria.getDeviceIdentification()).thenReturn("deviceIdentification");
        when(this.criteria.getDescriptionStartsWith()).thenReturn("descriptionStartValue");
        when(this.criteria.getDescription()).thenReturn("description");
        when(this.domainHelperService.findOrganisation(any())).thenReturn(new Organisation());
        doNothing().when(this.pagingSettings).updatePagingSettings(any());
        when(this.domainHelperService.findDevice(any())).thenReturn(new Device());
        doNothing().when(this.domainHelperService).isAllowed(any(), any(), any());
        when(this.eventSpecifications.isFromDevice(any())).thenReturn(this.specification);

        when(this.eventSpecifications.withDescription(any())).thenReturn(this.descriptionSpecification);
        when(this.eventSpecifications.startsWithDescription(any())).thenReturn(this.descriptionStartsWithSpecification);
        when(this.descriptionSpecification.or(this.descriptionStartsWithSpecification)).thenReturn(this.specification);

        when(this.pagingSettings.getPageNumber()).thenReturn(1);
        when(this.pagingSettings.getPageSize()).thenReturn(1);
        when(this.eventRepository.findAll((Specification<Event>) any(), any(Pageable.class))).thenReturn(null);
        when(this.specification.and(any())).thenReturn(this.specification);
        //when(this.descriptionSpecification.or(any())).thenReturn(this.descriptionSpecification);

        this.deviceManagementService.findEvents(this.criteria);

        verify(this.descriptionSpecification, times(1)).or(this.descriptionStartsWithSpecification);
        verify(this.specification, times(1)).and(this.specification);
    }

    @Test
    void findEventsWithNoDescriptionStartsWithTest() throws FunctionalException {
        when(this.criteria.getOrganisationIdentification()).thenReturn("orgIdentification");
        when(this.criteria.getDeviceIdentification()).thenReturn("deviceIdentification");
        when(this.domainHelperService.findOrganisation(any())).thenReturn(new Organisation());
        doNothing().when(this.pagingSettings).updatePagingSettings(any());
        when(this.domainHelperService.findDevice(any())).thenReturn(new Device());
        doNothing().when(this.domainHelperService).isAllowed(any(), any(), any());
        when(this.eventSpecifications.isFromDevice(any())).thenReturn(this.getSpecification());
        when(this.pagingSettings.getPageNumber()).thenReturn(1);
        when(this.pagingSettings.getPageSize()).thenReturn(1);
        when(this.eventRepository.findAll((Specification<Event>) any(), any(Pageable.class))).thenReturn(null);

        when(this.eventSpecifications.withDescription(any())).thenReturn(this.descriptionSpecification);
        when(this.eventSpecifications.startsWithDescription(any())).thenReturn(this.descriptionStartsWithSpecification);
        //when(this.descriptionSpecification.or(this.descriptionStartsWithSpecification)).thenReturn(this
        // .specification);

        this.deviceManagementService.findEvents(this.criteria);

        verify(this.specification, never()).and(this.descriptionStartsWithSpecification);
        verify(this.specification, never()).and(this.descriptionSpecification);
        verify(this.specification, never()).and(this.specification);
    }

    @Test
    void findEventsWithNoDescriptionTest() throws FunctionalException {
        when(this.criteria.getOrganisationIdentification()).thenReturn("orgIdentification");
        when(this.criteria.getDeviceIdentification()).thenReturn("deviceIdentification");
        when(this.criteria.getDescriptionStartsWith()).thenReturn("descriptionStartValue");
        when(this.domainHelperService.findOrganisation(any())).thenReturn(new Organisation());
        doNothing().when(this.pagingSettings).updatePagingSettings(any());
        when(this.domainHelperService.findDevice(any())).thenReturn(new Device());
        doNothing().when(this.domainHelperService).isAllowed(any(), any(), any());
        when(this.eventSpecifications.isFromDevice(any())).thenReturn(this.specification);
        when(this.pagingSettings.getPageNumber()).thenReturn(1);
        when(this.pagingSettings.getPageSize()).thenReturn(1);
        when(this.eventRepository.findAll((Specification<Event>) any(), any(Pageable.class))).thenReturn(null);

        when(this.eventSpecifications.hasEventTypes(any())).thenReturn(null);
        when(this.specification.and(null)).thenReturn(this.specification);

        when(this.eventSpecifications.withDescription(any())).thenReturn(this.descriptionSpecification);
        when(this.eventSpecifications.startsWithDescription(any())).thenReturn(this.descriptionStartsWithSpecification);
        //when(this.descriptionSpecification.or(this.descriptionStartsWithSpecification)).thenReturn(this
        // .specification);

        this.deviceManagementService.findEvents(this.criteria);

        verify(this.specification, times(1)).and(this.descriptionStartsWithSpecification);
        verify(this.specification, never()).and(this.descriptionSpecification);
        verify(this.descriptionSpecification, never()).or(this.descriptionStartsWithSpecification);
    }

    private Specification<Event> getSpecification(){
        return new Specification<Event>() {
            private static final long serialVersionUID = 2946693984484298490L;

            @Override
            public Predicate toPredicate(final Root<Event> root, final CriteriaQuery<?> criteriaQuery,
                    final CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };
    }
}
