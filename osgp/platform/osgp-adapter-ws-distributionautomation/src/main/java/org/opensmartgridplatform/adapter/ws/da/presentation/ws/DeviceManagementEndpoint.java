// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.AddRtuDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.AddRtuDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusResponse;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.RtuDevice;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class DeviceManagementEndpoint extends GenericDistributionAutomationEndPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementEndpoint.class);
  protected static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/distributionautomation/defs/2017/04";

  @PayloadRoot(localPart = "GetHealthStatusRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetHealthStatusAsyncResponse getHealthStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetHealthStatusRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Health Status Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetHealthStatusAsyncResponse response = new GetHealthStatusAsyncResponse();

    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest
          getHealthStatusRequest =
              this.mapper.map(
                  request,
                  org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest.class);
      final String correlationUid =
          this.service.enqueueGetHealthStatusRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              getHealthStatusRequest);
      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(LOGGER, e);
    }
    return response;
  }

  @PayloadRoot(localPart = "GetHealthStatusAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetHealthStatusResponse getHealthStatusResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetHealthStatusAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Health Status Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    GetHealthStatusResponse response = new GetHealthStatusResponse();
    try {
      final org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse
          healthStatusResponse =
              this.service.dequeueGetHealthResponse(request.getAsyncRequest().getCorrelationUid());
      if (healthStatusResponse != null) {
        response = this.mapper.map(healthStatusResponse, GetHealthStatusResponse.class);
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

  @PayloadRoot(localPart = "AddRtuDeviceRequest", namespace = NAMESPACE)
  @ResponsePayload
  public AddRtuDeviceAsyncResponse addRtuDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final AddRtuDeviceRequest request)
      throws OsgpException {

    LOGGER.info(
        "Incoming AddRtuDeviceRequest for device: {}.",
        request.getRtuDevice().getDeviceIdentification());

    AddRtuDeviceAsyncResponse response = null;
    try {
      response = new AddRtuDeviceAsyncResponse();
      final RtuDevice rtuDevice = this.mapper.map(request.getRtuDevice(), RtuDevice.class);
      final DeviceModel deviceModel =
          new DeviceModel(
              request.getDeviceModel().getManufacturer(),
              request.getDeviceModel().getModelCode(),
              "");
      final String correlationUid =
          this.service.enqueueAddRtuDeviceRequest(
              organisationIdentification,
              rtuDevice.getDeviceIdentification(),
              new org.opensmartgridplatform.domain.core.valueobjects.AddRtuDeviceRequest(
                  rtuDevice, deviceModel));
      response.setCorrelationUid(correlationUid);
      response.setDeviceId(request.getRtuDevice().getDeviceIdentification());
    } catch (final Exception e) {
      this.handleException(LOGGER, e);
    }
    return response;
  }
}
