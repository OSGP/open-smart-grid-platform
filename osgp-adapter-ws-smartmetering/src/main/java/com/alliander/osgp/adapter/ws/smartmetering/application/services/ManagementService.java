/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.application.syncrequest.FindMessageLogsSyncRequestExecutor;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

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
    private MeterResponseDataService meterResponseDataService;

    @Autowired
    private MeterResponseDataRepository meterResponseDataRepository;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    @Autowired
    private FindMessageLogsSyncRequestExecutor findMessageLogsSyncRequestExecutor;

    public ManagementService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueFindEventsRequest(final String organisationIdentification, final String deviceIdentification,
            final List<FindEventsRequestData> findEventsQueryList, final int messagePriority, final Long scheduleTime)
                    throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.FIND_EVENTS);

        LOGGER.info("findEvents called with organisation {}", organisationIdentification);

        for (final FindEventsRequestData findEventsQuery : findEventsQueryList) {
            if (!findEventsQuery.getFrom().isBefore(findEventsQuery.getUntil())) {
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                        ComponentType.WS_SMART_METERING, new Exception(
                                "The 'from' timestamp designates a time after 'until' timestamp."));
            }
        }
        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, SmartMeteringRequestMessageType.FIND_EVENTS.toString(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata)
        .request(new FindEventsRequestDataList(findEventsQueryList)).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public List<Event> findEventsByCorrelationUid(final String organisationIdentification, final String correlationUid)
            throws OsgpException {

        LOGGER.info("findEventsByCorrelationUid called with organisation {}}", organisationIdentification);

        this.domainHelperService.findOrganisation(organisationIdentification);

        final List<MeterResponseData> meterResponseDataList = this.meterResponseDataRepository
                .findByCorrelationUid(correlationUid);
        final List<Event> events = new ArrayList<>();
        final List<MeterResponseData> meterResponseDataToDeleteList = new ArrayList<>();

        for (final MeterResponseData meterResponseData : meterResponseDataList) {
            final Serializable messageData = meterResponseData.getMessageData();

            if (messageData instanceof EventMessagesResponse) {
                events.addAll(((EventMessagesResponse) messageData).getEvents());
                meterResponseDataToDeleteList.add(meterResponseData);
            } else {
                /**
                 * If the returned data is not an EventMessageContainer but a
                 * String, there has been an exception. The exception message
                 * has been put in the messageData.
                 *
                 * As there is no way of knowing what the type of the exception
                 * was (because it is passed as a String) it is thrown as a
                 * TechnicalException because the user is most probably not to
                 * blame for the exception.
                 */
                if (messageData instanceof String) {
                    throw new TechnicalException(ComponentType.UNKNOWN, (String) messageData);
                }
                LOGGER.info(
                        "findEventsByCorrelationUid also found other type of meter response data: {} for correlation UID: {}",
                        messageData.getClass().getName(), correlationUid);
            }
        }

        LOGGER.info("deleting {} MeterResponseData rows", meterResponseDataToDeleteList.size());
        this.meterResponseDataRepository.delete(meterResponseDataToDeleteList);

        LOGGER.info("returning a list containing {} events", events.size());
        return events;
    }

    public Page<Device> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {

        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final PageRequest request = new PageRequest(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
        return this.deviceRepository.findAllAuthorized(organisation, request);
    }

    public String enqueueEnableDebuggingRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.ENABLE_DEBUGGING);

        LOGGER.info("EnableDebugging called with organisation {}", organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.ENABLE_DEBUGGING.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(
                deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueDisableDebuggingRequest(final String organisationIdentification,
            final String deviceIdentification, final int messagePriority, final Long scheduleTime)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.DISABLE_DEBUGGING);

        LOGGER.info("DisableDebugging called with organisation {}", organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.DISABLE_DEBUGGING.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder().deviceMessageMetadata(
                deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueEnableDebuggingResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public MeterResponseData dequeueDisableDebuggingResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String findMessageLogsRequest(final String organisationIdentification, final String deviceIdentification,
            final int pageNumber) throws FunctionalException {

        LOGGER.debug("findMessageLogs called with organisation {}, device {} and pagenumber {}", new Object[] {
                organisationIdentification, deviceIdentification, pageNumber });

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_MESSAGES);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        this.findMessageLogsSyncRequestExecutor.execute(organisationIdentification, deviceIdentification,
                correlationUid, pageNumber);

        return correlationUid;
    }

    public MeterResponseData dequeueFindMessageLogsResponse(final String correlationUid) throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, Page.class);
    }
}
