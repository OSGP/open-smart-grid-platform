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

import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolResponseMessageSender;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.FirmwareFileDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component("getFirmwareFileMessageProcessor")
public class GetFirmwareFileMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareFileMessageProcessor.class);

    @Autowired
    private ProtocolResponseMessageSender protocolResponseMessageSender;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private FirmwareRepository firmwareRepository;

    protected GetFirmwareFileMessageProcessor() {
        super(DeviceFunction.GET_FIRMWARE_FILE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {

        final DeviceMessageMetadata metadata = new DeviceMessageMetadata(message);

        LOGGER.info(
                "[{}] - Received message of messageType: {}, organisationIdentification: {}, deviceIdentification: {}",
                metadata.getCorrelationUid(), metadata.getMessageType(), metadata.getOrganisationIdentification(),
                metadata.getDeviceIdentification());

        final Device device = this.deviceRepository.findByDeviceIdentification(metadata.getDeviceIdentification());

        String firmwareIdentification = StringUtils.EMPTY;

        try {
            firmwareIdentification = (String) message.getObject();

            // TODO - SLIM-748 - Replace by findByIdentification once implemented and merged
            final Firmware firmware = this.firmwareRepository.findByFilename(firmwareIdentification);

            final FirmwareFileDto firmwareFileDto = new FirmwareFileDto(firmware.getFilename(), firmware.getFile());

            this.sendSuccesResponse(metadata, device.getProtocolInfo(), firmwareFileDto);

        } catch (final Exception e) {
            LOGGER.error("[{}] - Exception while retrieving firmware file: {}", metadata.getCorrelationUid(),
                    firmwareIdentification);
            final OsgpException osgpException = new OsgpException(ComponentType.OSGP_CORE,
                    "Exception while retrieving firmware file.", e);
            this.sendFailureResponse(metadata, device.getProtocolInfo(), osgpException);
        }
    }

    private void sendSuccesResponse(final DeviceMessageMetadata metadata, final ProtocolInfo protocolInfo,
            final FirmwareFileDto firmwareFileDto) {

        final ResponseMessage responseMessage = new ResponseMessage(metadata.getCorrelationUid(),
                metadata.getOrganisationIdentification(), metadata.getDeviceIdentification(),
                ResponseMessageResultType.OK, null, firmwareFileDto, metadata.getMessagePriority(),
                metadata.bypassRetry());

        this.protocolResponseMessageSender.send(responseMessage, DeviceFunction.GET_FIRMWARE_FILE.name(), protocolInfo);
    }

    private void sendFailureResponse(final DeviceMessageMetadata metadata, final ProtocolInfo protocolInfo,
            final OsgpException exception) {

        final ResponseMessage responseMessage = new ResponseMessage(metadata.getCorrelationUid(),
                metadata.getOrganisationIdentification(), metadata.getDeviceIdentification(),
                ResponseMessageResultType.NOT_OK, exception, null, metadata.getMessagePriority(),
                metadata.bypassRetry());

        this.protocolResponseMessageSender.send(responseMessage, DeviceFunction.GET_FIRMWARE_FILE.name(), protocolInfo);
    }

}
