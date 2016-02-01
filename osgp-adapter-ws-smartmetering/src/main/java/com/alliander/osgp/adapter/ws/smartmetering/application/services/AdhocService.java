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
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SMSDetails;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.UnknownCorrelationUidException;

@Service(value = "wsSmartMeteringAdhocService")
@Validated
public class AdhocService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    public String enqueueSynchronizeTimeRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final SynchronizeTimeRequest synchronizeTimeRequest) {

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

    public String enqueueSendWakeUpSMSRequest(final String organisationIdentification, final String deviceIdentification) {

        LOGGER.debug("enqueueSendWakeUpSMSRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);
        final SMSDetails smsDetails = new SMSDetails(deviceIdentification, 0L, "", "", "");

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.SEND_WAKEUP_SMS, correlationUid, organisationIdentification,
                deviceIdentification, smsDetails);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueSendWakeUpSMSResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }

    public String enqueueGetSMSDetailsRequest(final String organisationIdentification,
            final String deviceIdentification, final SMSDetails smsDetails) {

        LOGGER.debug("enqueueGetSMSDetailsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage(
                SmartMeteringRequestMessageType.GET_SMS_DETAILS, correlationUid, organisationIdentification,
                deviceIdentification, smsDetails);

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

    public MeterResponseData dequeueGetSMSDetailsResponse(final String correlationUid)
            throws UnknownCorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid);
    }
}
