/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * @author OSGP
 *
 */
@Service(value = "domainSmartMeteringManagementService")
@Transactional(value = "transactionManager")
public class ManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private ManagementMapper managementMapper;

    public ManagementService() {
        // Parameterless constructor required for transactions...
    }

    public void findEvents(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid, final String messageType,
            final FindEventsQueryMessageDataContainer findEventsQueryMessageDataContainer) throws FunctionalException {

        LOGGER.info("findEvents for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        // TODO: bypassing authorization, this should be fixed.

        final SmartMeter smartMeteringDevice = this.domainHelperService
                .findSmartMeteringDevice(deviceIdentification);

        LOGGER.info("Sending request message to core.");
        final RequestMessage requestMessage = new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, smartMeteringDevice.getIpAddress(), this.managementMapper.map(
                        findEventsQueryMessageDataContainer,
                        com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer.class));
        this.osgpCoreRequestMessageSender.send(requestMessage, messageType);
    }

    public void handleFindEventsResponse(
            final String deviceIdentification,
            final String organisationIdentification,
            final String correlationUid,
            final String messageType,
            final ResponseMessageResultType responseMessageResultType,
            final OsgpException osgpException,
            final com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainer eventMessageDataContainerDto) {

        final EventMessageDataContainer eventMessageDataContainer = this.managementMapper.map(
                eventMessageDataContainerDto, EventMessageDataContainer.class);

        // Send the response containing the events to the webservice-adapter
        final ResponseMessage responseMessage = new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, responseMessageResultType, osgpException, eventMessageDataContainer);
        this.webServiceResponseMessageSender.send(responseMessage, messageType);
    }
}
