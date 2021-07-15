/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.microgrids.presentation.ws;

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.microgrids.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.microgrids.application.mapping.MicrogridsMapper;
import org.opensmartgridplatform.adapter.ws.microgrids.application.services.MicrogridsService;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.GetDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.common.OsgpResultType;
import org.opensmartgridplatform.domain.microgrids.valueobjects.EmptyResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AdHocManagementEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementEndpoint.class);
  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/microgrids/adhocmanagement/2016/06";

  @Autowired private MicrogridsService service;

  @Autowired private MicrogridsMapper mapper;

  // === GET DATA ===

  @PayloadRoot(localPart = "GetDataRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDataAsyncResponse getData(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDataRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Data Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetDataAsyncResponse response = new GetDataAsyncResponse();

    try {
      final org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest dataRequest =
          this.mapper.map(
              request,
              org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest.class);
      final String correlationUid =
          this.service.enqueueGetDataRequest(
              organisationIdentification, request.getDeviceIdentification(), dataRequest);

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetDataAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDataResponse getGetDataResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDataAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Data Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    GetDataResponse response = new GetDataResponse();

    try {

      final org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataResponse dataResponse =
          this.service.dequeueGetDataResponse(request.getAsyncRequest().getCorrelationUid());
      if (dataResponse != null) {
        response = this.mapper.map(dataResponse, GetDataResponse.class);
        response.setResult(OsgpResultType.OK);

      } else {
        response.setResult(OsgpResultType.NOT_FOUND);
      }

    } catch (final ResponseNotFoundException e) {
      LOGGER.warn("ResponseNotFoundException for getGetDataResponse", e);
      response.setResult(OsgpResultType.NOT_FOUND);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  // === SET DATA ===

  @PayloadRoot(localPart = "SetDataRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetDataAsyncResponse setData(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDataRequest request)
      throws OsgpException {

    LOGGER.info(
        "Set Data Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final SetDataAsyncResponse response = new SetDataAsyncResponse();

    try {
      final org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest setDataRequest =
          this.mapper.map(
              request,
              org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest.class);
      final String correlationUid =
          this.service.enqueueSetDataRequest(
              organisationIdentification, request.getDeviceIdentification(), setDataRequest);

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "SetDataAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetDataResponse getSetDataResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDataAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Set Data Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetDataResponse response = new SetDataResponse();

    try {
      final EmptyResponse setDataResponse =
          this.service.dequeueSetDataResponse(request.getAsyncRequest().getCorrelationUid());
      if (setDataResponse != null) {
        response.setResult(OsgpResultType.OK);
      } else {
        response.setResult(OsgpResultType.NOT_FOUND);
      }
    } catch (final ResponseNotFoundException e) {
      LOGGER.warn("ResponseNotFoundException for getSetDataResponse", e);
      response.setResult(OsgpResultType.NOT_FOUND);
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  private void handleException(final Exception e) throws OsgpException {
    // Rethrow exception if it already is a functional or technical
    // exception, otherwise throw new technical exception.
    LOGGER.error("Exception occurred: ", e);
    if (e instanceof OsgpException) {
      throw (OsgpException) e;
    } else {
      throw new TechnicalException(ComponentType.WS_MICROGRIDS, e);
    }
  }
}
