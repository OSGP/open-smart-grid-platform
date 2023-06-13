// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.tariffswitching.endpoints;

import javax.validation.ConstraintViolationException;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.DevicePage;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.tariffswitching.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.adapter.ws.tariffswitching.application.services.AdHocManagementService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TariffSwitchingAdHocManagementEndpoint {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TariffSwitchingAdHocManagementEndpoint.class);
  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/tariffswitching/adhocmanagement/2014/10";
  private static final ComponentType COMPONENT_WS_TARIFF_SWITCHING =
      ComponentType.WS_TARIFF_SWITCHING;

  private final AdHocManagementService adHocManagementService;
  private final AdHocManagementMapper adHocManagementMapper;

  @Autowired
  public TariffSwitchingAdHocManagementEndpoint(
      @Qualifier(value = "wsTariffSwitchingAdHocManagementService")
          final AdHocManagementService adHocManagementService,
      @Qualifier(value = "tariffSwitchingAdhocManagementMapper")
          final AdHocManagementMapper adHocManagementMapper) {
    this.adHocManagementService = adHocManagementService;
    this.adHocManagementMapper = adHocManagementMapper;
  }

  @PayloadRoot(localPart = "GetDevicesRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetDevicesResponse getDevices(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetDevicesRequest request)
      throws OsgpException {

    LOGGER.info("Get Devices Request received from organisation: {}.", organisationIdentification);

    final GetDevicesResponse response = new GetDevicesResponse();

    try {
      final Page<Device> page =
          this.adHocManagementService.findAllDevices(organisationIdentification, request.getPage());

      final DevicePage devicePage = new DevicePage();
      devicePage.setTotalPages(page.getTotalPages());
      devicePage
          .getDevices()
          .addAll(
              this.adHocManagementMapper.mapAsList(
                  page.getContent(),
                  org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.Device
                      .class));
      response.setDevicePage(devicePage);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_WS_TARIFF_SWITCHING,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  // === GET STATUS ===

  @PayloadRoot(localPart = "GetStatusRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetStatusAsyncResponse getStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetStatusRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Get Status received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final GetStatusAsyncResponse response = new GetStatusAsyncResponse();

    try {
      final String correlationUid =
          this.adHocManagementService.enqueueGetTariffStatusRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetStatusAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetStatusResponse getGetStatusResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetStatusAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Status Response received from organisation: {} for correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final GetStatusResponse response = new GetStatusResponse();

    try {
      final ResponseMessage message =
          this.adHocManagementService.dequeueGetTariffStatusResponse(
              request.getAsyncRequest().getCorrelationUid());
      if (message != null) {
        response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
        final DeviceStatusMapped deviceStatus = (DeviceStatusMapped) message.getDataObject();
        if (deviceStatus != null) {
          response.setDeviceStatus(
              this.adHocManagementMapper.map(
                  deviceStatus,
                  org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                      .DeviceStatus.class));
        }
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  private void handleException(final Exception e) throws OsgpException {
    // Rethrow exception if it already is a functional or technical
    // exception,
    // otherwise throw new technical exception.
    LOGGER.error("Exception occurred: ", e);
    if (e instanceof OsgpException) {
      throw (OsgpException) e;
    } else {
      throw new TechnicalException(COMPONENT_WS_TARIFF_SWITCHING, e);
    }
  }
}
