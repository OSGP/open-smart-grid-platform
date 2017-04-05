/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.core.messageprocessors;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.smartsocietyservices.osgp.adapter.domain.da.application.services.DeviceManagementService;
import com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.core.AbstractOsgpCoreResponseMessageProcessor;
import com.smartsocietyservices.osgp.dto.da.GetHealthStatusResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * Class for processing da get data response messages
 */
@Component("domainDistributionAutomationGetDeviceManagementResponseMessageProcessor")
public class GetDeviceManagementResponseMessageProcessor extends AbstractOsgpCoreResponseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger( GetDeviceManagementResponseMessageProcessor.class );

    @Autowired
    @Qualifier("domainDistributionAutomationDeviceManagementService")
    private DeviceManagementService deviceManagementService;

    protected GetDeviceManagementResponseMessageProcessor() {
        super( DeviceFunction.GET_HEALTH_STATUS );
    }

    @Override
    public void processMessage( final ObjectMessage message ) throws JMSException {
        LOGGER.debug( "Processing DA Get Health Status response message" );

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage = null;
        ResponseMessageResultType responseMessageResultType = null;
        OsgpException osgpException = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty( Constants.ORGANISATION_IDENTIFICATION );
            deviceIdentification = message.getStringProperty( Constants.DEVICE_IDENTIFICATION );

            responseMessage = (ResponseMessage) message.getObject();
            responseMessageResultType = responseMessage.getResult();
            osgpException = responseMessage.getOsgpException();
            dataObject = responseMessage.getDataObject();
        } catch ( final JMSException e ) {
            LOGGER.error( "UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e );
            LOGGER.debug( "correlationUid: {}", correlationUid );
            LOGGER.debug( "messageType: {}", messageType );
            LOGGER.debug( "organisationIdentification: {}", organisationIdentification );
            LOGGER.debug( "deviceIdentification: {}", deviceIdentification );
            LOGGER.debug( "responseMessageResultType: {}", responseMessageResultType );
            LOGGER.debug( "deviceIdentification: {}", deviceIdentification );
            LOGGER.debug( "osgpException: {}", osgpException );
            return;
        }
        try {
            LOGGER.info( "Calling application service function to handle response: {}", messageType );

            final GetHealthStatusResponseDto dataResponse = (GetHealthStatusResponseDto) dataObject;

            this.deviceManagementService
                    .handleHealthStatusResponse( dataResponse, deviceIdentification, organisationIdentification, correlationUid, messageType,
                            responseMessageResultType, osgpException );

        } catch ( final Exception e ) {
            this.handleError( e, correlationUid, organisationIdentification, deviceIdentification, messageType );
        }
    }
}
