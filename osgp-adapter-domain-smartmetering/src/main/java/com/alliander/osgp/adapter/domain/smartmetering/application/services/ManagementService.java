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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestList;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

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

    public void findEvents(final DeviceMessageMetadata deviceMessageMetadata,
            final FindEventsRequestDataList findEventsQueryMessageDataContainer) throws FunctionalException {

        LOGGER.info("findEvents for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService.findSmartMeter(deviceMessageMetadata
                .getDeviceIdentification());

        LOGGER.info("Sending request message to core.");
        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeter.getIpAddress(), this.managementMapper.map(findEventsQueryMessageDataContainer,
                        FindEventsRequestList.class));
        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime());
    }

    public void handleFindEventsResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException,
            final EventMessageDataResponseDto eventMessageDataContainerDto) {

        final EventMessagesResponse eventMessageDataContainer = this.managementMapper.map(
                eventMessageDataContainerDto, EventMessagesResponse.class);

        // Send the response containing the events to the webservice-adapter
        final ResponseMessage responseMessage = new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                responseMessageResultType, osgpException, eventMessageDataContainer,
                deviceMessageMetadata.getMessagePriority());
        this.webServiceResponseMessageSender.send(responseMessage, deviceMessageMetadata.getMessageType());
    }
}
