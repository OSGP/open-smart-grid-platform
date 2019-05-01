/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetPQValuesResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.Measurement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroup;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReport;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReportHeader;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.ReasonType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class MonitoringEndpoint extends GenericDistributionAutomationEndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringEndpoint.class);

    @PayloadRoot(localPart = "GetPQValuesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPQValuesAsyncResponse getPQValues(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesRequest request) throws OsgpException {

        LOGGER.info("Get PQ Values Request received from organisation: {} for device: {}.", organisationIdentification,
                request.getDeviceIdentification());

        final GetPQValuesAsyncResponse response = new GetPQValuesAsyncResponse();

        try {
            final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest getPQValue = this.mapper
                    .map(request, org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest.class);
            final String correlationUid = this.service.enqueueGetPQValuesRequest(organisationIdentification,
                    request.getDeviceIdentification(), getPQValue);
            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            this.handleException(LOGGER, e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetPQValuesAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPQValuesResponse getPQValuesAsync(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesAsyncRequest request) throws OsgpException {

        LOGGER.info("Get PQ Values Response received from organisation: {} for correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        GetPQValuesResponse response = new GetPQValuesResponse();
        try {
            final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse dataResponse = this.service
                    .dequeueGetPQValuesResponse(request.getAsyncRequest().getCorrelationUid());
            if (dataResponse != null) {
                response = this.mapper.map(dataResponse, GetPQValuesResponse.class);
                response.setResult(OsgpResultType.OK);
            } else {
                response.setResult(OsgpResultType.NOT_FOUND);
            }
        } catch (final ResponseNotFoundException e) {
            LOGGER.warn("ResponseNotFoundException for getGetDataResponse", e);
            response.setResult(OsgpResultType.NOT_FOUND);
        } catch (final Exception e) {
            this.handleException(LOGGER, e);
        }
        response.setDeviceIdentification(request.getAsyncRequest().getDeviceId());
        return response;
    }

    @PayloadRoot(localPart = "GetPQValuesPeriodicRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPQValuesPeriodicAsyncResponse getPQValuesPeriodic(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesPeriodicRequest request) throws OsgpException {

        LOGGER.info("Get PQ Values Periodic Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final GetPQValuesPeriodicAsyncResponse response = new GetPQValuesPeriodicAsyncResponse();

        try {
            final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest getPQValuesPeriodicRequest = this.mapper
                    .map(request, org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest.class);
            final String correlationUid = this.service.enqueueGetPQValuesPeriodicRequest(organisationIdentification,
                    request.getDeviceIdentification(), getPQValuesPeriodicRequest);
            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {
            this.handleException(LOGGER, e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetPQValuesPeriodicAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetPQValuesPeriodicResponse getPQValuesPeriodicAsync(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPQValuesPeriodicAsyncRequest request) throws OsgpException {

        LOGGER.info("Get PQ Values Periodic Response received from organisation: {} for correlationUid: {}.",
                organisationIdentification, request.getAsyncRequest().getCorrelationUid());

        GetPQValuesPeriodicResponse response = new GetPQValuesPeriodicResponse();
        try {
            final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse dataResponse = this.service
                    .dequeueGetPQValuesPeriodicResponse(request.getAsyncRequest().getCorrelationUid());
            if (dataResponse != null) {
                response = this.mapper.map(dataResponse, GetPQValuesPeriodicResponse.class);
                response.setResult(OsgpResultType.OK);
            } else {
                response.setResult(OsgpResultType.NOT_FOUND);
            }
        } catch (final ResponseNotFoundException e) {
            LOGGER.warn("ResponseNotFoundException for getPQValuesPeriodicAsync", e);
            response.setResult(OsgpResultType.NOT_FOUND);
        } catch (final Exception e) {
            this.handleException(LOGGER, e);
        }
        response.setDeviceIdentification(request.getAsyncRequest().getDeviceId());
        return response;
    }

    @PayloadRoot(localPart = "GetMeasurementReportRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetMeasurementReportResponse getMeasurementReport(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetMeasurementReportRequest request) throws OsgpException {

        LOGGER.info("Get Measurement Report Request received from organisation: {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        // TODO: code voor ophalen measurement report maken.
        final MeasurementReport mr = this.createDummyMeasurementReport();
        final GetMeasurementReportResponse gmrr = new GetMeasurementReportResponse();
        gmrr.setMeasurementReport(mr);
        gmrr.setResult(OsgpResultType.OK);

        return gmrr;
    }

    private MeasurementReport createDummyMeasurementReport() {
        final MeasurementElement me = new MeasurementElement();
        me.setBitmaskMeasurementElement((byte) 64);

        final Measurement m = new Measurement();
        m.getMeasurementElement().add(me);

        final MeasurementGroup mg = new MeasurementGroup();
        mg.setMeasurementGroupIdentifier(7);
        mg.getMeasurement().add(m);

        final MeasurementReportHeader mrh = new MeasurementReportHeader();
        mrh.setCommonAddress(3);
        mrh.setMeasurementType(MeasurementType.MEASURED_SHORT_FLOAT_WITH_TIME_TAG);
        mrh.setOriginatorAddress(83);
        mrh.setReasonType(ReasonType.SPONTANEOUS);

        final MeasurementReport mr = new MeasurementReport();
        mr.getMeasurementGroups().add(mg);
        mr.setMeasurementReportHeader(mrh);

        return mr;
    }

}
