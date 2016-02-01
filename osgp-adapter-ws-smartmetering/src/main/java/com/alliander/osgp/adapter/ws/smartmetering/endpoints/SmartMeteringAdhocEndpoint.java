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

import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSMSDetailsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSMSDetailsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSMSDetailsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSMSDetailsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SMSDetailsType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSMSAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSMSAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSMSRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SendWakeupSMSResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.AdhocService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Endpoint
public class SmartMeteringAdhocEndpoint extends SmartMeteringEndpoint {

    private static final String SMARTMETER_ADHOC_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-adhoc/2014/10";

    @Autowired
    private AdhocService adhocService;

    @Autowired
    private AdhocMapper adhocMapper;

    public SmartMeteringAdhocEndpoint() {
    }

    @PayloadRoot(localPart = "SynchronizeTimeRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SynchronizeTimeAsyncResponse synchronizeTime(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SynchronizeTimeRequest request) throws OsgpException {

        final SynchronizeTimeAsyncResponse response = new SynchronizeTimeAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest synchronizeTimeRequest = new com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequest(
                request.getDeviceIdentification());

        final String correlationUid = this.adhocService.enqueueSynchronizeTimeRequest(organisationIdentification,
                synchronizeTimeRequest.getDeviceIdentification(), synchronizeTimeRequest);

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SynchronizeTimeAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SynchronizeTimeResponse getSynchronizeTimeResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SynchronizeTimeAsyncRequest request) throws OsgpException {

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

    @PayloadRoot(localPart = "SendWakeupSMSRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SendWakeupSMSAsyncResponse sendWakeupSMS(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SendWakeupSMSRequest request) throws OsgpException {

        final SendWakeupSMSAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
        .createSendWakeupSMSAsyncResponse();

        final String correlationUid = this.adhocService.enqueueSendWakeUpSMSRequest(organisationIdentification,
                request.getDeviceIdentification());

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;

    }

    @PayloadRoot(localPart = "SendWakeupSMSAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public SendWakeupSMSResponse getSendWakeupSMSResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SendWakeupSMSAsyncRequest request) throws OsgpException {

        SendWakeupSMSResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.adhocService.dequeueSendWakeUpSMSResponse(request
                    .getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "retrieving the send wakeup sms response data");

            response = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
                    .createSendWakeupSMSResponse();

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            final SMSDetailsType dataRequest = this.adhocMapper.map(meterResponseData.getMessageData(),
                    SMSDetailsType.class);
            response.setSmsMsgId(dataRequest.getSmsMsgId());

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetSMSDetailsRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetSMSDetailsAsyncResponse getSMSDetails(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetSMSDetailsRequest request) throws OsgpException {

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SMSDetails smsDetails = this.adhocMapper.map(
                request.getSMSDetails(), com.alliander.osgp.domain.core.valueobjects.smartmetering.SMSDetails.class);

        final GetSMSDetailsAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
        .createGetSMSDetailsAsyncResponse();

        final String correlationUid = this.adhocService.enqueueGetSMSDetailsRequest(organisationIdentification,
                smsDetails.getDeviceIdentification(), smsDetails);

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(smsDetails.getDeviceIdentification());

        return asyncResponse;

    }

    @PayloadRoot(localPart = "GetSMSDetailsAsyncRequest", namespace = SMARTMETER_ADHOC_NAMESPACE)
    @ResponsePayload
    public GetSMSDetailsResponse getSMSDetailsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetSMSDetailsAsyncRequest request) throws OsgpException {

        GetSMSDetailsResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.adhocService.dequeueGetSMSDetailsResponse(request
                    .getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "retrieving the get sms details response data");

            response = new com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ObjectFactory()
                    .createGetSMSDetailsResponse();

            final SMSDetailsType smsDetailsType = this.adhocMapper.map(meterResponseData.getMessageData(),
                    SMSDetailsType.class);
            response.setSMSDetails(smsDetailsType);

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }
}
