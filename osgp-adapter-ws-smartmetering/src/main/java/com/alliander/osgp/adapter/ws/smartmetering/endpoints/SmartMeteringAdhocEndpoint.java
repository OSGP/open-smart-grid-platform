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
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSmsDetailsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSmsDetailsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSmsDetailsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSmsDetailsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.RetrieveConfigurationObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSmsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSmsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSmsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSmsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.AdhocService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
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
            @RequestPayload final SynchronizeTimeRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        final SynchronizeTimeAsyncResponse response = new SynchronizeTimeAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest synchronizeTimeRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueSynchronizeTimeRequest(organisationIdentification,
                synchronizeTimeRequest.getDeviceIdentification(), synchronizeTimeRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority));

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

    @PayloadRoot(localPart = "SendWakeupSmsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SendWakeupSmsAsyncResponse sendWakeupSms(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SendWakeupSmsRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        final SendWakeupSmsAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
        .createSendWakeupSmsAsyncResponse();

        final String correlationUid = this.adhocService.enqueueSendWakeUpSmsRequest(organisationIdentification,
                request.getDeviceIdentification(), MessagePriorityEnum.getMessagePriority(messagePriority));

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;

    }

    @PayloadRoot(localPart = "SendWakeupSmsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SendWakeupSmsResponse getSendWakeupSmsResponse(@RequestPayload final SendWakeupSmsAsyncRequest request)
            throws OsgpException {

        SendWakeupSmsResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.adhocService.dequeueSendWakeUpSmsResponse(request
                    .getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "retrieving the send wakeup sms response data");

            response = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
            .createSendWakeupSmsResponse();

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            final SmsDetailsType dataRequest = this.adhocMapper.map(meterResponseData.getMessageData(),
                    SmsDetailsType.class);
            response.setSmsMsgId(dataRequest.getSmsMsgId());

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetSmsDetailsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetSmsDetailsAsyncResponse getSmsDetails(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetSmsDetailsRequest request, @MessagePriority final String messagePriority)
            throws OsgpException {

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails smsDetails = this.adhocMapper.map(
                request.getSmsDetails(), com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails.class);

        final GetSmsDetailsAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
        .createGetSmsDetailsAsyncResponse();

        final String correlationUid = this.adhocService.enqueueGetSmsDetailsRequest(organisationIdentification,
                smsDetails.getDeviceIdentification(), smsDetails,
                MessagePriorityEnum.getMessagePriority(messagePriority));

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(smsDetails.getDeviceIdentification());

        return asyncResponse;

    }

    @PayloadRoot(localPart = "GetSmsDetailsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetSmsDetailsResponse getSmsDetailsResponse(@RequestPayload final GetSmsDetailsAsyncRequest request)
            throws OsgpException {

        GetSmsDetailsResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.adhocService.dequeueGetSmsDetailsResponse(request
                    .getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "retrieving the get sms details response data");

            response = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
            .createGetSmsDetailsResponse();

            final SmsDetailsType smsDetailsType = this.adhocMapper.map(meterResponseData.getMessageData(),
                    SmsDetailsType.class);
            response.setSmsDetails(smsDetailsType);

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
            @MessagePriority final String messagePriority) throws OsgpException {

        final RetrieveConfigurationObjectsAsyncResponse response = new RetrieveConfigurationObjectsAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest retrieveConfigurationObjectsRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueRetrieveConfigurationObjectsRequest(
                organisationIdentification, retrieveConfigurationObjectsRequest.getDeviceIdentification(),
                retrieveConfigurationObjectsRequest, MessagePriorityEnum.getMessagePriority(messagePriority));

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
            final MeterResponseData meterResponseData = this.adhocService
                    .dequeueRetrieveConfigurationObjectsResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setOutput((String) meterResponseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
