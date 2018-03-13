/**
 * Copyright 2016 Smart Society Services B.V.
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

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainSmartMeteringBundleService")
@Transactional(value = "transactionManager")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutgoingOsgpCoreRequestMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    private WebServiceResponseMessageSender webServiceResponseMessageSender;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private ActionMapperService actionMapperService;

    @Autowired
    private ActionMapperResponseService actionMapperResponseService;

    @Autowired
    private MBusGatewayService mBusGatewayService;

    @Autowired
    private ManagementService managementService;

    public BundleService() {
        // Parameterless constructor required for transactions...
    }

    public void handleBundle(final DeviceMessageMetadata deviceMessageMetadata,
            final BundleMessageRequest bundleMessageDataContainer) throws FunctionalException {

        LOGGER.info("handleBundle for organisationIdentification: {} for deviceIdentification: {}",
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification());

        final SmartMeter smartMeter = this.domainHelperService
                .findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        final BundleMessagesRequestDto bundleMessageDataContainerDto = this.actionMapperService
                .mapAllActions(bundleMessageDataContainer, smartMeter);

        LOGGER.info("Sending request message to core.");
        final RequestMessage requestMessage = new RequestMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                smartMeter.getIpAddress(), bundleMessageDataContainerDto);
        this.osgpCoreRequestMessageSender.send(requestMessage, deviceMessageMetadata.getMessageType(),
                deviceMessageMetadata.getMessagePriority(), deviceMessageMetadata.getScheduleTime(),
                deviceMessageMetadata.bypassRetry());
    }

    public void handleBundleResponse(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessageResultType responseMessageResultType, final OsgpException osgpException,
            final BundleMessagesRequestDto bundleResponseMessageDataContainerDto) throws FunctionalException {

        this.checkIfAdditionalActionIsNeeded(deviceMessageMetadata, bundleResponseMessageDataContainerDto);

        // convert bundleResponseMessageDataContainerDto back to core object
        final BundleMessagesResponse bundleResponseMessageDataContainer = this.actionMapperResponseService
                .mapAllActions(bundleResponseMessageDataContainerDto);

        // Send the response final containing the events final to the
        // webservice-adapter
        final ResponseMessage responseMessage = new ResponseMessage(deviceMessageMetadata.getCorrelationUid(),
                deviceMessageMetadata.getOrganisationIdentification(), deviceMessageMetadata.getDeviceIdentification(),
                responseMessageResultType, osgpException, bundleResponseMessageDataContainer,
                deviceMessageMetadata.getMessagePriority());
        this.webServiceResponseMessageSender.send(responseMessage, deviceMessageMetadata.getMessageType());
    }

    private void checkIfAdditionalActionIsNeeded(final DeviceMessageMetadata deviceMessageMetadata,
            final BundleMessagesRequestDto bundleResponseMessageDataContainerDto) throws FunctionalException {

        for (final ActionResponseDto action : bundleResponseMessageDataContainerDto.getAllResponses()) {
            if (action instanceof CoupleMbusDeviceByChannelResponseDto) {
                this.mBusGatewayService.handleCoupleMbusDeviceByChannelResponse(deviceMessageMetadata,
                        (CoupleMbusDeviceByChannelResponseDto) action);
            } else if (action instanceof SetDeviceLifecycleStatusByChannelResponseDto) {
                this.managementService
                        .setDeviceLifecycleStatusByChannel((SetDeviceLifecycleStatusByChannelResponseDto) action);
            }
        }
    }
}
