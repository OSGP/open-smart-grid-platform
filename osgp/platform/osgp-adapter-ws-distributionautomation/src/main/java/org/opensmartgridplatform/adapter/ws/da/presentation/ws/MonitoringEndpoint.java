//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReport;
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
  public GetPQValuesAsyncResponse getPQValues(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetPQValuesRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get PQ Values Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetPQValuesAsyncResponse response = new GetPQValuesAsyncResponse();

    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest getPQValue =
          this.mapper.map(
              request, org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest.class);
      final String correlationUid =
          this.service.enqueueGetPQValuesRequest(
              organisationIdentification, request.getDeviceIdentification(), getPQValue);
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
  public GetPQValuesResponse getPQValuesAsync(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetPQValuesAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get PQ Values Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    GetPQValuesResponse response = new GetPQValuesResponse();
    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse dataResponse =
          this.service.dequeueGetPQValuesResponse(request.getAsyncRequest().getCorrelationUid());
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
      @RequestPayload final GetPQValuesPeriodicRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get PQ Values Periodic Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetPQValuesPeriodicAsyncResponse response = new GetPQValuesPeriodicAsyncResponse();

    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest
          getPQValuesPeriodicRequest =
              this.mapper.map(
                  request,
                  org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest
                      .class);
      final String correlationUid =
          this.service.enqueueGetPQValuesPeriodicRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              getPQValuesPeriodicRequest);
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
      @RequestPayload final GetPQValuesPeriodicAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get PQ Values Periodic Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    GetPQValuesPeriodicResponse response = new GetPQValuesPeriodicResponse();
    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse dataResponse =
          this.service.dequeueGetPQValuesPeriodicResponse(
              request.getAsyncRequest().getCorrelationUid());
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
      @RequestPayload final GetMeasurementReportRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Measurement Report Request received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getCorrelationUid());

    final GetMeasurementReportResponse response = new GetMeasurementReportResponse();
    try {
      final org.opensmartgridplatform.domain.da.measurements.MeasurementReport dataResponse =
          this.service.dequeueMeasurementReport(request.getCorrelationUid());
      if (dataResponse != null) {
        final MeasurementReport report = this.mapper.map(dataResponse, MeasurementReport.class);
        response.setMeasurementReport(report);
        response.setResult(OsgpResultType.OK);
      } else {
        response.setResult(OsgpResultType.NOT_FOUND);
      }
    } catch (final ResponseNotFoundException e) {
      LOGGER.warn("ResponseNotFoundException for getMeasurementReport", e);
      response.setResult(OsgpResultType.NOT_FOUND);
    } catch (final Exception e) {
      this.handleException(LOGGER, e);
    }

    return response;
  }
}
