/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAssociationLnObjectsRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

@Service(value = "wsSmartMeteringAdhocService")
@Validated
public class AdhocService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    public String enqueueSynchronizeTimeRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SynchronizeTimeRequestData utcOffset, final int messagePriority,
            final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SYNCHRONIZE_TIME);

        LOGGER.debug("enqueueSynchronizeTimeRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.SYNCHRONIZE_TIME.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata).request(utcOffset).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSynchronizeTimeResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueRetrieveConfigurationObjectsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final RetrieveConfigurationObjectsRequest request,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_CONFIGURATION_OBJECTS);

        LOGGER.debug("enqueueRetrieveConfigurationObjectsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_CONFIGURATION_OBJECTS.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata).request(request).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueResponse(final String correlationUid) throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);

    }

    public String enqueueGetAssociationLnObjectsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final GetAssociationLnObjectsRequest request,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_ASSOCIATION_LN_OBJECTS);

        LOGGER.debug("enqueueGetAssociationLnObjectsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_ASSOCIATION_LN_OBJECTS.toString(), messagePriority, scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata).request(request).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public String enqueueSpecificConfigurationObjectRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificConfigurationObjectRequest request,
            final int messagePriority, final Long scheduleTime) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_SPECIFIC_CONFIGURATION_OBJECT);

        LOGGER.debug("enqueueSpecificConfigurationObjectRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.GET_SPECIFIC_CONFIGURATION_OBJECT.toString(), messagePriority,
                scheduleTime);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata).request(request).build();

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }
}
