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

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.requests.SetSetPointsDeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.RtuDeviceRequestMessageProcessor;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import com.alliander.osgp.dto.valueobjects.microgrids.SetPointsRequestDto;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing microgrids get data request messages
 */
@Component("iec61850MicrogridsSetSetPointsRequestMessageProcessor")
public class MicrogridsSetSetPointsRequestMessageProcessor extends RtuDeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MicrogridsSetSetPointsRequestMessageProcessor.class);

    public MicrogridsSetSetPointsRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_SETPOINT);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing microgrids get data request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        String ipAddress = null;
        int retryCount = 0;
        boolean isScheduled = false;
        SetPointsRequestDto setSetPointsRequest = null;

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
                    setSetPointsRequest = (SetPointsRequestDto) message.getObject();
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

        final RequestMessageData requestMessageData = new RequestMessageData(null, domain, domainVersion, messageType,
                retryCount, isScheduled, correlationUid, organisationIdentification, deviceIdentification);

        this.printDomainInfo(messageType, domain, domainVersion);

        final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler = this.createIec61850DeviceResponseHandler(
                requestMessageData, message);

        final SetSetPointsDeviceRequest deviceRequest = new SetSetPointsDeviceRequest(organisationIdentification,
                deviceIdentification, correlationUid, setSetPointsRequest, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.deviceService.setSetPoints(deviceRequest, iec61850DeviceResponseHandler);
    }
}
