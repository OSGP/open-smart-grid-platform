/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.oslp.elster.device.responses.GetFirmwareVersionDeviceResponse;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import com.alliander.osgp.dto.valueobjects.FirmwareModuleType;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;
import com.alliander.osgp.oslp.UnsignedOslpEnvelopeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

/**
 * Class for processing common get firmware request messages
 */
@Component("oslpCommonGetFirmwareRequestMessageProcessor")
public class CommonGetFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor implements
OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonGetFirmwareRequestMessageProcessor.class);

    public CommonGetFirmwareRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_FIRMWARE_VERSION);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common get firmware request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        int retryCount = 0;
        boolean isScheduled = false;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
            isScheduled = message.propertyExists(Constants.IS_SCHEDULED) ? message
                    .getBooleanProperty(Constants.IS_SCHEDULED) : false;
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            return;
        }

        LOGGER.info("Calling DeviceService function: {} for domain: {} {}", messageType, domain, domainVersion);

        final DeviceRequest deviceRequest = new DeviceRequest(organisationIdentification, deviceIdentification,
                correlationUid, domain, domainVersion, messageType, ipAddress, retryCount, isScheduled);

        this.deviceService.getFirmwareVersion(deviceRequest);
    }

    @Override
    public void processSignedOslpEnvelope(final String deviceIdentification,
            final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        final UnsignedOslpEnvelopeDto unsignedOslpEnvelopeDto = signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto();
        final OslpEnvelope oslpEnvelope = signedOslpEnvelopeDto.getOslpEnvelope();
        final String correlationUid = unsignedOslpEnvelopeDto.getCorrelationUid();
        final String organisationIdentification = unsignedOslpEnvelopeDto.getOrganisationIdentification();
        final String domain = unsignedOslpEnvelopeDto.getDomain();
        final String domainVersion = unsignedOslpEnvelopeDto.getDomainVersion();
        final String messageType = unsignedOslpEnvelopeDto.getMessageType();
        final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
        final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
        final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                CommonGetFirmwareRequestMessageProcessor.this.handleGetFirmwareVersionDeviceResponse(deviceResponse,
                        CommonGetFirmwareRequestMessageProcessor.this.responseMessageSender, domain, domainVersion,
                        messageType, retryCount);
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                CommonGetFirmwareRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse, t,
                        null, CommonGetFirmwareRequestMessageProcessor.this.responseMessageSender, deviceResponse,
                        domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        final DeviceRequest deviceRequest = new DeviceRequest(organisationIdentification, deviceIdentification,
                correlationUid);

        try {
            this.deviceService.doGetFirmwareVersion(oslpEnvelope, deviceRequest, deviceResponseHandler, ipAddress);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain,
                    domainVersion, messageType, retryCount);
        }
    }

    private void handleGetFirmwareVersionDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        String firmwareVersion = "";
        OsgpException osgpException = null;

        final List<FirmwareVersionDto> firmwareVersions = new ArrayList<>();
        try {
            final GetFirmwareVersionDeviceResponse response = (GetFirmwareVersionDeviceResponse) deviceResponse;
            firmwareVersion = response.getFirmwareVersion();

            firmwareVersions.add(new FirmwareVersionDto(FirmwareModuleType.FUNCTIONAL, firmwareVersion));
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while getting device firmware version", e);
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, MessagePriorityEnum.DEFAULT.getPriority());
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(osgpException).dataObject((Serializable) firmwareVersions).retryCount(retryCount)
                .build();

        responseMessageSender.send(responseMessage);
    }
}
