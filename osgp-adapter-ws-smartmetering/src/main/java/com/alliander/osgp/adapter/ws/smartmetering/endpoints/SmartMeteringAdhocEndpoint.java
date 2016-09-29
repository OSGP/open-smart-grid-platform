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
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveAllAttributeValuesAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveAllAttributeValuesAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveAllAttributeValuesResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SpecificAttributeValueResponse;
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

    @PayloadRoot(localPart = "RetrieveAllAttributeValuesRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public RetrieveAllAttributeValuesAsyncResponse retrieveAllAttributeValues(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final RetrieveAllAttributeValuesRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
                    throws OsgpException {

        final RetrieveAllAttributeValuesAsyncResponse response = new RetrieveAllAttributeValuesAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveAllAttributeValuesRequest retrieveAllAttributeValuesRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveAllAttributeValuesRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueRetrieveAllAttributeValuesRequest(
                organisationIdentification, retrieveAllAttributeValuesRequest.getDeviceIdentification(),
                retrieveAllAttributeValuesRequest, MessagePriorityEnum.getMessagePriority(messagePriority),
                this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SpecificAttributeValueAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SpecificAttributeValueResponse getSpecificAttributeValueResponse(
            @RequestPayload final SpecificAttributeValueAsyncRequest request) throws OsgpException {

        SpecificAttributeValueResponse response = null;
        try {
            response = new SpecificAttributeValueResponse();
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

    @PayloadRoot(localPart = "SpecificAttributeValueRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SpecificAttributeValueAsyncResponse getSpecificAttributeValue(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SpecificAttributeValueRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        final SpecificAttributeValueAsyncResponse response = new SpecificAttributeValueAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest getSpecificAttributeValueRequest = this.adhocMapper
                .map(request,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequest.class);

        final String correlationUid = this.adhocService
                .enqueueSpecificAttributeValueRequest(organisationIdentification, request.getDeviceIdentification(),
                        getSpecificAttributeValueRequest, MessagePriorityEnum.getMessagePriority(messagePriority),
                        this.adhocMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "RetrieveAllAttributeValuesAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public RetrieveAllAttributeValuesResponse getRetrieveAllAttributeValuesResponse(
            @RequestPayload final RetrieveAllAttributeValuesAsyncRequest request) throws OsgpException {

        RetrieveAllAttributeValuesResponse response = null;
        try {
            response = new RetrieveAllAttributeValuesResponse();
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
