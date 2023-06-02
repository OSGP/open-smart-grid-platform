//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetDeviceModelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetDeviceModelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetDeviceModelRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetDeviceModelResponse;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AdHocManagementEndpoint extends GenericDistributionAutomationEndPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementEndpoint.class);

  @PayloadRoot(localPart = "GetDeviceModelRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDeviceModelAsyncResponse getDeviceModel(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDeviceModelRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Device Model Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());
    final GetDeviceModelAsyncResponse response = new GetDeviceModelAsyncResponse();
    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest
          getDeviceModelRequest =
              this.mapper.map(
                  request,
                  org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest.class);
      final String correlationUid =
          this.service.enqueueGetDeviceModelRequest(
              organisationIdentification, request.getDeviceIdentification(), getDeviceModelRequest);
      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(LOGGER, e);
    }
    return response;
  }

  @PayloadRoot(localPart = "GetDeviceModelAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDeviceModelResponse getDeviceModelResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDeviceModelAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Device Model Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    GetDeviceModelResponse response = new GetDeviceModelResponse();
    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelResponse dataResponse =
          this.service.dequeueGetDeviceModelResponse(request.getAsyncRequest().getCorrelationUid());
      if (dataResponse != null) {
        response = this.mapper.map(dataResponse, GetDeviceModelResponse.class);
        response.setResult(OsgpResultType.OK);
      } else {

        response.setResult(OsgpResultType.NOT_FOUND);
      }
    } catch (final ResponseNotFoundException e) {
      LOGGER.warn("ResponseNotFoundException for getDeviceModel", e);
      response.setResult(OsgpResultType.NOT_FOUND);
    } catch (final Exception e) {
      this.handleException(LOGGER, e);
    }
    response.setDeviceIdentification(request.getAsyncRequest().getDeviceId());
    return response;
  }
}
