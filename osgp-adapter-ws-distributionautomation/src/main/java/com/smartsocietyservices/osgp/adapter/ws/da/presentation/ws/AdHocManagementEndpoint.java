/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.ws.da.presentation.ws;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.smartsocietyservices.osgp.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import com.smartsocietyservices.osgp.adapter.ws.da.application.mapping.DistributionAutomationMapper;
import com.smartsocietyservices.osgp.adapter.ws.da.application.services.DistributionAutomationService;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataAsyncRequest;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataAsyncResponse;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataRequest;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.GetDataResponse;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataAsyncRequest;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataAsyncResponse;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataRequest;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.adhocmanagement.SetDataResponse;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.common.AsyncResponse;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.common.OsgpResultType;
import com.smartsocietyservices.osgp.domain.da.valueobjects.EmptyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AdHocManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger( AdHocManagementEndpoint.class );
    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/da/adhocmanagement/2017/03";

    @Autowired
    private DistributionAutomationService service;

    @Autowired
    private DistributionAutomationMapper mapper;

    // === GET DATA ===

    @PayloadRoot(localPart = "GetDataRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GetDataAsyncResponse getData( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDataRequest request ) throws OsgpException {

        LOGGER.info( "Get Data Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification() );

        final GetDataAsyncResponse response = new GetDataAsyncResponse();

        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest dataRequest = this.mapper
                    .map( request, com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataRequest.class );
            final String correlationUid = this.service
                    .enqueueGetDataRequest( organisationIdentification, request.getDeviceIdentification(), dataRequest );

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid( correlationUid );
            asyncResponse.setDeviceId( request.getDeviceIdentification() );
            response.setAsyncResponse( asyncResponse );
        } catch ( final Exception e ) {
            this.handleException( e );
        }

        return response;
    }

    @PayloadRoot(localPart = "GetDataAsyncRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GetDataResponse getGetDataResponse( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDataAsyncRequest request ) throws OsgpException {

        LOGGER.info( "Get Data Response received from organisation: {} for correlationUid: {}.", organisationIdentification,
                request.getAsyncRequest().getCorrelationUid() );

        GetDataResponse response = new GetDataResponse();

        try {

            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetDataResponse dataResponse = this.service
                    .dequeueGetDataResponse( request.getAsyncRequest().getCorrelationUid() );
            if ( dataResponse != null ) {
                response = this.mapper.map( dataResponse, GetDataResponse.class );
                response.setResult( OsgpResultType.OK );

            } else {
                response.setResult( OsgpResultType.NOT_FOUND );
            }

        } catch ( final ResponseNotFoundException e ) {
            LOGGER.warn( "ResponseNotFoundException for getGetDataResponse", e );
            response.setResult( OsgpResultType.NOT_FOUND );
        } catch ( final Exception e ) {
            this.handleException( e );
        }

        return response;
    }

    // === SET DATA ===

    @PayloadRoot(localPart = "SetDataRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public SetDataAsyncResponse setData( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDataRequest request ) throws OsgpException {

        LOGGER.info( "Set Data Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification() );

        final SetDataAsyncResponse response = new SetDataAsyncResponse();

        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataRequest setDataRequest = this.mapper
                    .map( request, com.smartsocietyservices.osgp.domain.da.valueobjects.SetDataRequest.class );
            final String correlationUid = this.service
                    .enqueueSetDataRequest( organisationIdentification, request.getDeviceIdentification(), setDataRequest );

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid( correlationUid );
            asyncResponse.setDeviceId( request.getDeviceIdentification() );
            response.setAsyncResponse( asyncResponse );
        } catch ( final Exception e ) {
            this.handleException( e );
        }
        return response;
    }

    @PayloadRoot(localPart = "SetDataAsyncRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public SetDataResponse getSetDataResponse( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetDataAsyncRequest request ) throws OsgpException {

        LOGGER.info( "Get Set Data Response received from organisation: {} with correlationUid: {}.", organisationIdentification,
                request.getAsyncRequest().getCorrelationUid() );

        final SetDataResponse response = new SetDataResponse();

        try {
            final EmptyResponse setDataResponse = this.service.dequeueSetDataResponse( request.getAsyncRequest().getCorrelationUid() );
            if ( setDataResponse != null ) {
                response.setResult( OsgpResultType.OK );
            } else {
                response.setResult( OsgpResultType.NOT_FOUND );
            }
        } catch ( final ResponseNotFoundException e ) {
            LOGGER.warn( "ResponseNotFoundException for getSetDataResponse", e );
            response.setResult( OsgpResultType.NOT_FOUND );
        } catch ( final Exception e ) {
            this.handleException( e );
        }
        return response;
    }

    private void handleException( final Exception e ) throws OsgpException {
        // Rethrow exception if it already is a functional or technical
        // exception, otherwise throw new technical exception.
        LOGGER.error( "Exception occurred: ", e );
        if ( e instanceof OsgpException ) {
            throw (OsgpException) e;
        } else {
            throw new TechnicalException( ComponentType.WS_MICROGRIDS, e );
        }
    }
}
