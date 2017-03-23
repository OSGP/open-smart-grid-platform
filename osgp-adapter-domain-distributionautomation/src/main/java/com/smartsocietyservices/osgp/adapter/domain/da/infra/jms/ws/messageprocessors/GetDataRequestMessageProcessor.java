/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.ws.messageprocessors;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.smartsocietyservices.osgp.adapter.domain.da.application.services.AdHocManagementService;
import com.smartsocietyservices.osgp.adapter.domain.da.infra.jms.ws.AbstractWebServiceRequestMessageProcessor;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

/**
 * Class for processing da get data request messages
 */
@Component("domainDistributionAutomationGetDataRequestMessageProcessor")
public class GetDataRequestMessageProcessor extends AbstractWebServiceRequestMessageProcessor
{
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger( GetDataRequestMessageProcessor.class );

    @Autowired
    @Qualifier("domainDistributionAutomationAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    public GetDataRequestMessageProcessor()
    {
        super( DeviceFunction.GET_DATA );
    }

    @Override
    public void processMessage( final ObjectMessage message )
    {
        LOGGER.info( "Processing public lighting get status request message" );

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        GetDataRequest dataRequest = null;

        try
        {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty( Constants.ORGANISATION_IDENTIFICATION );
            deviceIdentification = message.getStringProperty( Constants.DEVICE_IDENTIFICATION );

            if ( message.getObject() instanceof GetDataRequest )
            {
                dataRequest = (GetDataRequest) message.getObject();
            }

        } catch ( final JMSException e )
        {
            LOGGER.error( "UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e );
            LOGGER.debug( "correlationUid: {}", correlationUid );
            LOGGER.debug( "messageType: {}", messageType );
            LOGGER.debug( "organisationIdentification: {}", organisationIdentification );
            LOGGER.debug( "deviceIdentification: {}", deviceIdentification );
            return;
        }

        try
        {
            LOGGER.info( "Calling application service function: {}", messageType );

            this.adHocManagementService.getData( organisationIdentification, deviceIdentification, correlationUid,
                    messageType, dataRequest );

        } catch ( final Exception e )
        {
            this.handleError( e, correlationUid, organisationIdentification, deviceIdentification, messageType );
        }
    }
}
