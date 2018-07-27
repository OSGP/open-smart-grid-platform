/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

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

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.syncrequest.FindMessageLogsSyncRequestExecutor;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderService;
import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

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
    private ResponseDataRepository responseDataRepository;

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
                throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_SMART_METERING,
                        new Exception("The 'from' timestamp designates a time after 'until' timestamp."));
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

    public Page<Device> findAllDevices(@Identification final String organisationIdentification, final int pageNumber)
            throws FunctionalException {

        LOGGER.debug("findAllDevices called with organisation {} and pageNumber {}", organisationIdentification,
                pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        final PageRequest request = new PageRequest(pageNumber, PAGE_SIZE, Sort.Direction.DESC, "deviceIdentification");
        return this.deviceRepository.findAllAuthorized(organisation, request);
    }

    public List<Event> findEventsByCorrelationUid(final String organisationIdentification, final String correlationUid)
            throws OsgpException {

        LOGGER.info("findEventsByCorrelationUid called with organisation {}}", organisationIdentification);

        this.domainHelperService.findOrganisation(organisationIdentification);

        final ResponseData responseData = this.responseDataRepository.findByCorrelationUid(correlationUid);
        final List<Event> events = new ArrayList<>();

        final Serializable messageData = responseData.getMessageData();

        if (messageData instanceof EventMessagesResponse) {
            events.addAll(((EventMessagesResponse) messageData).getEvents());

            LOGGER.info("deleting ResponseData for correlation uid {}.", correlationUid);
            this.responseDataRepository.delete(responseData);

        } else {
            /**
             * If the returned data is not an EventMessageContainer but a
             * String, there has been an exception. The exception message has
             * been put in the messageData.
             *
             * As there is no way of knowing what the type of the exception was
             * (because it is passed as a String) it is thrown as a
             * TechnicalException because the user is most probably not to blame
             * for the exception.
             */
            if (messageData instanceof String) {
                throw new TechnicalException(ComponentType.UNKNOWN, (String) messageData);
            }
            LOGGER.info(
                    "findEventsByCorrelationUid found other type of meter response data: {} for correlation UID: {}",
                    messageData.getClass().getName(), correlationUid);
        }

        LOGGER.info("returning a list containing {} events", events.size());
        return events;
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
                organisationIdentification, correlationUid, SmartMeteringRequestMessageType.ENABLE_DEBUGGING.toString(),
                messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

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

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String findMessageLogsRequest(final String organisationIdentification, final String deviceIdentification,
            final int pageNumber) throws FunctionalException {

        LOGGER.debug("findMessageLogs called with organisation {}, device {} and pagenumber {}",
                organisationIdentification, deviceIdentification, pageNumber);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_MESSAGES);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        this.findMessageLogsSyncRequestExecutor.execute(organisationIdentification, deviceIdentification,
                correlationUid, pageNumber);

        return correlationUid;
    }

    public String enqueueSetDeviceCommunicationSettingsRequest(final String organisationIdentification,
            final String deviceIdentification,
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequest dataRequest,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DEVICE_COMMUNICATION_SETTINGS);

        LOGGER.info("SetDeviceCommunicationSettings called with organisation {}", organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_DEVICE_COMMUNICATION_SETTINGS.toString(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(dataRequest).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSetDeviceLifecycleStatusByChannelRequest(final String organisationIdentification,
            final String deviceIdentification, final SetDeviceLifecycleStatusByChannelRequestData requestData,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL);

        LOGGER.info("SetDeviceLifecycleStatusByChannel called with organisation {}", organisationIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL.toString(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(requestData).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}