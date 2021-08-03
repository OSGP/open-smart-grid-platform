/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessageMetadata;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ActionMapperResponseService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ActionMapperService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.BundleService;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringBundleEndpoint extends SmartMeteringEndpoint {

  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-bundle/2014/10";

  private final BundleService bundleService;
  private final ActionMapperService actionMapperService;
  private final ActionMapperResponseService actionMapperResponseService;

  @Autowired
  public SmartMeteringBundleEndpoint(
      @Qualifier(value = "wsSmartMeteringBundleService") final BundleService bundleService,
      @Qualifier(value = "wsSmartMeteringActionMapperService")
          final ActionMapperService actionMapperService,
      @Qualifier(value = "wsSmartMeteringActionResponseMapperService")
          final ActionMapperResponseService actionMapperResponseService) {
    this.bundleService = bundleService;
    this.actionMapperService = actionMapperService;
    this.actionMapperResponseService = actionMapperResponseService;
  }

  @PayloadRoot(localPart = "BundleRequest", namespace = NAMESPACE)
  @ResponsePayload
  public BundleAsyncResponse bundleRequest(
      @ResponseUrl final String responseUrl,
      @MessageMetadata
          final org.opensmartgridplatform.shared.infra.jms.MessageMetadata messageMetadata,
      @RequestPayload final BundleRequest request)
      throws OsgpException {

    final String organisationIdentification = messageMetadata.getOrganisationIdentification();
    final String deviceIdentification = request.getDeviceIdentification();

    log.info(
        "Incoming BundleRequest with responseUrl {} and {} actions. [deviceId={} | organisationId={}]",
        responseUrl,
        request.getActions().getActionList().size(),
        deviceIdentification,
        organisationIdentification);

    BundleAsyncResponse response = null;
    try {
      response = new BundleAsyncResponse();

      final List<ActionRequest> actionRequestList =
          this.actionMapperService.mapAllActions(request.getActions().getActionList());

      final String correlationUid =
          this.bundleService.enqueueBundleRequest(
              messageMetadata
                  .builder()
                  .withDeviceIdentification(deviceIdentification)
                  .withMessageType(MessageType.HANDLE_BUNDLED_ACTIONS.name())
                  .build(),
              actionRequestList);

      log.info(
          "BundleRequest placed on queue [correlationId={} | deviceId={} | organisationId={}]",
          correlationUid,
          deviceIdentification,
          organisationIdentification);

      response.setCorrelationUid(correlationUid);
      response.setDeviceIdentification(request.getDeviceIdentification());

      this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "BundleAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public BundleResponse getBundleResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final BundleAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming call to retrieve BundleResponse [correlationId={} | deviceId={} | organisation={}]",
        request.getCorrelationUid(),
        request.getDeviceIdentification(),
        organisationIdentification);

    final ResponseData responseData =
        this.responseDataService.dequeue(
            request.getCorrelationUid(),
            BundleMessagesResponse.class,
            ComponentType.WS_SMART_METERING);

    this.throwExceptionIfResultNotOk(responseData, "get bundle response");

    // Create response.
    return this.actionMapperResponseService.mapAllActions(responseData.getMessageData());
  }
}
