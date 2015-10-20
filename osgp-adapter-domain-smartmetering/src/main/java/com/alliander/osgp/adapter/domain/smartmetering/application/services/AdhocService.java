/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeteringDevice;
import com.alliander.osgp.domain.core.repositories.SmartMeteringDeviceRepository;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringAdhocService")
@Transactional(value = "transactionManager")
public class AdhocService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private AdhocMapper adhocMapper;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private SmartMeteringDeviceRepository smartMeteringDeviceRepository;

    public AdhocService() {
        // Parameterless constructor required for transactions...
    }

    public void requestSynchronizeTime(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest synchronizeTimeRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestSynchronizeTime for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final SmartMeteringDevice device = this.smartMeteringDeviceRepository
                .findByDeviceIdentification(deviceIdentification);

        if (device == null) {
            LOGGER.info("Unknown device.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.DOMAIN_SMART_METERING);
        } else {
            LOGGER.info("Sending request message to core.");

            final SynchronizeTimeRequest synchronizeTimeRequestDto = this.adhocMapper.map(
                    synchronizeTimeRequestValueObject, SynchronizeTimeRequest.class);

            this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                    deviceIdentification, synchronizeTimeRequestDto), messageType);

        }
    }

    public void handleSynchronizeTimeresponse(final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        LOGGER.info("handleSynchronizeReadsresponse for MessageType: {}", messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

        }
        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, null), messageType);
    }
}
