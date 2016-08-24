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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.ScheduleTime;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificConfigurationObjectAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.AdhocService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AssociationLnListType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

@Endpoint
public class SmartMeteringAdhocEndpoint extends SmartMeteringEndpoint {

    private static final String SMARTMETER_ADHOC_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-adhoc/2014/10";

    @Autowired
    private AdhocService adhocService;

    @Autowired
    private AdhocMapper adhocMapper;

    @PayloadRoot(localPart = "SynchronizeTimeRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SynchronizeTimeAsyncResponse synchronizeTime(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SynchronizeTimeRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        final SynchronizeTimeAsyncResponse response = new SynchronizeTimeAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData synchronizeTimeRequestData = this.adhocMapper
                .map(request.getSynchronizeTimeRequestData(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData.class);

        final String correlationUid = this.adhocService
                .enqueueSynchronizeTimeRequest(organisationIdentification, request.getDeviceIdentification(),
                        synchronizeTimeRequestData, MessagePriorityEnum.getMessagePriority(messagePriority),
                        this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SynchronizeTimeAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SynchronizeTimeResponse getSynchronizeTimeResponse(@RequestPayload final SynchronizeTimeAsyncRequest request)
            throws OsgpException {

        SynchronizeTimeResponse response = null;
        try {
            response = new SynchronizeTimeResponse();
            final MeterResponseData meterResponseData = this.adhocService.dequeueSynchronizeTimeResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "RetrieveConfigurationObjectsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public RetrieveConfigurationObjectsAsyncResponse retrieveConfigurationObjects(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveConfigurationObjectsRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        final RetrieveConfigurationObjectsAsyncResponse response = new RetrieveConfigurationObjectsAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest retrieveConfigurationObjectsRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueRetrieveConfigurationObjectsRequest(
                organisationIdentification, retrieveConfigurationObjectsRequest.getDeviceIdentification(),
                retrieveConfigurationObjectsRequest, MessagePriorityEnum.getMessagePriority(messagePriority),
                this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SpecificConfigurationObjectAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SpecificConfigurationObjectResponse getSpecificConfigurationObjectResponse(
            @RequestPayload final SpecificConfigurationObjectAsyncRequest request) throws OsgpException {

        SpecificConfigurationObjectResponse response = null;
        try {
            response = new SpecificConfigurationObjectResponse();
            final MeterResponseData meterResponseData = this.adhocService.dequeueResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                if (ResponseMessageResultType.OK == meterResponseData.getResultType()) {
                    response.setConfigurationData((String) meterResponseData.getMessageData());
                } else {
                    response.setConfigurationData("");
                    response.setException((String) meterResponseData.getMessageData());
                }
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SpecificConfigurationObjectRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SpecificConfigurationObjectAsyncResponse getSpecificConfigurationObject(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SpecificConfigurationObjectRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        final SpecificConfigurationObjectAsyncResponse response = new SpecificConfigurationObjectAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificConfigurationObjectRequest getSpecificConfigurationObjectRequest = this.adhocMapper
                .map(request,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificConfigurationObjectRequest.class);

        final String correlationUid = this.adhocService
                .enqueueSpecificConfigurationObjectRequest(organisationIdentification,
                        request.getDeviceIdentification(), getSpecificConfigurationObjectRequest,
                        MessagePriorityEnum.getMessagePriority(messagePriority),
                        this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "RetrieveConfigurationObjectsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public RetrieveConfigurationObjectsResponse getRetrieveConfigurationObjectsResponse(
            @RequestPayload final RetrieveConfigurationObjectsAsyncRequest request) throws OsgpException {

        RetrieveConfigurationObjectsResponse response = null;
        try {
            response = new RetrieveConfigurationObjectsResponse();
            final MeterResponseData meterResponseData = this.adhocService.dequeueResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setOutput((String) meterResponseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetAssociationLnObjectsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetAssociationLnObjectsAsyncResponse getAssociationLnObjects(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetAssociationLnObjectsRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        final GetAssociationLnObjectsAsyncResponse response = new GetAssociationLnObjectsAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAssociationLnObjectsRequest getAssociationLnObjectsRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAssociationLnObjectsRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueGetAssociationLnObjectsRequest(
                organisationIdentification, getAssociationLnObjectsRequest.getDeviceIdentification(),
                getAssociationLnObjectsRequest, MessagePriorityEnum.getMessagePriority(messagePriority),
                this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "GetAssociationLnObjectsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetAssociationLnObjectsResponse getGetAssociationLnObjectsResponse(
            @RequestPayload final GetAssociationLnObjectsAsyncRequest request) throws OsgpException {

        GetAssociationLnObjectsResponse response = null;
        try {
            response = new GetAssociationLnObjectsResponse();
            final MeterResponseData meterResponseData = this.adhocService.dequeueResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof AssociationLnListType) {
                response.setAssociationLnList(this.adhocMapper.map(meterResponseData.getMessageData(),
                        com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.AssociationLnListType.class));
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }
}
