/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest.Builder;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.LmdDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.DeviceFunctionDto;
import com.alliander.osgp.dto.valueobjects.DomainTypeDto;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing public lighting get light sensor status request messages
 */
@Component
public class PublicLightingGetLightSensorStatusRequestMessageProcessor extends LmdDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetLightSensorStatusRequestMessageProcessor.class);

    protected PublicLightingGetLightSensorStatusRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_LIGHT_SENSOR_STATUS);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.alliander.osgp.shared.infra.jms.MessageProcessor#processMessage(javax
     * .jms.ObjectMessage)
     */
    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting get status request message");

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
            isScheduled = message.propertyExists(Constants.IS_SCHEDULED)
                    ? message.getBooleanProperty(Constants.IS_SCHEDULED)
                    : false;
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

        this.printDomainInfo(messageType, domain, domainVersion);

        // To ensure the response is sent to the public lighting or common
        // domain and can be parsed using the existing GetStatus functions, the
        // message type is changed from GET_LIGHT_SENSOR_STATUS to
        // GET_LIGHT_STATUS or to GET_STATUS, depending on domain.
        if (DomainTypeDto.PUBLIC_LIGHTING.name().equals(domain)) {
            messageType = DeviceFunctionDto.GET_LIGHT_STATUS.name();
        } else if (DomainTypeDto.TARIFF_SWITCHING.name().equals(domain)) {
            LOGGER.warn("Unexpected domain request received: {} for messageType: {}", domain, messageType);
        } else {
            messageType = DeviceFunctionDto.GET_STATUS.name();
        }

        final RequestMessageData requestMessageData = RequestMessageData.newRequestMessageDataBuilder()
                .withDomain(domain).withDomainVersion(domainVersion).withMessageType(messageType)
                .withRetryCount(retryCount).withIsScheduled(isScheduled).withCorrelationUid(correlationUid)
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).build();

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final Builder deviceRequest = DeviceRequest.newDeviceRequestBuilder()
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withCorrelationUid(correlationUid).withDomain(domain)
                .withDomainVersion(domainVersion).withMessageType(messageType).withIpAddress(ipAddress)
                .withRetryCount(retryCount).withIsScheduled(isScheduled);

        this.deviceService.getStatus(new DeviceRequest(deviceRequest), iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final com.alliander.osgp.shared.infra.jms.ResponseMessageSender responseMessageSender, final String domain,
            final String domainVersion, final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by PublicLightingGetLightSensorStatusRequestMessageProcessor");
        this.handleGetStatusDeviceResponse(deviceResponse, responseMessageSender, domain, domainVersion, messageType,
                retryCount);
    }
}
