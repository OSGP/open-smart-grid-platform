/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.domain.model.protocol.ProtocolResponseService;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareFileRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.FirmwareFileDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component("getFirmwareFileMessageProcessor")
public class GetFirmwareFileMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareFileMessageProcessor.class);

    @Autowired
    private ProtocolResponseService protocolResponseMessageSender;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

    protected GetFirmwareFileMessageProcessor() {
        super(DeviceFunction.GET_FIRMWARE_FILE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {

        MessageMetadata metadata = null;
        Device device = null;
        String firmwareFileIdentification = StringUtils.EMPTY;

        try {
            metadata = MessageMetadata.fromMessage(message);
            LOGGER.info(
                    "[{}] - Received message of messageType: {}, organisationIdentification: {}, deviceIdentification: {}",
                    metadata.getCorrelationUid(), metadata.getMessageType(), metadata.getOrganisationIdentification(),
                    metadata.getDeviceIdentification());

            device = this.deviceRepository.findByDeviceIdentification(metadata.getDeviceIdentification());

            final RequestMessage requestMessage = (RequestMessage) message.getObject();
            firmwareFileIdentification = (String) requestMessage.getRequest();

            final FirmwareFile firmwareFile = this.firmwareFileRepository
                    .findByIdentification(firmwareFileIdentification);

            final FirmwareFileDto firmwareFileDto = new FirmwareFileDto(firmwareFile.getFilename(),
                    firmwareFile.getFile());

            this.sendSuccesResponse(metadata, device.getProtocolInfo(), firmwareFileDto);

        } catch (final Exception e) {
            LOGGER.error("Exception while retrieving firmware file: {}", firmwareFileIdentification);
            final OsgpException osgpException = new OsgpException(ComponentType.OSGP_CORE,
                    "Exception while retrieving firmware file.", e);
            this.sendFailureResponse(metadata, device.getProtocolInfo(), osgpException);
        }
    }

    private void sendSuccesResponse(final MessageMetadata metadata, final ProtocolInfo protocolInfo,
            final FirmwareFileDto firmwareFileDto) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(new DeviceMessageMetadata(metadata)).domain(metadata.getDomain())
                .domainVersion(metadata.getDomainVersion()).result(ResponseMessageResultType.OK).osgpException(null)
                .dataObject(firmwareFileDto).retryCount(metadata.getRetryCount()).scheduled(metadata.isScheduled())
                .build();

        this.protocolResponseMessageSender.send(responseMessage, DeviceFunction.GET_FIRMWARE_FILE.name(), protocolInfo,
                metadata);
    }

    private void sendFailureResponse(final MessageMetadata metadata, final ProtocolInfo protocolInfo,
            final OsgpException exception) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage.Builder()
                .deviceMessageMetadata(new DeviceMessageMetadata(metadata)).domain(metadata.getDomain())
                .domainVersion(metadata.getDomainVersion()).result(ResponseMessageResultType.NOT_OK)
                .osgpException(exception).dataObject(null).retryCount(metadata.getRetryCount())
                .scheduled(metadata.isScheduled()).build();

        this.protocolResponseMessageSender.send(responseMessage, DeviceFunction.GET_FIRMWARE_FILE.name(), protocolInfo,
                metadata);
    }

}
