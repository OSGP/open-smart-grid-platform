/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.List;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainSmartMeteringBundleService")
@Transactional(value = "transactionManager")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
    private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

    @Autowired
    @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
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

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private FirmwareService firmwareService;

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

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
                .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
                .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
                .withResult(responseMessageResultType)
                .withOsgpException(osgpException)
                .withDataObject(bundleResponseMessageDataContainer)
                .withMessagePriority(deviceMessageMetadata.getMessagePriority())
                .build();
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
            } else if (action instanceof FirmwareVersionResponseDto) {
                final List<FirmwareVersion> firmwareVersions = this.configurationMapper
                        .mapAsList(((FirmwareVersionResponseDto) action).getFirmwareVersions(), FirmwareVersion.class);
                this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
                        deviceMessageMetadata.getDeviceIdentification(), firmwareVersions);
            }
        }
    }
}
