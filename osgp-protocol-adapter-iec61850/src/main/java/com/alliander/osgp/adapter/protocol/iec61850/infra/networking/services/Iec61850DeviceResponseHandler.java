/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.io.Serializable;
import java.util.Objects;

import javax.jms.JMSException;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.BaseMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.ConnectionFailureException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

public class Iec61850DeviceResponseHandler implements DeviceResponseHandler {

    private final BaseMessageProcessor messageProcessor;
    private final Integer jmsxDeliveryCount;
    private final DeviceMessageMetadata deviceMessageMetadata;
    private final DomainInformation domainInformation;
    private final Integer retryCount;
    private final Boolean isScheduled;
    private final Serializable messageData;
    private final ResponseMessageSender responseMessageSender;

    public Iec61850DeviceResponseHandler(final BaseMessageProcessor messageProcessor, final Integer jmsxDeliveryCount,
            final RequestMessageData requestMessageData, final ResponseMessageSender responseMessageSender) {
        this.messageProcessor = messageProcessor;
        this.jmsxDeliveryCount = jmsxDeliveryCount;
        this.deviceMessageMetadata = new DeviceMessageMetadata(requestMessageData.getDeviceIdentification(),
                requestMessageData.getOrganisationIdentification(), requestMessageData.getCorrelationUid(),
                requestMessageData.getMessageType(), requestMessageData.getMessagePriority());
        this.domainInformation = new DomainInformation(requestMessageData.getDomain(),
                requestMessageData.getDomainVersion());
        this.retryCount = requestMessageData.getRetryCount();
        this.isScheduled = requestMessageData.isScheduled();
        this.messageData = requestMessageData.getMessageData();
        this.responseMessageSender = responseMessageSender;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler
     * #handleResponse(com.alliander.osgp.adapter.protocol.iec61850.device.
     * DeviceResponse)
     */
    @Override
    public void handleResponse(final DeviceResponse deviceResponse) {
        this.messageProcessor.handleDeviceResponse(deviceResponse, this.responseMessageSender,
                this.domainInformation.getDomain(), this.domainInformation.getDomainVersion(),
                this.deviceMessageMetadata.getMessageType(), this.retryCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler
     * #handleConnectionFailure(java.lang.Throwable,
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse)
     */
    @Override
    public void handleConnectionFailure(final Throwable t, final DeviceResponse deviceResponse) throws JMSException {
        Objects.requireNonNull(t, "handleConnectionFailure() Throwable t may not be null");
        final ConnectionFailureException connectionFailureException = new ConnectionFailureException(
                ComponentType.PROTOCOL_IEC61850, t.getMessage());
        this.messageProcessor.checkForRedelivery(this.deviceMessageMetadata, connectionFailureException,
                this.domainInformation.getDomain(), this.domainInformation.getDomainVersion(), this.jmsxDeliveryCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler
     * #handleException(java.lang.Throwable,
     * com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse)
     */
    @Override
    public void handleException(final Throwable t, final DeviceResponse deviceResponse) {
        Objects.requireNonNull(t, "handleException() Throwable t may not be null");
        this.messageProcessor.handleUnExpectedError(deviceResponse, t, this.messageData,
                this.domainInformation.getDomain(), this.domainInformation.getDomainVersion(),
                this.deviceMessageMetadata.getMessageType(), this.isScheduled, this.retryCount);
    }
}
