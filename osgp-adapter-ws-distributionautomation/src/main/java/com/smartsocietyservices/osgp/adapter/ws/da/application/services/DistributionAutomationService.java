/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.ws.da.application.services;

import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.smartsocietyservices.osgp.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import com.smartsocietyservices.osgp.adapter.ws.da.domain.entities.RtuResponseData;
import com.smartsocietyservices.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessage;
import com.smartsocietyservices.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageSender;
import com.smartsocietyservices.osgp.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageType;
import com.smartsocietyservices.osgp.domain.da.entities.RtuDevice;
import com.smartsocietyservices.osgp.domain.da.valueobjects.EmptyResponse;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataResponse;
import com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Service
@Transactional(value = "wsTransactionManager")
@Validated
public class DistributionAutomationService {

    private static final Logger LOGGER = LoggerFactory.getLogger( DistributionAutomationService.class );

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private DistributionAutomationRequestMessageSender requestMessageSender;

    @Autowired
    private RtuResponseDataService responseDataService;

    public DistributionAutomationService() {
        // Parameterless constructor required for transactions
    }

    public String enqueueGetDataRequest( @Identification final String organisationIdentification, @Identification final String deviceIdentification,
            @NotNull final GetDataRequest dataRequest ) throws OsgpException {

        LOGGER.debug( "enqueueGetDataRequest called with organisation {} and device {}", organisationIdentification, deviceIdentification );

        final Organisation organisation = this.domainHelperService.findOrganisation( organisationIdentification );

        final String correlationUid = this.correlationIdProviderService.getCorrelationId( organisationIdentification, deviceIdentification );

        final RtuDevice device = this.domainHelperService.findDevice( deviceIdentification );
        this.domainHelperService.isAllowed( organisation, device, DeviceFunction.GET_DATA );

        final DistributionAutomationRequestMessage message = new DistributionAutomationRequestMessage(
                DistributionAutomationRequestMessageType.GET_DATA, correlationUid, organisationIdentification, deviceIdentification, dataRequest,
                null );

        try {
            this.requestMessageSender.send( message );
        } catch ( final ArgumentNullOrEmptyException e ) {
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, e );
        }

        return correlationUid;
    }

    public GetDataResponse dequeueGetDataResponse( final String correlationUid ) throws OsgpException {

        LOGGER.debug( "dequeueGetDataRequest called with correlation uid {}", correlationUid );

        final RtuResponseData responseData = this.responseDataService.dequeue( correlationUid, ResponseMessage.class );
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch ( response.getResult() ) {
        case NOT_FOUND:
            throw new ResponseNotFoundException( ComponentType.WS_MICROGRIDS, "Response message not found." );
        case NOT_OK:
            if ( response.getOsgpException() != null ) {
                throw response.getOsgpException();
            }
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, "Response message not ok." );
        case OK:
            if ( response.getDataObject() != null ) {
                return (GetDataResponse) response.getDataObject();
            }
            // Should not get here
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, "Response message contains no data." );
        default:
            // Should not get here
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, "Response message contains invalid result." );
        }

    }

    public String enqueueSetDataRequest( final String organisationIdentification, final String deviceIdentification,
            final SetDataRequest setDataRequest ) throws OsgpException {

        LOGGER.debug( "enqueueSetDataRequest called with organisation {} and device {}", organisationIdentification, deviceIdentification );

        final Organisation organisation = this.domainHelperService.findOrganisation( organisationIdentification );

        final String correlationUid = this.correlationIdProviderService.getCorrelationId( organisationIdentification, deviceIdentification );

        final RtuDevice device = this.domainHelperService.findDevice( deviceIdentification );
        this.domainHelperService.isAllowed( organisation, device, DeviceFunction.SET_DATA );

        final DistributionAutomationRequestMessage message = new DistributionAutomationRequestMessage(
                DistributionAutomationRequestMessageType.SET_DATA, correlationUid, organisationIdentification, deviceIdentification, setDataRequest,
                null );

        try {
            this.requestMessageSender.send( message );
        } catch ( final ArgumentNullOrEmptyException e ) {
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, e );
        }

        return correlationUid;
    }

    public EmptyResponse dequeueSetDataResponse( final String correlationUid ) throws OsgpException {

        LOGGER.debug( "dequeueSetDataRequest called with correlation uid {}", correlationUid );

        final RtuResponseData responseData = this.responseDataService.dequeue( correlationUid, ResponseMessage.class );
        final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

        switch ( response.getResult() ) {
        case NOT_FOUND:
            throw new ResponseNotFoundException( ComponentType.WS_MICROGRIDS, "Response message not found." );
        case NOT_OK:
            if ( response.getOsgpException() != null ) {
                throw response.getOsgpException();
            }
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, "Response message not ok." );
        case OK:
            return new EmptyResponse();
        default:
            // Should not get here
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, "Response message contains invalid result." );
        }

    }
}
