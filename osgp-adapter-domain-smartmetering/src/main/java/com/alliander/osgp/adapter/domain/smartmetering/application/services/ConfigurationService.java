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

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringConfigurationService")
@Transactional(value = "transactionManager")
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    public ConfigurationService() {
        // Parameterless constructor required for transactions...
    }

    public void requestSpecialDays(
            @Identification final String organisationIdentification,
            @Identification final String deviceIdentification,
            final String correlationUid,
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest specialDaysRequestValueObject,
            final String messageType) throws FunctionalException {

        LOGGER.info("requestSSpecialDays for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.
        // Organisation organisation =
        // this.findOrganisation(organisationIdentification);
        // final Device device = this.findActiveDevice(deviceIdentification);

        // TODO deviceAuthorization
        // final DeviceAuthorization deviceAuthorization = new
        // DeviceAuthorization(duMy, organisation,
        // com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER);
        // this.deviceAuthorizationRepository.save(deviceAuthorization);

        final SpecialDaysRequest specialDaysRequestDto = this.configurationMapper.map(specialDaysRequestValueObject,
                SpecialDaysRequest.class);

        // this.osgpCoreRequestMessageSender.send(new
        // RequestMessage(correlationUid, organisationIdentification,
        // deviceIdentification, synchronizeTimeRequestDto), messageType);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, specialDaysRequestDto), messageType);

    }

    public void handleSpecialDaysResponse(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleSpecialDaysresponse for MessageType: {}", messageType);

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