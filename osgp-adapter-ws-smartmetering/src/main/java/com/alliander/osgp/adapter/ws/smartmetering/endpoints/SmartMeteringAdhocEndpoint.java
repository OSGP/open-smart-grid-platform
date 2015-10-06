/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.AdhocService;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Endpoint
public class SmartMeteringAdhocEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringAdhocEndpoint.class);
    private static final String SMARTMETER_ADHOC_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-adhoc/2014/10";

    @Autowired
    private AdhocService adhocService;

    @Autowired
    private AdhocMapper adhocMapper;

    public SmartMeteringAdhocEndpoint() {
    }

    @PayloadRoot(localPart = "SynchronizeTimeRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SynchronizeTimeResponse requestSynchronizeTimeData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SynchronizeTimeRequest request) throws OsgpException {

        final SynchronizeTimeResponse response = new SynchronizeTimeResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest dataRequest = this.adhocMapper
                .map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest.class);

        final String correlationUid = this.adhocService.requestSynchronizeTimeData(organisationIdentification,
                dataRequest);

        final AsyncResponse asyncResponse = new AsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        response.setAsyncResponse(asyncResponse);

        return response;
    }
}