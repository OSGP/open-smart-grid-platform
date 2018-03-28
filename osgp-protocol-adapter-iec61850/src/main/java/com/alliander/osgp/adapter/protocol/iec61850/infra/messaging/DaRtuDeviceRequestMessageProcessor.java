/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest.Builder;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu.DaDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu.DaRtuDeviceService;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.Function;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DaRtuDeviceRequestMessageProcessor extends BaseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaRtuDeviceRequestMessageProcessor.class);

    @Autowired
    protected DaRtuDeviceService deviceService;

    /**
     * Each MessageProcessor should register it's MessageType at construction.
     *
     * @param deviceRequestMessageType
     *            The MessageType the MessageProcessor implementation can process.
     */
    protected DaRtuDeviceRequestMessageProcessor(final DeviceRequestMessageType deviceRequestMessageType) {
        this.deviceRequestMessageType = deviceRequestMessageType;
    }

    /**
     * Generic function to get the data from the rtu based on the device connection
     * details and the deviceRequest. Must be implemented in each concrete
     * MessageProcessor
     *
     */
    public abstract <T> Function<T> getDataFunction(Iec61850Client iec61850Client, DeviceConnection connection,
            DaDeviceRequest deviceRequest);

    /**
     * Initialization function executed after dependency injection has finished. The
     * MessageProcessor Singleton is added to the HashMap of MessageProcessors. The
     * key for the HashMap is the integer value of the enumeration member.
     */
    @PostConstruct
    public void init() {
        this.iec61850RequestMessageProcessorMap.addMessageProcessor(this.deviceRequestMessageType.ordinal(),
                this.deviceRequestMessageType.name(), this);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing distribution automation request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        int retryCount = 0;
        boolean isScheduled = false;
        Serializable request = null;

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
            request = message.getObject();
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

        final RequestMessageData requestMessageData = RequestMessageData.newRequestMessageDataBuilder()
                .withDomain(domain).withDomainVersion(domainVersion).withMessageType(messageType)
                .withRetryCount(retryCount).withIsScheduled(isScheduled).withCorrelationUid(correlationUid)
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).build();

        this.printDomainInfo(messageType, domain, domainVersion);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final Builder deviceRequest = DeviceRequest.newDeviceRequestBuilder()
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withCorrelationUid(correlationUid).withDomain(domain)
                .withDomainVersion(domainVersion).withMessageType(messageType).withIpAddress(ipAddress)
                .withRetryCount(retryCount).withIsScheduled(isScheduled);

        this.deviceService.getData(new DaDeviceRequest(deviceRequest, request), iec61850DeviceResponseHandler, this);
    }

    /**
     * Override to include the data in the response
     */
    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType, final int retryCount, final int messagePriority, final Long scheduleTime) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException ex = null;
        Serializable dataObject = null;

        try {
            final DaDeviceResponse response = (DaDeviceResponse) deviceResponse;
            this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
            dataObject = response.getDataResponse();
        } catch (final OsgpException e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            ex = e;
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType, messagePriority, scheduleTime);
        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage.Builder().domain(domain)
                .domainVersion(domainVersion).deviceMessageMetadata(deviceMessageMetadata).result(result)
                .osgpException(ex).dataObject(dataObject).retryCount(retryCount).build();
        responseMessageSender.send(protocolResponseMessage);
    }
}
