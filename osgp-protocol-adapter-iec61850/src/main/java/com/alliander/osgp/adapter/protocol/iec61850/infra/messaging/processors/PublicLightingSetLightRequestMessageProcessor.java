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
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.requests.SetLightDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.LightValueMessageDataContainerDto;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;

/**
 * Class for processing public lighting set light request messages
 */
@Component("iec61850PublicLightingSetLightRequestMessageProcessor")
public class PublicLightingSetLightRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingSetLightRequestMessageProcessor.class);

    public PublicLightingSetLightRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_LIGHT);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting set light request message");

        MessageMetadata messageMetadata = null;
        LightValueMessageDataContainerDto lightValueMessageDataContainer = null;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
            lightValueMessageDataContainer = (LightValueMessageDataContainerDto) message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        final RequestMessageData requestMessageData = RequestMessageData.newBuilder().messageMetadata(messageMetadata)
                .build();

        this.printDomainInfo(requestMessageData);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this
                .createIec61850DeviceResponseHandler(requestMessageData, message);

        final DeviceRequest.Builder deviceRequestBuilder = DeviceRequest.newBuilder().messageMetaData(messageMetadata);

        this.deviceService.setLight(new SetLightDeviceRequest(deviceRequestBuilder, lightValueMessageDataContainer),
                iec61850DeviceResponseHandler);
    }
}
