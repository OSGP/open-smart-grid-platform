/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SetConfigurationDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for processing common set configuration request messages
 */
@Component("oslpCommonSetConfigurationRequestMessageProcessor")
public class CommonSetConfigurationRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonSetConfigurationRequestMessageProcessor.class);

    @Autowired
    private CommonRebootRequestMessageProcessor commonRebootRequestMessageProcessor;

    public CommonSetConfigurationRequestMessageProcessor() {
        super(MessageType.SET_CONFIGURATION);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common set configuration message");

        MessageMetadata messageMetadata;
        ConfigurationDto configuration;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            configuration = (ConfigurationDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        try {
            this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                    messageMetadata.getDomainVersion());

            final SetConfigurationDeviceRequest deviceRequest = new SetConfigurationDeviceRequest(
                    DeviceRequest.newBuilder().messageMetaData(messageMetadata), configuration);

            this.deviceService.setConfiguration(deviceRequest);
        } catch (final RuntimeException e) {
            this.handleError(e, messageMetadata);
        }
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
        final int messagePriority = unsignedOslpEnvelopeDto.getMessagePriority();
        final String ipAddress = unsignedOslpEnvelopeDto.getIpAddress();
        final int retryCount = unsignedOslpEnvelopeDto.getRetryCount();
        final boolean isScheduled = unsignedOslpEnvelopeDto.isScheduled();

        final DeviceResponseHandler setConfigurationDeviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                if (((EmptyDeviceResponse) deviceResponse).getStatus().equals(DeviceMessageStatus.OK)) {
                    // If the response is OK, just log it. The setReboot()
                    // function will be called next.
                    LOGGER.info("setConfiguration() successful for device : {}",
                            deviceResponse.getDeviceIdentification());
                } else {
                    // If the response is not OK, send a response message to the
                    // responses queue.
                    CommonSetConfigurationRequestMessageProcessor.this.handleScheduledEmptyDeviceResponse(
                            deviceResponse, CommonSetConfigurationRequestMessageProcessor.this.responseMessageSender,
                            domain, domainVersion, messageType, isScheduled, retryCount);
                }
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                CommonSetConfigurationRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(deviceResponse,
                        t, domain, domainVersion, messageType, isScheduled, retryCount);
            }
        };

        final DeviceRequest setConfigurationDeviceRequest = new DeviceRequest(organisationIdentification,
                deviceIdentification, correlationUid, messagePriority);

        final DeviceResponseHandler setRebootDeviceResponseHandler = this.commonRebootRequestMessageProcessor
                .createSetRebootDeviceResponseHandler(domain, domainVersion, messageType, retryCount, isScheduled);

        final DeviceRequest setRebootDeviceRequest = DeviceRequest.newBuilder()
                .organisationIdentification(organisationIdentification)
                .deviceIdentification(deviceIdentification)
                .correlationUid(correlationUid)
                .messagePriority(messagePriority)
                .messageType(MessageType.SET_REBOOT.name())
                .domain(domain)
                .domainVersion(domainVersion)
                .ipAddress(ipAddress)
                .isScheduled(isScheduled)
                .retryCount(retryCount)
                .build();

        try {
            this.deviceService.doSetConfiguration(oslpEnvelope, setConfigurationDeviceRequest, setRebootDeviceRequest,
                    setConfigurationDeviceResponseHandler, setRebootDeviceResponseHandler, ipAddress);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, messagePriority, retryCount);
        }
    }
}
