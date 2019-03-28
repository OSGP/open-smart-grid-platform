/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests.SwitchConfigurationBankRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpEnvelopeProcessor;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.SignedOslpEnvelopeDto;
import org.opensmartgridplatform.oslp.UnsignedOslpEnvelopeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing common get configuration request messages
 */
@Component("oslpCommonSwitchConfigurationRequestMessageProcessor")
public class CommonSwitchConfigurationRequestMessageProcessor extends DeviceRequestMessageProcessor
        implements OslpEnvelopeProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommonSwitchConfigurationRequestMessageProcessor.class);

    public CommonSwitchConfigurationRequestMessageProcessor() {
        super(MessageType.SWITCH_CONFIGURATION_BANK);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common get configuration message");

        MessageMetadata messageMetadata;
        String configurationBank;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            configurationBank = (String) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                messageMetadata.getDomainVersion());

        final SwitchConfigurationBankRequest deviceRequest = new SwitchConfigurationBankRequest(
                DeviceRequest.newBuilder().messageMetaData(messageMetadata), configurationBank);

        this.deviceService.switchConfiguration(deviceRequest);
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

        final DeviceResponseHandler deviceResponseHandler = new DeviceResponseHandler() {

            @Override
            public void handleResponse(final DeviceResponse deviceResponse) {
                CommonSwitchConfigurationRequestMessageProcessor.this.handleEmptyDeviceResponse(deviceResponse,
                        CommonSwitchConfigurationRequestMessageProcessor.this.responseMessageSender, domain,
                        domainVersion, messageType, retryCount);
            }

            @Override
            public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
                CommonSwitchConfigurationRequestMessageProcessor.this.handleUnableToConnectDeviceResponse(
                        deviceResponse, t, domain, domainVersion, messageType, isScheduled, retryCount);
            }

        };

        final DeviceRequest deviceRequest = new DeviceRequest(organisationIdentification, deviceIdentification,
                correlationUid, messagePriority);

        try {
            this.deviceService.doSwitchConfiguration(oslpEnvelope, deviceRequest, deviceResponseHandler, ipAddress);
        } catch (final IOException e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, domain, domainVersion,
                    messageType, messagePriority, retryCount);
        }
    }
}
