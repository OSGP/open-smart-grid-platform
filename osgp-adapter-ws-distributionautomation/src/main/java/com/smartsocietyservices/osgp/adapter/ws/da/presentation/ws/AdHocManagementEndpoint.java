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
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.DeviceModelResponseType;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GenericAsyncResponseType;
import com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetPQValuesAsyncRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AdHocManagementEndpoint extends GenericDistributionAutomationEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger( AdHocManagementEndpoint.class );

    @PayloadRoot(localPart = "GetDeviceModelRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public GenericAsyncResponseType getDeviceModel( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetDeviceModelRequest request ) throws OsgpException {

        LOGGER.info( "Get Device Model Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentifier() );

        String correlationUid = null;
        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelRequest getDeviceModelRequest = this.mapper
                    .map( request, com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelRequest.class );
            correlationUid = this.service
                    .enqueueGetDeviceModelRequest( organisationIdentification, request.getDeviceIdentifier(), getDeviceModelRequest );

        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return getGenericResponse( correlationUid, request.getDeviceIdentifier() );
    }

    @PayloadRoot(localPart = "GetDeviceModelAsyncRequest",
            namespace = NAMESPACE)
    @ResponsePayload
    public DeviceModelResponseType getDeviceModel( @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesAsyncRequest request ) throws OsgpException {

        LOGGER.info( "Get Device Model Response received from organisation: {} for correlationUid: {}.", organisationIdentification,
                request.getAsyncRequest().getCorrelationUid() );

        DeviceModelResponseType response = new DeviceModelResponseType();
        try {
            final com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelResponse dataResponse = this.service
                    .dequeueGetDeviceModelResponse( request.getAsyncRequest().getCorrelationUid() );
            if ( dataResponse != null ) {
                response = this.mapper.map( dataResponse, DeviceModelResponseType.class );
                response.setResult( OsgpResultType.OK );
            } else {

                response.setResult( OsgpResultType.NOT_FOUND );
            }
        } catch ( final ResponseNotFoundException e ) {
            LOGGER.warn( "ResponseNotFoundException for getDeviceModel", e );
            response.setResult( OsgpResultType.NOT_FOUND );
        } catch ( final Exception e ) {
            this.handleException( LOGGER, e );
        }
        return response;
    }
}
