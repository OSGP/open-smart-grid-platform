/**
 * Copyright 2014-2016 Smart Society Services B.V.
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
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.GetPowerUsageHistoryDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetPowerUsageHistoryDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

/**
 * Class for processing public lighting get power usage history request messages
 */
@Component("iec61850PublicLightingGetPowerUsageHistoryRequestMessageProcessor")
public class PublicLightingGetPowerUsageHistoryRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PublicLightingGetPowerUsageHistoryRequestMessageProcessor.class);

    public PublicLightingGetPowerUsageHistoryRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_POWER_USAGE_HISTORY);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting get power usage history request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        Boolean isScheduled = null;
        int retryCount = 0;
        int messagePriority = 0;
        Long scheduleTime = null;
        PowerUsageHistoryMessageDataContainerDto powerUsageHistoryMessageDataContainerDto;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            ipAddress = message.getStringProperty(Constants.IP_ADDRESS);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
            retryCount = message.getIntProperty(Constants.RETRY_COUNT);
            messagePriority = message.getJMSPriority();
            scheduleTime = message.propertyExists(Constants.SCHEDULE_TIME)
                    ? message.getLongProperty(Constants.SCHEDULE_TIME) : null;
            powerUsageHistoryMessageDataContainerDto = (PowerUsageHistoryMessageDataContainerDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("ipAddress: {}", ipAddress);
            LOGGER.debug("scheduled: {}", isScheduled);
            LOGGER.debug("retryCount: {}", retryCount);
            LOGGER.debug("messagePriority: {}", messagePriority);
            LOGGER.debug("scheduleTime: {}", scheduleTime);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder()
                .messageData(powerUsageHistoryMessageDataContainerDto).domain(domain).domainVersion(domainVersion)
                .messageType(messageType).retryCount(retryCount).isScheduled(isScheduled).correlationUid(correlationUid)
                .organisationIdentification(organisationIdentification).deviceIdentification(deviceIdentification)
                .ipAddress(ipAddress).messagePriority(messagePriority).scheduleTime(scheduleTime).build();

        this.printDomainInfo(messageType, domain, domainVersion);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final Builder deviceRequestBuilder = DeviceRequest.newBuilder()
                .organisationIdentification(organisationIdentification).deviceIdentification(deviceIdentification)
                .correlationUid(correlationUid).domain(domain).domainVersion(domainVersion).messageType(messageType)
                .ipAddress(ipAddress).retryCount(retryCount).isScheduled(isScheduled);

        this.deviceService.getPowerUsageHistory(
                new GetPowerUsageHistoryDeviceRequest(deviceRequestBuilder, powerUsageHistoryMessageDataContainerDto),
                iec61850DeviceResponseHandler);
    }

    @Override
    public void handleDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {
        LOGGER.info("Override for handleDeviceResponse() by PublicLightingGetPowerUsageHistoryRequestMessageProcessor");
        this.handleGetPowerUsageHistoryDeviceResponse(deviceResponse, responseMessageSender, domainInformation,
                messageType, retryCount);
    }

    private void handleGetPowerUsageHistoryDeviceResponse(final DeviceResponse deviceResponse,
            final ResponseMessageSender responseMessageSender, final DomainInformation domainInformation,
            final String messageType, final int retryCount) {

        final GetPowerUsageHistoryDeviceResponse getPowerUsageHistoryDeviceResponse = (GetPowerUsageHistoryDeviceResponse) deviceResponse;
        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = null;
        PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainer = null;

        try {
            powerUsageHistoryResponseMessageDataContainer = new PowerUsageHistoryResponseMessageDataContainerDto(
                    getPowerUsageHistoryDeviceResponse.getPowerUsageHistoryData());
        } catch (final Exception e) {
            LOGGER.error("Device Response Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.PROTOCOL_IEC61850,
                    "Unexpected exception while retrieving response message", e);
        }

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(
                deviceResponse.getDeviceIdentification(), deviceResponse.getOrganisationIdentification(),
                deviceResponse.getCorrelationUid(), messageType);
        final ProtocolResponseMessage.Builder builder = new ProtocolResponseMessage.Builder();
        final ProtocolResponseMessage responseMessage = builder.domain(domainInformation.getDomain())
                .domainVersion(domainInformation.getDomainVersion()).deviceMessageMetadata(deviceMessageMetadata)
                .result(result).osgpException(osgpException).dataObject(powerUsageHistoryResponseMessageDataContainer)
                .retryCount(retryCount).build();

        responseMessageSender.send(responseMessage);
    }
}
