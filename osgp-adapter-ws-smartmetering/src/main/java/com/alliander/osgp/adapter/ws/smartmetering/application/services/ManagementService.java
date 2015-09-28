/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.FindEventsQuery;
import com.alliander.osgp.domain.core.valueobjects.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "wsSmartMeteringManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ManagementService {

    private static final int PAGE_SIZE = 30;

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventSpecifications eventSpecifications;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    public ManagementService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueFindEventsRequest(final String organisationIdentification, final String deviceIdentification,
            final List<FindEventsQuery> findEventsQuery) throws FunctionalException {

        LOGGER.info("findEvents called with organisation {}", organisationIdentification);

        this.domainHelperService.findOrganisation(organisationIdentification);
        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        // for (final FindEventsQuery query : findEventsQuery) {
        // final DateTime from = query.getFrom() == null ? null : new
        // DateTime(query.getFrom().toGregorianCalendar())
        // .toDateTime(DateTimeZone.UTC);
        // final DateTime until = query.getUntil() == null ? null : new
        // DateTime(query.getUntil()
        // .toGregorianCalendar()).toDateTime(DateTimeZone.UTC);
        // final EventLogCategory eventLogCategory =
        // query.getEventLogCategory();
        //
        // LOGGER.info("looping through FindEventsQuery, from: {}, until: {}, EventLogCategory: {}",
        // from, until,
        // eventLogCategory);
        // }

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.FIND_EVENTS, correlationUid, organisationIdentification,
                deviceIdentification, new FindEventsQueryMessageDataContainer(findEventsQuery));
        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public List<Event> findEventsByCorrelationUid(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid) throws FunctionalException {

        LOGGER.info("findEventsByCorrelationUid called with organisation {}}", organisationIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        return null;
    }

    // THIS FUNCTION SHOULD FIND THE TEMPORARY STORED EVENTS
    // A RESPONSE QUEUE MESSAGE SHOULD BE DEQUEUED, AND THE RESULTING EVENTS
    // WILL BE STORED
    public List<Event> findEvents(@Identification final String organisationIdentification,
            final String deviceIdentification, final Integer pageSize, final Integer pageNumber, final DateTime from,
            final DateTime until) throws FunctionalException {

        LOGGER.debug("findEvents called for organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        Specifications<Event> specifications = null;

        try {
            if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
                final Device device = this.domainHelperService.findDevice(deviceIdentification);
                this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_EVENT_NOTIFICATIONS);

                specifications = where(this.eventSpecifications.isFromDevice(device));
            } else {
                specifications = where(this.eventSpecifications.isAuthorized(organisation));
            }

            if (from != null) {
                specifications = specifications.and(this.eventSpecifications.isCreatedAfter(from.toDate()));
            }

            if (until != null) {
                specifications = specifications.and(this.eventSpecifications.isCreatedBefore(until.toDate()));
            }
        } catch (final ArgumentNullOrEmptyException e) {
            throw new FunctionalException(FunctionalExceptionType.ARGUMENT_NULL, ComponentType.WS_CORE, e);
        }

        return this.eventRepository.findAll(specifications);
    }

    public Page<Device> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {

        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final PageRequest request = new PageRequest(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
        return this.deviceRepository.findAllAuthorized(organisation, request);
    }
}