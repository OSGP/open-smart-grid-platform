/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.endpoints;

import org.opensmartgridplatform.adapter.ws.core.application.services.AdHocManagementService;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint(value = "coreAdHocManagementEndpoint")
public class AdHocManagementEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementEndpoint.class);
  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/adhocmanagement/2014/10";
  private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

  private final AdHocManagementService adHocManagementService;

  @Autowired
  public AdHocManagementEndpoint(
      @Qualifier(value = "wsCoreAdHocManagementService")
          final AdHocManagementService adHocManagementService) {
    this.adHocManagementService = adHocManagementService;
  }

  // === SET REBOOT ===

  @PayloadRoot(localPart = "SetRebootRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetRebootAsyncResponse setReboot(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetRebootRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set Reboot received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SetRebootAsyncResponse response = new SetRebootAsyncResponse();

    try {
      final String correlationUid =
          this.adHocManagementService.enqueueSetRebootRequest(
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

  @PayloadRoot(localPart = "SetRebootAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetRebootResponse getSetRebootResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetRebootAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Set Reboot Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetRebootResponse response = new SetRebootResponse();

    try {
      final ResponseMessage message =
          this.adHocManagementService.dequeueSetRebootResponse(
              request.getAsyncRequest().getCorrelationUid());
      if (message != null) {
        response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
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
    if (e instanceof OsgpException) {
      LOGGER.error("Exception occurred: ", e);
      throw (OsgpException) e;
    } else {
      LOGGER.error("Exception occurred: ", e);
      throw new TechnicalException(COMPONENT_WS_CORE, e);
    }
  }
}
