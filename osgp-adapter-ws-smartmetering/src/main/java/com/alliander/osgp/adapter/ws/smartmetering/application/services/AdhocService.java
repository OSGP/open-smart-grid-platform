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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;

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
            @Identification final String deviceIdentification, final SynchronizeTimeRequest synchronizeTimeRequest)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SYNCHRONIZE_TIME);

        LOGGER.debug("enqueueSynchronizeTimeRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SYNCHRONIZE_TIME, correlationUid, organisationIdentification,
                deviceIdentification, synchronizeTimeRequest);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSynchronizeTimeResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueSendWakeUpSmsRequest(final String organisationIdentification, final String deviceIdentification)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SEND_WAKEUP_SMS);

        LOGGER.debug("enqueueSendWakeUpSmsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final SmsDetails smsDetails = new SmsDetails(deviceIdentification, 0L, "", "", "");

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SEND_WAKEUP_SMS, correlationUid, organisationIdentification,
                deviceIdentification, smsDetails);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSendWakeUpSmsResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueGetSmsDetailsRequest(final String organisationIdentification,
            final String deviceIdentification, final SmsDetails smsDetails) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_SMS_DETAILS);

        LOGGER.debug("enqueueGetSmsDetailsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.GET_SMS_DETAILS, correlationUid, organisationIdentification,
                deviceIdentification, smsDetails);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueGetSmsDetailsResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueRetrieveConfigurationObjectsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final RetrieveConfigurationObjectsRequest request)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_CONFIGURATION_OBJECTS);

        LOGGER.debug("enqueueRetrieveConfigurationObjectsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.GET_CONFIGURATION_OBJECTS, correlationUid, organisationIdentification,
                deviceIdentification, request);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueRetrieveConfigurationObjectsResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);

    }
}
