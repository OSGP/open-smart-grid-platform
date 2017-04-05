/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.ws.da.presentation.ws;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.smartsocietyservices.osgp.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.common.OsgpResultType;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GenericAsyncResponseType;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GenericPQValuesResponseType;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetPQValuesAsyncRequest;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicAsyncRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class MonitoringEndpoint extends GenericDistributionAutomationEndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger( MonitoringEndpoint.class );

    @PayloadRoot(localPart = "GetPQValuesRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GenericAsyncResponseType getPQValues( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesRequest request ) throws OsgpException {

        LOGGER.info( "Get PQ Values Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentifier() );

        String correlationUid = null;
        try {
            final GetPQValuesRequest getPQValue = this.mapper.map( request, GetPQValuesRequest.class );
            correlationUid = this.service.enqueueGetPQValuesRequest( organisationIdentification, request.getDeviceIdentifier(), getPQValue );

        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return getGenericResponse( correlationUid, request.getDeviceIdentifier() );
    }

    @PayloadRoot(localPart = "GetPQValuesAsyncRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GenericPQValuesResponseType getPQValuesAsync( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesAsyncRequest request ) throws OsgpException {

        LOGGER.info( "Get PQ Values Response received from organisation: {} for correlationUid: {}.", organisationIdentification,
                request.getAsyncRequest().getCorrelationUid() );

        GenericPQValuesResponseType response = new GenericPQValuesResponseType();
        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesResponse dataResponse = this.service
                    .dequeueGetPQValuesResponse( request.getAsyncRequest().getCorrelationUid() );
            if ( dataResponse != null ) {
                response = this.mapper.map( dataResponse, GenericPQValuesResponseType.class );
                response.setResult( OsgpResultType.OK );
            } else {
                response.setResult( OsgpResultType.NOT_FOUND );
            }
        } catch ( final ResponseNotFoundException e ) {
            LOGGER.warn( "ResponseNotFoundException for getGetDataResponse", e );
            response.setResult( OsgpResultType.NOT_FOUND );
        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return response;
    }

    @PayloadRoot(localPart = "GetPQValuesPeriodicRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GenericAsyncResponseType getPQValuesPeriodic( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesPeriodicRequest request ) throws OsgpException {

        LOGGER.info( "Get PQ Values Periodic Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentifier() );

        String correlationUid = null;
        try {
            final GetPQValuesPeriodicRequest getPQValuesPeriodicRequest = this.mapper.map( request, GetPQValuesPeriodicRequest.class );
            correlationUid = this.service
                    .enqueueGetPQValuesPeriodicRequest( organisationIdentification, request.getDeviceIdentifier(), getPQValuesPeriodicRequest );

        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return getGenericResponse( correlationUid, request.getDeviceIdentifier() );
    }

    @PayloadRoot(localPart = "GetPQValuesPeriodicAsyncRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GenericPQValuesResponseType getPQValuesPeriodicAsync( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesPeriodicAsyncRequest request ) throws OsgpException {

        LOGGER.info( "Get PQ Values Periodic Response received from organisation: {} for correlationUid: {}.", organisationIdentification,
                request.getAsyncRequest().getCorrelationUid() );

        GenericPQValuesResponseType response = new GenericPQValuesResponseType();
        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesResponse dataResponse = this.service
                    .dequeueGetPQValuesPeriodicResponse( request.getAsyncRequest().getCorrelationUid() );
            if ( dataResponse != null ) {
                response = this.mapper.map( dataResponse, GenericPQValuesResponseType.class );
                response.setResult( OsgpResultType.OK );
            } else {
                response.setResult( OsgpResultType.NOT_FOUND );
            }
        } catch ( final ResponseNotFoundException e ) {
            LOGGER.warn( "ResponseNotFoundException for getPQValuesPeriodicAsync", e );
            response.setResult( OsgpResultType.NOT_FOUND );
        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return response;
    }
}
