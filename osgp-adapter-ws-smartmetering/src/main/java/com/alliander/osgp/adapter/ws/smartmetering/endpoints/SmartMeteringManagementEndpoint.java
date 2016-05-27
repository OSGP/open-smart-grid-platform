/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import java.util.List;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.ScheduleTime;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.DevicePage;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.GetDevicesResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.ManagementService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.

@SuppressWarnings("deprecation")
@Endpoint
public class SmartMeteringManagementEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringManagementEndpoint.class);
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-management/2014/10";
    private static final ComponentType COMPONENT_WS_SMART_METERING = ComponentType.WS_SMART_METERING;

    private final ManagementService managementService;
    private final ManagementMapper managementMapper;

    @Autowired
    public SmartMeteringManagementEndpoint(
            @Qualifier(value = "wsSmartMeteringManagementService") final ManagementService managementService,
            @Qualifier(value = "smartMeteringManagementMapper") final ManagementMapper managementMapper) {
        this.managementService = managementService;
        this.managementMapper = managementMapper;
    }

    @PayloadRoot(localPart = "FindEventsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindEventsAsyncResponse findEventsRequest(
            @OrganisationIdentification final String organisationIdentification,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @RequestPayload final FindEventsRequest request) throws OsgpException {

        LOGGER.info("Find events request for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindEventsAsyncResponse response = null;
        try {
            // Create response.
            response = new FindEventsAsyncResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String deviceIdentification = request.getDeviceIdentification();
            final List<FindEventsRequestData> findEventsQuery = request.getFindEventsRequestData();

            final String correlationUid = this.managementService.enqueueFindEventsRequest(organisationIdentification,
                    deviceIdentification, this.managementMapper.mapAsList(findEventsQuery,
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestData.class),
                    MessagePriorityEnum.getMessagePriority(messagePriority), this.managementMapper.map(scheduleTime,
                            Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "FindEventsAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public FindEventsResponse getFindEventsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final FindEventsAsyncRequest request) throws OsgpException {

        LOGGER.info("Get find events response for organisation: {} and device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        FindEventsResponse response = null;
        try {
            // Create response.
            response = new FindEventsResponse();

            // Get the request parameters, make sure that date time are in UTC.
            final String correlationUid = request.getCorrelationUid();

            final List<Event> events = this.managementService.findEventsByCorrelationUid(organisationIdentification,
                    correlationUid);

            LOGGER.info("getFindEventsResponse() number of events: {}", events.size());
            for (final Event event : events) {
                LOGGER.info("event.eventCode: {} event.timestamp: {} event.eventCounter: {}", event.getEventCode(),
                        event.getTimestamp(), event.getEventCounter());
            }
            LOGGER.info("mapping events to schema type...");
            response.getEvents().addAll(
                    this.managementMapper.mapAsList(events,
                            com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class));
            LOGGER.info("mapping done, sending response...");
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("FindEventsRequest Exception", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetDevicesResponse getDevices(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDevicesRequest request) throws OsgpException {

        LOGGER.info("Get Devices Request received from organisation: {}.", organisationIdentification);

        GetDevicesResponse response = null;
        try {
            response = new GetDevicesResponse();
            final Page<Device> page = this.managementService.findAllDevices(organisationIdentification,
                    request.getPage());

            final DevicePage devicePage = new DevicePage();
            devicePage.setTotalPages(page.getTotalPages());
            devicePage.getDevices().addAll(
                    this.managementMapper.mapAsList(page.getContent(),
                            com.alliander.osgp.adapter.ws.schema.smartmetering.management.Device.class));
            response.setDevicePage(devicePage);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, COMPONENT_WS_SMART_METERING,
                    new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
